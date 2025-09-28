//package springboot.demo.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import springboot.demo.dto.EnrollmentDTO;
//import springboot.demo.dto.StudentDTO;
//import springboot.demo.dto.SubjectDTO;
//import springboot.demo.dto.TeacherDTO;
//import springboot.demo.service.EnrollmentService;
//import springboot.demo.service.StudentService;
//import springboot.demo.service.SubjectService;
//import springboot.demo.service.TeacherService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//@CrossOrigin
//@RequiredArgsConstructor
//public class AdminController {
//    private final StudentService studentService;
//    private final TeacherService teacherService;
//    private final SubjectService subjectService;
//    private final EnrollmentService enrollmentService;
//
//    // Students
//    @GetMapping("/students")
//    public List<StudentDTO> allStudents(){ return studentService.findAll(); }
//
//    @GetMapping("/students/{id}")
//    public StudentDTO getStudent(@PathVariable Long id){ return studentService.findById(id); }
//
//    @PostMapping("/students")
//    public StudentDTO createStudent(@RequestBody StudentDTO dto){ return studentService.create(dto); }
//
//    @PutMapping("/students/{id}")
//    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO dto){ return studentService.update(id, dto); }
//
//    @DeleteMapping("/students/{id}")
//    public ResponseEntity<?> deleteStudent(@PathVariable Long id){ studentService.delete(id); return ResponseEntity.ok().build(); }
//
//    // Teachers
//    @GetMapping("/teachers")
//    public List<TeacherDTO> allTeachers(){ return teacherService.findAll(); }
//
//    @GetMapping("/teachers/{id}")
//    public TeacherDTO getTeacher(@PathVariable Long id){ return teacherService.findById(id); }
//
//    @PostMapping("/teachers")
//    public TeacherDTO createTeacher(@RequestBody TeacherDTO dto){ return teacherService.create(dto); }
//
//    @PutMapping("/teachers/{id}")
//    public TeacherDTO updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO dto){ return teacherService.update(id,dto); }
//
//    @DeleteMapping("/teachers/{id}")
//    public ResponseEntity<?> deleteTeacher(@PathVariable Long id){ teacherService.delete(id); return ResponseEntity.ok().build(); }
//
//    // Subjects
//    @GetMapping("/subjects")
//    public List<SubjectDTO> allSubjects(){ return subjectService.findAll(); }
//
//    @GetMapping("/subjects/{id}")
//    public SubjectDTO getSubject(@PathVariable Long id){ return subjectService.findById(id); }
//
//    @PostMapping("/subjects")
//    public SubjectDTO createSubject(@RequestBody SubjectDTO dto){ return subjectService.create(dto); }
//
//    @PutMapping("/subjects/{id}")
//    public SubjectDTO updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto){ return subjectService.update(id,dto); }
//
//    @DeleteMapping("/subjects/{id}")
//    public ResponseEntity<?> deleteSubject(@PathVariable Long id){ subjectService.delete(id); return ResponseEntity.ok().build(); }
//
//    // Enroll / Unenroll
//    @PostMapping("/students/{studentId}/enroll/{subjectId}")
//    public EnrollmentDTO enroll(@PathVariable Long studentId, @PathVariable Long subjectId){
//        return enrollmentService.enroll(studentId, subjectId);
//    }
//
//    @DeleteMapping("/students/{studentId}/unenroll/{subjectId}")
//    public ResponseEntity<?> unenroll(@PathVariable Long studentId, @PathVariable Long subjectId){
//        enrollmentService.unenroll(studentId, subjectId);
//        return ResponseEntity.ok().build();
//    }
//}
package springboot.demo.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.*;
import springboot.demo.service.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@RequiredArgsConstructor
public class AdminController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final EnrollmentService enrollmentService;
    private final RoomService roomService;

    // Students
    @GetMapping("/students")
    public List<StudentDTO> allStudents(){ return studentService.findAll(); }

    @GetMapping("/students/{id}")
    public StudentDTO getStudent(@PathVariable Long id){ return studentService.findById(id); }


    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody StudentDTO dto){
        // use createWithAccount which returns student + initialPassword (if user created)
        StudentService.CreateResult res = studentService.createWithAccount(dto);
        Map<String, Object> body = new HashMap<>();
        body.put("student", res.getStudent());
        if (res.getInitialPassword() != null) {
            body.put("username", res.getStudent().getStudentCode());
            body.put("initialPassword", res.getInitialPassword());
        }
        return ResponseEntity.ok(body);
    }

    @PutMapping("/students/{id}")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody StudentDTO dto){ return studentService.update(id, dto); }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){ studentService.delete(id); return ResponseEntity.ok().build(); }

    // Teachers
    @GetMapping("/teachers")
    public List<TeacherDTO> allTeachers(){ return teacherService.findAll(); }

    @GetMapping("/teachers/{id}")
    public TeacherDTO getTeacher(@PathVariable Long id){ return teacherService.findById(id); }


    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherDTO dto){
        // we expect TeacherService.createWithAccount(...) to exist and behave like StudentService.createWithAccount(...)
        TeacherService.CreateResult res = teacherService.createWithAccount(dto);
        Map<String, Object> body = new HashMap<>();
        body.put("teacher", res.getTeacher());
        if (res.getInitialPassword() != null) {
            body.put("username", res.getTeacher().getTeacherCode());
            body.put("initialPassword", res.getInitialPassword());
        }
        return ResponseEntity.ok(body);
    }

    @PutMapping("/teachers/{id}")
    public TeacherDTO updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO dto){ return teacherService.update(id,dto); }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id){ teacherService.delete(id); return ResponseEntity.ok().build(); }

    // Subjects
    @GetMapping("/subjects")
    public List<SubjectDTO> allSubjects(){ return subjectService.findAll(); }

    @GetMapping("/subjects/{id}")
    public SubjectDTO getSubject(@PathVariable Long id){ return subjectService.findById(id); }

    @PostMapping("/subjects")
    public SubjectDTO createSubject(@RequestBody SubjectDTO dto){ return subjectService.create(dto); }

    @PutMapping("/subjects/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto){ return subjectService.update(id,dto); }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id){ subjectService.delete(id); return ResponseEntity.ok().build(); }

    // Enroll / Unenroll
    @PostMapping("/students/{studentId}/enroll/{subjectId}")
    public EnrollmentDTO enroll(@PathVariable Long studentId, @PathVariable Long subjectId){
        return enrollmentService.enroll(studentId, subjectId);
    }

    @DeleteMapping("/students/{studentId}/unenroll/{subjectId}")
    public ResponseEntity<?> unenroll(@PathVariable Long studentId, @PathVariable Long subjectId){
        enrollmentService.unenroll(studentId, subjectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/import/students")
    public ResponseEntity<?> importStudents(@RequestBody List<StudentDTO> dtos) {
        if (dtos.size() > 50) return ResponseEntity.badRequest().body("Max 50 records allowed");
        int created = studentService.createBatchWithAccount(dtos);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @PostMapping("/import/teachers")
    public ResponseEntity<?> importTeachers(@RequestBody List<TeacherDTO> dtos) {
        if (dtos.size() > 50) return ResponseEntity.badRequest().body("Max 50 records allowed");
        int created = teacherService.createBatchWithAccount(dtos);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @PostMapping("/import/subjects")
    public ResponseEntity<?> importSubjects(@RequestBody List<SubjectDTO> dtos) {
        if (dtos.size() > 50) return ResponseEntity.badRequest().body("Max 50 records allowed");
        int created = subjectService.createBatch(dtos);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @GetMapping("/students/export")
    public ResponseEntity<ByteArrayResource> exportStudents() throws IOException {
        ByteArrayResource resource = studentService.exportStudentsToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/teachers/export")
    public ResponseEntity<ByteArrayResource> exportTeachers() throws IOException {
        ByteArrayResource resource = teacherService.exportTeachersToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teachers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    //room
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> list() {
        List<RoomDTO> list = roomService.listAll();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/room/{id}")
    public ResponseEntity<RoomDTO> get(@PathVariable Long id) {
        return roomService.findDtoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/rooms")
    public ResponseEntity<RoomDTO> create(@Valid @RequestBody RoomDTO dto) {
        RoomDTO created = roomService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }



    @PutMapping("/room/{id}")
    public ResponseEntity<RoomDTO> update(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        try {
            RoomDTO updated = roomService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/room/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }



}

