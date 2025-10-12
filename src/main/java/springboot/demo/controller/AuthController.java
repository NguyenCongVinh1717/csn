package springboot.demo.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.LoginRequest;
import springboot.demo.dto.LoginResponse;
import springboot.demo.entity.AppUser;
import springboot.demo.repository.UserRepository;
import springboot.demo.security.JWTUtils;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid username/password");
        }

        UserDetails ud = (UserDetails) auth.getPrincipal();
        String token = jwtUtils.generateJwtToken(ud.getUsername());

        AppUser appUser = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        LoginResponse resp = new LoginResponse(token, "Bearer", appUser.getUsername(),
                appUser.getRole(), appUser.getId(), appUser.getStudentId(), appUser.getTeacherId());
        return ResponseEntity.ok(resp);
    }

}

