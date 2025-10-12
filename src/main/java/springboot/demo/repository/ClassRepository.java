package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.dto.SchoolClassDTO;
import springboot.demo.entity.SchoolClass;

import java.util.List;

public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
}
