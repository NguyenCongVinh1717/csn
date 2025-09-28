//package springboot.demo.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.http.HttpStatus;
//import springboot.demo.dto.EnrollmentDTO;
//import springboot.demo.dto.StudentDTO;
//import springboot.demo.entity.Enrollment;
//import springboot.demo.entity.Student;
//import springboot.demo.mapper.EnrollmentMapper;
//import springboot.demo.mapper.StudentMapper;
//import springboot.demo.repository.EnrollmentRepository;
//import springboot.demo.repository.StudentRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class StudentService{
//    private final StudentRepository studentRepo;
//    private final EnrollmentRepository enrollmentRepo;
//
//    public List<StudentDTO> findAll() {
//        return studentRepo.findAll().stream().map(StudentMapper::toDto).collect(Collectors.toList());
//    }
//
//
//    public StudentDTO findById(Long id) {
//        Student s = studentRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
//        return StudentMapper.toDto(s);
//    }
//
//
//    public StudentDTO create(StudentDTO dto) {
//        Student s = StudentMapper.toEntity(dto);
//        Student saved = studentRepo.save(s);
//        return StudentMapper.toDto(saved);
//    }
//
//
//    public StudentDTO update(Long id, StudentDTO dto) {
//        Student exist = studentRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
//        exist.setStudentCode(dto.getStudentCode());
//        exist.setFullName(dto.getFullName());
//        exist.setDob(dto.getDob());
//        exist.setGender(dto.getGender());
//        exist.setEmail(dto.getEmail());
//        exist.setPhone(dto.getPhone());
//        Student updated = studentRepo.save(exist);
//        return StudentMapper.toDto(updated);
//    }
//
//
//    public void delete(Long id) { studentRepo.deleteById(id); }
//
//    public List<EnrollmentDTO> getEnrollments(Long studentId) {
//        studentRepo.findById(studentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
//        List<Enrollment> ens = enrollmentRepo.findByStudentId(studentId);
//        return ens.stream().map(EnrollmentMapper::toDto).collect(Collectors.toList());
//    }
//}
//
package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.ChangePasswordRequest;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.dto.StudentDTO;
import springboot.demo.entity.Enrollment;
import springboot.demo.entity.Student;
import springboot.demo.entity.AppUser;
import springboot.demo.mapper.EnrollmentMapper;
import springboot.demo.mapper.StudentMapper;
import springboot.demo.repository.EnrollmentRepository;
import springboot.demo.repository.StudentRepository;
import springboot.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDTO> findAll() {
        return studentRepo.findAll().stream().map(StudentMapper::toDto).collect(Collectors.toList());
    }

    public StudentDTO findById(Long id) {
        Student s = studentRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
        return StudentMapper.toDto(s);
    }


//    public StudentDTO create(StudentDTO dto) {
//        Student s = StudentMapper.toEntity(dto);
//        Student saved = studentRepo.save(s);
//        return StudentMapper.toDto(saved);
//    }


    @Transactional
    public CreateResult createWithAccount(StudentDTO dto) {
        Student s = StudentMapper.toEntity(dto);

        if (studentRepo.existsByStudentCode(dto.getStudentCode())) {
            throw new RuntimeException("Student code already exists");
        }

        Student saved = studentRepo.save(s); // lưu student trước để có id
        String username = saved.getStudentCode();
        String initialPassword = null;

        if (username != null && !username.isBlank()) {
            boolean exists = userRepo.existsByUsername(username);
            if (!exists) {
                // mật khẩu mặc định cho student
                initialPassword = "stud123";
                AppUser u = AppUser.builder()
                        .username(username)
                        .password(passwordEncoder.encode(initialPassword)) // lưu hash
                        .role("STUDENT")
                        .studentId(saved.getId())
                        .build();
                userRepo.save(u);
            }
            // nếu exists = true -> không ghi đè tài khoản, trả initialPassword = null
        }

        return new CreateResult(StudentMapper.toDto(saved), initialPassword);
    }

    public StudentDTO update(Long id, StudentDTO dto) {
        Student exist = studentRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
        if (!exist.getStudentCode().equals(dto.getStudentCode())
                && studentRepo.existsByStudentCode(dto.getStudentCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student code already exists");
        }
        exist.setStudentCode(dto.getStudentCode());
        exist.setFullName(dto.getFullName());
        exist.setDob(dto.getDob());
        exist.setGender(dto.getGender());
        exist.setEmail(dto.getEmail());
        exist.setPhone(dto.getPhone());
        Student updated = studentRepo.save(exist);
        return StudentMapper.toDto(updated);
    }

//    public StudentDTO update(Long id, StudentDTO dto) {
//
//        Student exist = studentRepo.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
//
//        String newCode = dto.getStudentCode();
//
//        if (newCode != null) {
//            boolean codeExists = studentRepo.existsByStudentCode(newCode);
//            if (codeExists) {
//                if (!newCode.equals(exist.getStudentCode())) {
//                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Student code already exists");
//                }
//            }
//        }
//
//        exist.setStudentCode(dto.getStudentCode());
//        exist.setFullName(dto.getFullName());
//        exist.setDob(dto.getDob());
//        exist.setGender(dto.getGender());
//        exist.setEmail(dto.getEmail());
//        exist.setPhone(dto.getPhone());
//
//        Student updated = studentRepo.save(exist);
//        return StudentMapper.toDto(updated);
//    }


    public void delete(Long id) {
        Student sv = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found id=" + id));

        userRepo.findByStudentId(id).ifPresent(userRepo::delete);

        studentRepo.delete(sv);
    }

    public List<EnrollmentDTO> getEnrollments(Long studentId) {
        studentRepo.findById(studentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
        List<Enrollment> ens = enrollmentRepo.findByStudentId(studentId);
        return ens.stream().map(EnrollmentMapper::toDto).collect(Collectors.toList());
    }

    public int createBatchWithAccount(List<StudentDTO> dtos) {
        int count = 0;
        for (StudentDTO dto : dtos) {
            if (dto.getStudentCode() == null || dto.getStudentCode().isBlank()
                    || dto.getFullName() == null || dto.getFullName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentCode and fullName required");
            }
            if (studentRepo.existsByStudentCode(dto.getStudentCode())) {
                //skip hoặc throw
                continue;
            }
            Student s = StudentMapper.toEntity(dto);
            Student saved = studentRepo.save(s);

            if (!userRepo.existsByUsername(saved.getStudentCode())) {
                AppUser u = new AppUser();
                u.setUsername(saved.getStudentCode());
                u.setPassword(passwordEncoder.encode("stud123")); // default password
                u.setRole("STUDENT"); // or "ROLE_STUDENT" depending on your model
                u.setStudentId(saved.getId());
                userRepo.save(u);
            }
            count++;
        }
        return count;
    }

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

        int rowIdx = 1;
        for (Student s : students) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getStudentCode());
            row.createCell(1).setCellValue(s.getFullName());
            row.createCell(2).setCellValue(s.getDob() != null ? s.getDob().toString() : "");
            row.createCell(3).setCellValue(s.getGender() != null ? s.getGender() : "");
            row.createCell(4).setCellValue(s.getEmail() != null ? s.getEmail() : "");
            row.createCell(5).setCellValue(s.getPhone() != null ? s.getPhone() : "");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayResource(out.toByteArray());
    }
    @Transactional
    public void changePassword(ChangePasswordRequest req) {

        if (req.getNewPassword() == null || req.getNewPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 8 characters");
        }
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
        }
        if (req.getOldPassword() == null || req.getOldPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is required");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String username = auth.getName();


        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from old password");
        }


        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);

    }



    public static class CreateResult {
        private final StudentDTO student;
        private final String initialPassword; // null nếu không tạo user mới

        public CreateResult(StudentDTO student, String initialPassword) {
            this.student = student;
            this.initialPassword = initialPassword;
        }

        public StudentDTO getStudent() { return student; }
        public String getInitialPassword() { return initialPassword; }
    }
}

