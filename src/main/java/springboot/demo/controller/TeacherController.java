package springboot.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.*;
import springboot.demo.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final EnrollmentService enrollmentService;
    private final ScheduleService scheduleService;
    private final ClassSubjectTeacherService classSubjectTeacherService;
    private final StudentService studentService;

    @GetMapping("/{id}")
    public TeacherDTO profile(@PathVariable Long id){ return teacherService.findById(id); }


    @GetMapping("/subjects/{subjectId}/students")
    public List<EnrollmentDTO> studentsOfSubject(@PathVariable Long subjectId){
        return enrollmentService.findBySubject(subjectId); }

    @PutMapping("/subjects/{cstId}/students/{studentId}/{grade}")
    public EnrollmentDTO setGrade(@PathVariable Long cstId, @PathVariable Long studentId, @PathVariable Double grade){
        return enrollmentService.setGrade(studentId, cstId, grade);
    }
    //need a function for deleting grade.


    @GetMapping("/classesOfSubjectAndTeacher/{subjectId}/{teacherId}")
    public List<ClassSubjectTeacherDTO> getClassesBySubject(@PathVariable Long subjectId,@PathVariable Long teacherId){
        return classSubjectTeacherService.listClassesBySubjectIdAndTeacherId(subjectId,teacherId);
    }
    @GetMapping("/studentsOfClass/{classId}")
    public List<StudentDTO> getStudentsOfClass(@PathVariable Long classId){
        return studentService.findByClass(classId);
    }

    @GetMapping("/class/{classId}/subject/{subjectId}/students")
    public List<EnrollmentDTO> getStudentsByClassAndSubject(
            @PathVariable Long classId,
            @PathVariable Long subjectId) {
        return enrollmentService.findByClassAndSubject(classId, subjectId);
    }

    @GetMapping("/subjectsOfTeacher/{teacherId}")
    public List<SubjectDTO> getSubjectsByTeacher(@PathVariable Long teacherId){
        return classSubjectTeacherService.listSubjectsByTeacher(teacherId);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        teacherService.changePassword(req);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/export/cst/{cstId}")
    public ResponseEntity<ByteArrayResource> exportCSTsById(@PathVariable Long cstId) throws IOException {
        ByteArrayResource resource = enrollmentService.exportCSTsToExcel(cstId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/teachers/{id}/schedules")
    public List<ScheduleDTO> getTeacherSchedules(@PathVariable Long id) {
        return scheduleService.findByTeacherId(id);
    }
}

