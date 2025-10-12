package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.entity.Subject;

import java.util.Arrays;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByCode(String code);

    List<Subject> findByGradeId(Long gradeID);

}
