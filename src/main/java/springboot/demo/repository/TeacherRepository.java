package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.entity.Teacher;

import java.util.Arrays;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByTeacherCode(String teacherCode);

    List<Teacher> findTeacherByFullNameContainingIgnoreCase(String name);
}
