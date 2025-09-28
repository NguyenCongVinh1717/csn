package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.entity.Enrollment;
import springboot.demo.entity.Student;
import springboot.demo.entity.Subject;
import springboot.demo.mapper.EnrollmentMapper;
import springboot.demo.repository.EnrollmentRepository;
import springboot.demo.repository.StudentRepository;
import springboot.demo.repository.SubjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService{
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;
    private final SubjectRepository subjectRepo;


    public EnrollmentDTO enroll(Long studentId, Long subjectId) {
        Student s = studentRepo.findById(studentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));
        Subject sub = subjectRepo.findById(subjectId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
        Enrollment exist = enrollmentRepo.findByStudentIdAndSubjectId(studentId, subjectId);
        if (exist != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Student already enrolled");
        Enrollment e = Enrollment.builder().student(s).subject(sub).grade(null).build();
        Enrollment saved = enrollmentRepo.save(e);
        return EnrollmentMapper.toDto(saved);
    }


    public void unenroll(Long studentId, Long subjectId) {
        Enrollment e = enrollmentRepo.findByStudentIdAndSubjectId(studentId, subjectId);
        if (e == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Enrollment not found");
        enrollmentRepo.delete(e);
    }


    public EnrollmentDTO setGrade(Long studentId, Long subjectId, Double grade) {
        Enrollment e = enrollmentRepo.findByStudentIdAndSubjectId(studentId, subjectId);
        if (e == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Enrollment not found");
        e.setGrade(grade);
        Enrollment saved = enrollmentRepo.save(e);
        return EnrollmentMapper.toDto(saved);
    }


    public List<EnrollmentDTO> findByStudent(Long studentId) {
        return enrollmentRepo.findByStudentId(studentId).stream().map(EnrollmentMapper::toDto).collect(Collectors.toList());
    }


    public List<EnrollmentDTO> findBySubject(Long subjectId) {
        return enrollmentRepo.findBySubjectId(subjectId).stream().map(EnrollmentMapper::toDto).collect(Collectors.toList());
    }
}

