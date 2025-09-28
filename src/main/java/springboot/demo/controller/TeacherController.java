package springboot.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.*;
import springboot.demo.service.EnrollmentService;
import springboot.demo.service.SubjectService;
import springboot.demo.service.TeacherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final EnrollmentService enrollmentService;
    private final SubjectService subjectService;

    @GetMapping("/{id}")
    public TeacherDTO profile(@PathVariable Long id){ return teacherService.findById(id); }

    @GetMapping("/{teacherId}/subjects")
    public List<SubjectDTO> subjects(@PathVariable Long teacherId){
        return teacherService.getSubjects(teacherId); }

    @GetMapping("/subjects/{subjectId}/students")
    public List<EnrollmentDTO> studentsOfSubject(@PathVariable Long subjectId){
        return enrollmentService.findBySubject(subjectId); }

    @PutMapping("/subjects/{subjectId}/students/{studentId}/grade")
    public EnrollmentDTO setGrade(@PathVariable Long subjectId, @PathVariable Long studentId, @RequestParam Double grade){
        return enrollmentService.setGrade(studentId, subjectId, grade);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        teacherService.changePassword(req);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}

