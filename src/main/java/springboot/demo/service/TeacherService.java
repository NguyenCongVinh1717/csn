//package springboot.demo.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.http.HttpStatus;
//import springboot.demo.dto.SubjectDTO;
//import springboot.demo.dto.TeacherDTO;
//import springboot.demo.entity.Teacher;
//import springboot.demo.mapper.SubjectMapper;
//import springboot.demo.mapper.TeacherMapper;
//import springboot.demo.repository.SubjectRepository;
//import springboot.demo.repository.TeacherRepository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class TeacherService{
//    private final TeacherRepository teacherRepo;
//    private final SubjectRepository subjectRepo;
//
//
//    public List<TeacherDTO> findAll() {
//        return teacherRepo.findAll().stream().map(TeacherMapper::toDto).collect(Collectors.toList());
//    }
//
//
//    public TeacherDTO findById(Long id) {
//        Teacher t = teacherRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
//        return TeacherMapper.toDto(t);
//    }
//
//
//    public TeacherDTO create(TeacherDTO dto) {
//        Teacher t = TeacherMapper.toEntity(dto);
//        Teacher saved = teacherRepo.save(t);
//        return TeacherMapper.toDto(saved);
//    }
//
//
//    public TeacherDTO update(Long id, TeacherDTO dto) {
//        Teacher exist = teacherRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
//        exist.setTeacherCode(dto.getTeacherCode());
//        exist.setFullName(dto.getFullName());
//        exist.setDob(dto.getDob());
//        exist.setGender(dto.getGender());
//        exist.setEmail(dto.getEmail());
//        exist.setPhone(dto.getPhone());
//        return TeacherMapper.toDto(teacherRepo.save(exist));
//    }
//
//
//    public void delete(Long id) { teacherRepo.deleteById(id); }
//
//
//    public List<SubjectDTO> getSubjects(Long teacherId) {
//        teacherRepo.findById(teacherId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
//        return subjectRepo.findByTeacherId(teacherId).stream().map(SubjectMapper::toDto).collect(Collectors.toList());
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springboot.demo.dto.ChangePasswordRequest;
import springboot.demo.dto.StudentDTO;
import springboot.demo.dto.SubjectDTO;
import springboot.demo.dto.TeacherDTO;
import springboot.demo.entity.AppUser;
import springboot.demo.entity.Student;
import springboot.demo.entity.Teacher;
import springboot.demo.mapper.StudentMapper;
import springboot.demo.mapper.SubjectMapper;
import springboot.demo.mapper.TeacherMapper;
import springboot.demo.repository.SubjectRepository;
import springboot.demo.repository.TeacherRepository;
import springboot.demo.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TeacherService: CRUD cho Teacher.
 * Thêm phương thức createWithAccount(...) để tạo Teacher + tạo AppUser với mật khẩu mặc định "teach123".
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {
    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDTO> findAll() {
        return teacherRepo.findAll().stream().map(TeacherMapper::toDto).collect(Collectors.toList());
    }

    public TeacherDTO findById(Long id) {
        Teacher t = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        return TeacherMapper.toDto(t);
    }


    public TeacherDTO create(TeacherDTO dto) {
        Teacher t = TeacherMapper.toEntity(dto);
        Teacher saved = teacherRepo.save(t);
        return TeacherMapper.toDto(saved);
    }


    @Transactional
    public CreateResult createWithAccount(TeacherDTO dto) {
        Teacher t = TeacherMapper.toEntity(dto);
        Teacher saved = teacherRepo.save(t);

        String username = saved.getTeacherCode();
        String initialPassword = null;

        if (username != null && !username.isBlank()) {
            boolean exists = userRepo.existsByUsername(username);
            if (!exists) {
                initialPassword = "teach123";
                AppUser u = AppUser.builder()
                        .username(username)
                        .password(passwordEncoder.encode(initialPassword))
                        .role("TEACHER")
                        .teacherId(saved.getId())
                        .build();
                userRepo.save(u);
            }
        }

        return new CreateResult(TeacherMapper.toDto(saved), initialPassword);
    }

    public TeacherDTO update(Long id, TeacherDTO dto) {
        Teacher exist = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        exist.setTeacherCode(dto.getTeacherCode());
        exist.setFullName(dto.getFullName());
        exist.setDob(dto.getDob());
        exist.setGender(dto.getGender());
        exist.setEmail(dto.getEmail());
        exist.setPhone(dto.getPhone());
        return TeacherMapper.toDto(teacherRepo.save(exist));
    }

    public void delete(Long id) {
        Teacher tc = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy student id=" + id));

        userRepo.findByTeacherId(id).ifPresent(userRepo::delete);

        teacherRepo.delete(tc);
    }
    public List<SubjectDTO> getSubjects(Long teacherId) {
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));
        return subjectRepo.findByTeacherId(teacherId).stream().map(SubjectMapper::toDto).collect(Collectors.toList());
    }

    public int createBatchWithAccount(List<TeacherDTO> dtos) {
        int count = 0;
        for (TeacherDTO dto : dtos) {
            if (dto.getTeacherCode() == null || dto.getTeacherCode().isBlank()
                    || dto.getFullName() == null || dto.getFullName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacherCode and fullName required");
            }
            if (teacherRepo.existsByTeacherCode(dto.getTeacherCode())) {
                continue;
            }
            Teacher t = TeacherMapper.toEntity(dto);
            Teacher saved = teacherRepo.save(t);

            if (!userRepo.existsByUsername(saved.getTeacherCode())) {
                AppUser u = new AppUser();
                u.setUsername(saved.getTeacherCode());
                u.setPassword(passwordEncoder.encode("teach123"));
                u.setRole("TEACHER");
                u.setTeacherId(saved.getId());
                userRepo.save(u);
            }
            count++;
        }
        return count;
    }

    public ByteArrayResource exportTeachersToExcel() throws IOException {
        List<Teacher> teachers = teacherRepo.findAll();

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
        for (Teacher s : teachers) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getTeacherCode());
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
        private final TeacherDTO teacher;
        private final String initialPassword;

        public CreateResult(TeacherDTO teacher, String initialPassword) {
            this.teacher = teacher;
            this.initialPassword = initialPassword;
        }

        public TeacherDTO getTeacher() { return teacher; }
        public String getInitialPassword() { return initialPassword; }
    }
}
