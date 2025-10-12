package springboot.demo.bootstrap;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot.demo.entity.AppUser;
import springboot.demo.repository.UserRepository;

@Component
public class AdminPasswordHashBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminPasswordHashBootstrap.class);

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password:123456}")
    //old password is admin123
    private String adminPasswordIfMissing;

    public AdminPasswordHashBootstrap(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Optional<AppUser> opt = userRepo.findByUsername(adminUsername);
        if (opt.isPresent()) {
            AppUser admin = opt.get();
            String stored = admin.getPassword();
            boolean looksBcrypt = stored != null &&
                    (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"));

            if (!looksBcrypt) {
                String toEncode = (stored == null || stored.isBlank()) ? adminPasswordIfMissing : stored;
                String encoded = passwordEncoder.encode(toEncode);
                admin.setPassword(encoded);
                userRepo.save(admin);
                log.info("Hashed admin password for username='{}'", adminUsername);
            } else {
                log.info("Admin password already hashed for '{}'", adminUsername);
            }
        } else {
            AppUser admin = AppUser.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPasswordIfMissing))
                    .role("ADMIN")
                    .build();
            userRepo.save(admin);
            log.info("Created bootstrap admin '{}'", adminUsername);
        }
    }
}

