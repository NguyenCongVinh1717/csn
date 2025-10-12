package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {}

