package springboot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.demo.entity.AppUser;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<AppUser> findByStudentId(Long studentId);

    Optional<AppUser> findByTeacherId(Long teacherId);

}

