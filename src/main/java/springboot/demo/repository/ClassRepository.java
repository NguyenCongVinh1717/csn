package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.dto.SchoolClassDTO;
import springboot.demo.entity.SchoolClass;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByName(String name);
}
