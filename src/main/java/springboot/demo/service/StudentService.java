package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springboot.demo.dto.ChangePasswordRequest;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.dto.StudentDTO;
import springboot.demo.entity.*;
import springboot.demo.mapper.EnrollmentMapper;
import springboot.demo.mapper.StudentMapper;
import springboot.demo.repository.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final UserRepository userRepo;
    private final ClassRepository classRepo;
    private final PasswordEncoder passwordEncoder;
    private final ClassSubjectTeacherRepository cstRepo;

    public List<StudentDTO> findAll() {
        return studentRepo.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    public StudentDTO findById(Long id) {
        Student s = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return StudentMapper.toDto(s);
    }

    //find by class
    public List<StudentDTO> findByClass(Long classID){
        return studentRepo.findBySchoolClassId(classID)
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    //find student with name
    public List<StudentDTO> findStudentByName(String name){
        return studentRepo.findStudentByFullNameContainingIgnoreCase(name)
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    // ------------------------ CREATE WITH ACCOUNT ------------------------
    @Transactional
    public CreateResult createWithAccount(StudentDTO dto) {
        // check class exist
        SchoolClass sc = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Class not found id=" + dto.getClassId()));

        //  check dupicate studentCode
        if (studentRepo.existsByStudentCode(dto.getStudentCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student code already exists");
        }

        // tranform entity and save
        Student s = StudentMapper.toEntity(dto);
        s.setSchoolClass(sc);
        Student saved = studentRepo.save(s);

        //  If that class has subjects, enroll that subjects for student
        List<ClassSubjectTeacher> classSubjects = cstRepo.findBySchoolClassId(dto.getClassId());
        if (!classSubjects.isEmpty()) {
            List<Enrollment> enrollments = new ArrayList<>();
            for (ClassSubjectTeacher cst : classSubjects) {
                enrollments.add(Enrollment.builder()
                        .student(saved)
                        .classSubjectTeacher(cst)
                        .grade(null)
                        .build());
            }
            enrollmentRepo.saveAll(enrollments);
        }

        // Create account
        String username = saved.getStudentCode();
        String initialPassword = null;

        if (username != null && !username.isBlank() && !userRepo.existsByUsername(username)) {
            initialPassword = "stud123";
            AppUser u = AppUser.builder()
                    .username(username)
                    .password(passwordEncoder.encode(initialPassword))
                    .role("STUDENT")
                    .studentId(saved.getId())
                    .build();
            userRepo.save(u);
        }

        return new CreateResult(StudentMapper.toDto(saved), initialPassword);
    }


    // ------------------------ UPDATE ------------------------
    @Transactional
    public StudentDTO update(Long id, StudentDTO dto) {
        Student exist = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // check dupicate studentCode
        if (!exist.getStudentCode().equals(dto.getStudentCode())
                && studentRepo.existsByStudentCode(dto.getStudentCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student code already exists");
        }

        // check class exist
        SchoolClass sc = classRepo.findById(dto.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Class not found id=" + dto.getClassId()));

        //No updating class if having enrollments
        Long oldClassId = exist.getSchoolClass() != null ? exist.getSchoolClass().getId() : null;
        Long newClassId = dto.getClassId();

        boolean classChanged;
        if (oldClassId == null) {
            classChanged = newClassId != null;
        } else {
            classChanged = !oldClassId.equals(newClassId);
        }

        if (classChanged) {
            if (enrollmentRepo.existsByStudent_Id(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot change class because student has enrollments");
            }
        }

        //If changing studentCode, also change in account
        if (!exist.getStudentCode().equals(dto.getStudentCode())) {
            // check dupicate
            if (userRepo.existsByUsername(dto.getStudentCode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Account username already exists");
            }

            userRepo.findByStudentId(id).ifPresent(user -> {
                user.setUsername(dto.getStudentCode());
                userRepo.save(user);
            });
        }

        // update
        exist.setStudentCode(dto.getStudentCode());
        exist.setFullName(dto.getFullName());
        exist.setDob(dto.getDob());
        exist.setGender(dto.getGender());
        exist.setEmail(dto.getEmail());
        exist.setPhone(dto.getPhone());
        exist.setSchoolClass(sc);

        Student updated = studentRepo.save(exist);
        return StudentMapper.toDto(updated);
    }


    // ------------------------ DELETE ------------------------
    @Transactional
    public void delete(Long id) {
        // check exist
        Student sv = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found id=" + id));

        //if having enrollmemts, throw error
        if (enrollmentRepo.existsByStudent_Id(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete student because student has enrollments");
        }

        // delete account
        userRepo.findByStudentId(id).ifPresent(userRepo::delete);

        // delete student
        studentRepo.delete(sv);
    }


    // ------------------------ ENROLLMENTS ------------------------
    public List<EnrollmentDTO> getEnrollments(Long studentId) {
        studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        List<Enrollment> ens = enrollmentRepo.findByStudentId(studentId);
        return ens.stream()
                .map(EnrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    //count students
    public long countStudents() {
        return studentRepo.count();
    }

    // ------------------------ BATCH CREATE ------------------------
    public int createBatchWithAccount(List<StudentDTO> dtos) {
        int created = 0;
        for (StudentDTO dto : dtos) {
            if (dto == null || dto.getStudentCode() == null || dto.getFullName() == null || dto.getClassId() == null)
                continue;

            if (studentRepo.existsByStudentCode(dto.getStudentCode()))
                continue;

            SchoolClass sc = classRepo.findById(dto.getClassId()).orElse(null);
            if (sc == null) continue;

            Student s = StudentMapper.toEntity(dto);
            s.setSchoolClass(sc);
            Student saved = studentRepo.save(s);

            if (!userRepo.existsByUsername(saved.getStudentCode())) {
                AppUser u = new AppUser();
                u.setUsername(saved.getStudentCode());
                u.setPassword(passwordEncoder.encode("stud123"));
                u.setRole("STUDENT");
                u.setStudentId(saved.getId());
                userRepo.save(u);
            }
            created++;
        }
        return created;
    }

    // ------------------------ EXPORT TO EXCEL ------------------------
    public ByteArrayResource exportStudentsToExcel() throws IOException {
        List<Student> students = studentRepo.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student Code");
        header.createCell(1).setCellValue("Full Name");
        header.createCell(2).setCellValue("DOB");
        header.createCell(3).setCellValue("Gender");
        header.createCell(4).setCellValue("Email");
        header.createCell(5).setCellValue("Phone");
        header.createCell(6).setCellValue("Class Code");
        header.createCell(7).setCellValue("Class Name");
        header.createCell(8).setCellValue("Grade");

        int rowIdx = 1;
        for (Student s : students) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getStudentCode() != null ? s.getStudentCode() : "");
            row.createCell(1).setCellValue(s.getFullName() != null ? s.getFullName() : "");
            row.createCell(2).setCellValue(s.getDob() != null ? s.getDob().toString() : "");
            row.createCell(3).setCellValue(s.getGender() != null ? s.getGender() : "");
            row.createCell(4).setCellValue(s.getEmail() != null ? s.getEmail() : "");
            row.createCell(5).setCellValue(s.getPhone() != null ? s.getPhone() : "");
            if (s.getSchoolClass() != null) {
                row.createCell(6).setCellValue(s.getSchoolClass().getClassCode() != null ? s.getSchoolClass().getClassCode() : "");
                row.createCell(7).setCellValue(s.getSchoolClass().getName() != null ? s.getSchoolClass().getName() : "");
                row.createCell(8).setCellValue(s.getSchoolClass().getGrade() != null ? s.getSchoolClass().getGrade().getName() : "");
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayResource(out.toByteArray());
    }

    // ------------------------ CHANGE PASSWORD ------------------------
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        String username = auth.getName();

        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from old password");

        if (!req.getNewPassword().equals(req.getConfirmPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    // ------------------------ INNER CLASS ------------------------
    public static class CreateResult {
        private final StudentDTO student;
        private final String initialPassword;

        public CreateResult(StudentDTO student, String initialPassword) {
            this.student = student;
            this.initialPassword = initialPassword;
        }

        public StudentDTO getStudent() { return student; }
        public String getInitialPassword() { return initialPassword; }
    }
}
