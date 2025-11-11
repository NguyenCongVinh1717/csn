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
import springboot.demo.dto.TeacherDTO;
import springboot.demo.entity.AppUser;
import springboot.demo.entity.Teacher;
import springboot.demo.mapper.TeacherMapper;
import springboot.demo.repository.SubjectRepository;
import springboot.demo.repository.ClassSubjectTeacherRepository;
import springboot.demo.repository.TeacherRepository;
import springboot.demo.repository.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepo;
    private final SubjectRepository subjectRepo;
    private final ClassSubjectTeacherRepository cstRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherDTO> findAll() {
        return teacherRepo.findAll().stream().map(TeacherMapper::toDto).collect(Collectors.toList());
    }

    public TeacherDTO findById(Long id) {
        Teacher t = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giáo viên id=" + id));
        return TeacherMapper.toDto(t);
    }

    //find teacher with name
    public List<TeacherDTO> findTeacherByName(String name){
        return teacherRepo.findTeacherByFullNameContainingIgnoreCase(name)
                .stream()
                .map(TeacherMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateResult createWithAccount(TeacherDTO dto) {
        // check dupicate
        if (teacherRepo.existsByTeacherCode(dto.getTeacherCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã giáo viên đã tồn tại");
        }

        // create teacher
        Teacher t = TeacherMapper.toEntity(dto);
        Teacher saved = teacherRepo.save(t);

        // create account
        String initialPassword = null;
        if (!userRepo.existsByUsername(dto.getTeacherCode())) {
            initialPassword = "teach123";
            AppUser u = AppUser.builder()
                    .username(dto.getTeacherCode())
                    .password(passwordEncoder.encode(initialPassword))
                    .role("TEACHER")
                    .teacherId(saved.getId())
                    .build();
            userRepo.save(u);
        }

        return new CreateResult(TeacherMapper.toDto(saved), initialPassword);
    }


    @Transactional
    public TeacherDTO update(Long id, TeacherDTO dto) {
        Teacher exist = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giáo viên"));

        String newCode = dto.getTeacherCode();

        // check dupicate
        if (!exist.getTeacherCode().equals(newCode) && teacherRepo.existsByTeacherCode(newCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã giáo viên đã tồn tại");
        }

        // if changing teacherCode, also change in account
        if (!exist.getTeacherCode().equals(newCode)) {
            userRepo.findByTeacherId(id).ifPresent(user -> {
                user.setUsername(newCode);
                userRepo.save(user);
            });
        }

        // update
        exist.setTeacherCode(newCode);
        exist.setFullName(dto.getFullName());
        exist.setDob(dto.getDob());
        exist.setGender(dto.getGender());
        exist.setEmail(dto.getEmail());
        exist.setPhone(dto.getPhone());

        Teacher updated = teacherRepo.save(exist);
        return TeacherMapper.toDto(updated);
    }


    @Transactional
    public void delete(Long id) {
        Teacher tc = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy giáo viên id=" + id));

        // if having in cst, throw
        boolean assigned = cstRepo.existsByTeacher_Id(id);
        if (assigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Không thể xoá giáo viên vì họ đang dạy, hãy huỷ gán trươc");
        }

        // delete account
        userRepo.findByTeacherId(id).ifPresent(userRepo::delete);

        //delete teacher
        teacherRepo.delete(tc);
    }

//
//    @Transactional(readOnly = true)
//    public List<SubjectDTO> getSubjects(Long teacherId) {
//        // Kiểm tra giáo viên tồn tại
//        if (!teacherRepo.existsById(teacherId)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found id=" + teacherId);
//        }
//
//        // Lấy danh sách môn distinct qua ClassSubjectTeacher
//        List<Subject> subjects = cstRepo.findByTeacher_IdOrderByDayOfWeekAscPeriodAsc(teacherId);
//        return subjects.stream()
//                .map(SubjectMapper::toDto)
//                .toList();
//    }


    //count teachers
    public long countTeachers(){
        return teacherRepo.count();
    }


    @Transactional
    public int createBatchWithAccount(List<TeacherDTO> dtos) {
        int count = 0;
        for (TeacherDTO dto : dtos) {
            if (teacherRepo.existsByTeacherCode(dto.getTeacherCode())){
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Mã giáo viên đã tồn tại: " + dto.getTeacherCode()
                );
            }

            Teacher t = TeacherMapper.toEntity(dto);
            Teacher saved = teacherRepo.save(t);

            if(userRepo.existsByUsername(saved.getTeacherCode())){
                throw new ResponseStatusException
                        (HttpStatus.CONFLICT,"Tài khoản giáo viên đã tồn tại: "+saved.getTeacherCode());
            }

                AppUser u = new AppUser();
                u.setUsername(saved.getTeacherCode());
                u.setPassword(passwordEncoder.encode("teach123"));
                u.setRole("TEACHER");
                u.setTeacherId(saved.getId());
                userRepo.save(u);
            count++;
        }
        return count;
    }

    public ByteArrayResource exportTeachersToExcel() throws IOException {
        List<Teacher> teachers = teacherRepo.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Teachers");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã giáo viên");
        header.createCell(1).setCellValue("Họ tên");
        header.createCell(2).setCellValue("Ngày sinh");
        header.createCell(3).setCellValue("Giới tính");
        header.createCell(4).setCellValue("Email");
        header.createCell(5).setCellValue("Số diện thoại");

        int rowIdx = 1;
        for (Teacher s : teachers) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s.getTeacherCode() != null ? s.getTeacherCode() : "");
            row.createCell(1).setCellValue(s.getFullName() != null ? s.getFullName() : "");
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String username = auth.getName();

        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không đúng");
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải khác mật khẩu cũ");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới và xác nhận mật khẩu không trùng");
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
