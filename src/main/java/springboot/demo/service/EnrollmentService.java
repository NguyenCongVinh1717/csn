package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.entity.*;
import springboot.demo.mapper.EnrollmentMapper;
import springboot.demo.repository.ClassSubjectTeacherRepository;
import springboot.demo.repository.EnrollmentRepository;
import springboot.demo.repository.StudentRepository;
import springboot.demo.repository.SubjectRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService{
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;
    private final ClassSubjectTeacherRepository cstRepo;


    @Transactional
    public EnrollmentDTO setGrade(Long studentId, Long cstId, Double grade) {
        Enrollment e = enrollmentRepo
                .findByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản đăng ký"));

        e.setGrade(grade);
        Enrollment saved = enrollmentRepo.save(e);
        return EnrollmentMapper.toDto(saved);
    }

    public EnrollmentDTO enroll(Long studentId, Long cstId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh"));
        ClassSubjectTeacher cst = cstRepo.findById(cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy CST"));

        if (!student.getSchoolClass().getId().equals(cst.getSchoolClass().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Môn học này không dành cho lớp của bạn");
        }

        if (enrollmentRepo.existsByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Học sinh đã đăng ký môn học này");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassSubjectTeacher(cst);

        return EnrollmentMapper.toDto(enrollmentRepo.save(enrollment));
    }

    public EnrollmentDTO unenroll(Long studentId, Long cstId) {
        Enrollment enrollment = enrollmentRepo.findByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bản đăng ký"));

        enrollmentRepo.delete(enrollment);
        return EnrollmentMapper.toDto(enrollment);
    }



    public List<EnrollmentDTO> findByStudent(Long studentId) {
        return enrollmentRepo.findByStudentId(studentId).stream().map(EnrollmentMapper::toDto).collect(Collectors.toList());
    }

    public List<EnrollmentDTO> findBySubject(Long subjectId) {
        return enrollmentRepo.findAllByClassSubjectTeacher_Subject_Id(subjectId)
                .stream()
                .map(EnrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    //getStudentsOfSubjectAndClass
    public List<EnrollmentDTO> findByClassAndSubject(Long classId, Long subjectId) {
        List<Enrollment> list = enrollmentRepo.findByStudent_SchoolClass_IdAndClassSubjectTeacher_Subject_Id(classId, subjectId);
        return list.stream()
                .map(EnrollmentMapper::toDto)
                .toList();
    }

    public ByteArrayResource exportCSTsToExcel(Long cstId) throws IOException {
        List<Enrollment> grades = enrollmentRepo.findAllByClassSubjectTeacherId(cstId);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Grades");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã HS");
        header.createCell(1).setCellValue("Họ tên");
        header.createCell(2).setCellValue("Điểm");
        int rowIdx = 1;
        for (Enrollment e : grades) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getStudent().getStudentCode() != null ? e.getStudent().getStudentCode() : "");
            row.createCell(1).setCellValue(e.getStudent().getFullName() != null ? e.getStudent().getFullName() : "");
            if (e.getGrade() != null) {
                row.createCell(2).setCellValue(e.getGrade().doubleValue());
            } else {
                row.createCell(2).setCellValue("");
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayResource(out.toByteArray());
    }
}

