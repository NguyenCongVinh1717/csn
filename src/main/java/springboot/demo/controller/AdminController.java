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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springboot.demo.dto.*;
import springboot.demo.service.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final ClassSubjectTeacherService classSubjectTeacherService;
    private final ScheduleService scheduleService;
    public final EnrollmentService enrollmentService;


    // Students
    @GetMapping("/students")
    public List<StudentDTO> allStudents(){ return studentService.findAll(); }

    @GetMapping("/students/{id}")
    public StudentDTO findStudentByID(@PathVariable Long id){
        return studentService.findById(id);
    }

    @GetMapping("/studentsWithSubjectAnhGrade/{id}")
    public List<EnrollmentDTO> getStudent(@PathVariable Long id){ return enrollmentService.findByStudent(id); }

    @GetMapping("/studentsOfClass/{classID}")
    public List<StudentDTO> getStudentsByClass(@PathVariable Long classID){
        return studentService.findByClass(classID);
    }

    @GetMapping("/studentsWithName/{name}")
    public List<StudentDTO> findStudentsByName(@PathVariable String name){
        return studentService.findStudentByName(name);
    }


    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDTO dto){
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
    public StudentDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO dto){ return studentService.update(id, dto); }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id){ studentService.delete(id); return ResponseEntity.ok().build(); }

    //enroll/unenroll
    @PostMapping("/students/{studentId}/CST/{cstId}")
    public EnrollmentDTO enroll(@PathVariable Long studentId,@PathVariable Long cstId){
        return enrollmentService.enroll(studentId,cstId);
    }

    @DeleteMapping("/students/{studentId}/CST/{cstId}")
    public EnrollmentDTO unenroll(@PathVariable Long studentId, @PathVariable Long cstId){
        return enrollmentService.unenroll(studentId,cstId);
    }

    // Teachers
    @GetMapping("/teachers")
    public List<TeacherDTO> allTeachers(){ return teacherService.findAll(); }

    @GetMapping("/teachers/{id}")
    public TeacherDTO getTeacher(@PathVariable Long id){ return teacherService.findById(id); }

    @GetMapping("/teachersWithName/{name}")
    public List<TeacherDTO> findTeachersByName(@PathVariable String name){
        return teacherService.findTeacherByName(name);
    }

    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@Valid @RequestBody TeacherDTO dto){
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
    public TeacherDTO updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherDTO dto){ return teacherService.update(id,dto); }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id){ teacherService.delete(id); return ResponseEntity.ok().build(); }

    // Subjects
    @GetMapping("/subjects")
    public List<SubjectDTO> allSubjects(){ return subjectService.findAll(); }

    @GetMapping("/subjects/{id}")
    public SubjectDTO getSubject(@PathVariable Long id){ return subjectService.findById(id); }

    @GetMapping("/subjectsByGrade/{gradeID}")
    public List<SubjectDTO> findSubjectsByGrade(@PathVariable Long gradeID){
        return subjectService.findByGrade(gradeID);
    }

    @GetMapping("/subjectsByClass/{classID}")
        public List<SubjectDTO> findSubjectsByClass(@PathVariable Long classID){
        return classSubjectTeacherService.listSubjectsByClass(classID);
    }

    @GetMapping("/subjectsOfTeacher/{teacherID}")
    public List<SubjectDTO> findSubjectsByTeacherID(@PathVariable Long teacherID){
        return classSubjectTeacherService.listSubjectsByTeacher(teacherID);
    }

    @PostMapping("/subjects")
    public SubjectDTO createSubject(@Valid @RequestBody SubjectDTO dto){ return subjectService.create(dto); }

    @PutMapping("/subjects/{id}")
    public SubjectDTO updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectDTO dto){ return subjectService.update(id,dto); }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id){ subjectService.delete(id); return ResponseEntity.ok().build(); }


    //Schedules
    @GetMapping("/schedules")
    public List<ScheduleDTO> allSchedules() {
        return scheduleService.findAll();
    }

    @GetMapping("/schedules/{id}")
    public ScheduleDTO getSchedule(@PathVariable Long id) {
        return scheduleService.findById(id);
    }

    @PostMapping("/schedules")
    public ScheduleDTO createSchedule(@Valid @RequestBody ScheduleDTO dto) {
        return scheduleService.create(dto);
    }

    @PutMapping("/schedules/{id}")
    public ScheduleDTO updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDTO dto) {
        return scheduleService.update(id, dto);
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/schedulesByClass/{classID}")
    public List<ScheduleDTO> findSchedulesByClassID(@PathVariable Long classID){
        return scheduleService.findByClassId(classID);
    }

    @GetMapping("/schedulesByTeacher/{teacherID}")
    public List<ScheduleDTO> findSchedulesByTeacherID(@PathVariable Long teacherID){
        return scheduleService.findByTeacherId(teacherID);
    }



    //Asssign subjects and choose teachers for class
    //find assignments
    @GetMapping("/class-subject-teachers")
    public List<ClassSubjectTeacherDTO> getAllAssignments() {
        return classSubjectTeacherService.findAllAssignments();
    }

    @PostMapping("/class-subject-teachers")
    public ResponseEntity<?> assign(
            @RequestParam Long classId,
            @RequestParam Long subjectId,
            @RequestParam Long teacherId
    ) {
            classSubjectTeacherService.assignSubjectAndTeacherToClass(classId, subjectId, teacherId);
            return ResponseEntity.ok("Subject assigned to class with teacher successfully");
    }


    @DeleteMapping("/class-subject-teachers")
    public ResponseEntity<?> unassign(
            @RequestParam Long classId,
            @RequestParam Long subjectId,
            @RequestParam Long teacherId
    ) {
            classSubjectTeacherService.unassign(classId, subjectId, teacherId);
            return ResponseEntity.ok("Subject unassigned from class successfully");
    }

    //change teacher for class
    @PutMapping("/classes/{classId}/subjects/{subjectId}/teachers/{teacherId}")
    public ResponseEntity<String> changeTeacher(
            @PathVariable Long classId,
            @PathVariable Long subjectId,
            @PathVariable Long teacherId) {

        classSubjectTeacherService.changeTeacher(classId, subjectId, teacherId);
        return ResponseEntity.ok("Teacher updated successfully for class " + classId + " and subject " + subjectId);
    }


    @GetMapping("/subjects/{subjectId}/teachers")
    public ResponseEntity<?> listTeachersBySubject(@PathVariable Long subjectId) {
        try {
            List<TeacherDTO> list = classSubjectTeacherService.listTeachersBySubject(subjectId);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }


    @GetMapping("/teachers/{teacherId}/subjects")
    public ResponseEntity<?> listSubjectsByTeacher(@PathVariable Long teacherId) {
        try {
            List<SubjectDTO> list = classSubjectTeacherService.listSubjectsByTeacher(teacherId);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @GetMapping("/classes/{classId}/subjects")
    public ResponseEntity<?> listSubjectsByClass(@PathVariable Long classId) {
        try {
            List<SubjectDTO> list = classSubjectTeacherService.listSubjectsByClass(classId);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @GetMapping("/classes/{classId}/csts")
    public ResponseEntity<?> listCSTsByClass(@PathVariable Long classId) {
        try {
            List<ClassSubjectTeacherDTO> list = classSubjectTeacherService.listByClassId(classId);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @GetMapping("/countObjects")
    public CountObjectResponse getDashboard() {
        long students = studentService.countStudents();
        long teachers = teacherService.countTeachers();
        long subjects = subjectService.countSubjects();
        long classes=classSubjectTeacherService.countClasses();
        return new CountObjectResponse(students, teachers, subjects,classes);
    }



    @PostMapping("/import/students")
    public ResponseEntity<?> importStudents(@RequestBody List<@Valid StudentDTO> dtos) {
        if (dtos.size() > 50) return ResponseEntity.badRequest().body("Max 50 records allowed");
        int created = studentService.createBatchWithAccount(dtos);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @PostMapping("/import/teachers")
    public ResponseEntity<?> importTeachers(@RequestBody List<@Valid TeacherDTO> dtos) {
        if (dtos.size() > 50) return ResponseEntity.badRequest().body("Max 50 records allowed");
        int created = teacherService.createBatchWithAccount(dtos);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @PostMapping("/import/subjects")
    public ResponseEntity<?> importSubjects(@RequestBody List<@Valid SubjectDTO> dtos) {
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

    @GetMapping("/classes")
    public List<SchoolClassDTO> getClasses() {
        return classSubjectTeacherService.findAllClasses();
    }

    @GetMapping("/grades")
    public List<GradeDTO> getGrades() {
        return classSubjectTeacherService.findAllGrades();
    }

}

