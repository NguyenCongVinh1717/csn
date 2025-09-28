package springboot.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.ChangePasswordRequest;
import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.dto.StudentDTO;
import springboot.demo.service.EnrollmentService;
import springboot.demo.service.StudentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    public StudentDTO profile(@PathVariable Long id){ return studentService.findById(id); }

    @GetMapping("/{id}/enrollments")
    public List<EnrollmentDTO> enrollments(@PathVariable Long id){ return enrollmentService.findByStudent(id); }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        studentService.changePassword(req);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
