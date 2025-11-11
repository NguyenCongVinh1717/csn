package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.entity.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);

    boolean existsByClassSubjectTeacher_Subject_Id(Long id);

    boolean existsByStudent_Id(Long id);

    boolean existsByClassSubjectTeacher_Id(Long id);

    Optional<Enrollment> findByStudent_IdAndClassSubjectTeacher_Id(Long studentId, Long cstId);

    List<Enrollment> findAllByClassSubjectTeacher_Subject_Id(Long subjectId);

    boolean existsByStudent_IdAndClassSubjectTeacher_Id(Long studentId, Long cstId);

    List<Enrollment> findByStudent_SchoolClass_IdAndClassSubjectTeacher_Subject_Id(Long classId, Long subjectId);

    List<Enrollment> findAllByClassSubjectTeacherId(Long cstId);
}