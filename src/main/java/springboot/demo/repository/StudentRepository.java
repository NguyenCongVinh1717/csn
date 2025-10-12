package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.dto.StudentDTO;
import springboot.demo.entity.Student;

import java.util.Arrays;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentCode(String studentCode);

    List<Student> findBySchoolClassId(Long classID);

    List<Student> findStudentByFullNameContainingIgnoreCase(String name);
}


