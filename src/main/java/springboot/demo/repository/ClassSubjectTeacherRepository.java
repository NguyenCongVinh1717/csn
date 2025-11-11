package springboot.demo.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.demo.entity.ClassSubjectTeacher;
import springboot.demo.entity.Subject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ClassSubjectTeacherRepository extends JpaRepository<ClassSubjectTeacher, Long> {

    Optional<ClassSubjectTeacher> findBySchoolClassIdAndSubjectIdAndTeacherId(Long classId, Long subjectId, Long teacherId);

    List<ClassSubjectTeacher> findBySubjectId(Long subjectId);

    List<ClassSubjectTeacher> findByTeacherId(Long teacherId);

    List<ClassSubjectTeacher> findBySchoolClass_Id(Long classId);


    boolean existsBySubject_Id(Long id);

    boolean existsBySchoolClass_IdAndSubject_Id(Long classId, Long subjectId);

    boolean existsByTeacher_Id(Long id);

    Optional<ClassSubjectTeacher> findBySchoolClass_IdAndSubject_Id(Long classId, Long subjectId);

    List<ClassSubjectTeacher> findBySchoolClassId(@NotNull(message = "classId is required") Long classId);

    //List<ClassSubjectTeacher> findBySubject_Id(Long subjectId);

    List<ClassSubjectTeacher> findBySubject_IdAndTeacher_Id(Long subjectId, Long teacherId);
}
