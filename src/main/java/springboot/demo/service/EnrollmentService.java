package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.entity.ClassSubjectTeacher;
import springboot.demo.entity.Enrollment;
import springboot.demo.entity.Student;
import springboot.demo.entity.Subject;
import springboot.demo.mapper.EnrollmentMapper;
import springboot.demo.repository.ClassSubjectTeacherRepository;
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
    private final ClassSubjectTeacherRepository cstRepo;


    @Transactional
    public EnrollmentDTO setGrade(Long studentId, Long cstId, Double grade) {
        Enrollment e = enrollmentRepo
                .findByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        e.setGrade(grade);
        Enrollment saved = enrollmentRepo.save(e);
        return EnrollmentMapper.toDto(saved);
    }

    public EnrollmentDTO enroll(Long studentId, Long cstId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        ClassSubjectTeacher cst = cstRepo.findById(cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CST not found"));

        if (!student.getSchoolClass().getId().equals(cst.getSchoolClass().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This subject is not for your class");
        }

        if (enrollmentRepo.existsByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already enrolled");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassSubjectTeacher(cst);

        return EnrollmentMapper.toDto(enrollmentRepo.save(enrollment));
    }

    public EnrollmentDTO unenroll(Long studentId, Long cstId) {
        Enrollment enrollment = enrollmentRepo.findByStudent_IdAndClassSubjectTeacher_Id(studentId, cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

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

}

