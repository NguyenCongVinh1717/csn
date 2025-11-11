package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.*;
import springboot.demo.entity.*;
import springboot.demo.mapper.SubjectMapper;
import springboot.demo.mapper.TeacherMapper;
import springboot.demo.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassSubjectTeacherService {

    private final ClassSubjectTeacherRepository cstRepo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final ClassRepository classRepo;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepo;
    private final ScheduleRepository scheduleRepository;


    @Transactional
    public void assignSubjectAndTeacherToClass(Long classId, Long subjectId, Long teacherId) {
        SchoolClass schoolClass = classRepo.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy lớp có id=" + classId));
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy môn học có id=" + subjectId));
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy giáo viên có id=" + teacherId));

        // check the grade of subject is belong to class
        if (!subject.getGrade().getId().equals(schoolClass.getGrade().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Môn học này không dành cho lớp của bạn");
        }

        // check whether the subject is assigned for that class?
        if (cstRepo.existsBySchoolClass_IdAndSubject_Id(classId, subjectId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lớp này đã có môn học đó");
        }

        // Create ClassSubjectTeacher
        ClassSubjectTeacher cst = ClassSubjectTeacher.builder()
                .schoolClass(schoolClass)
                .subject(subject)
                .teacher(teacher)
                .build();
        ClassSubjectTeacher savedCst = cstRepo.save(cst);

        // Create Enrollment for all students in that class
        if (!schoolClass.getStudents().isEmpty()) {
            List<Enrollment> enrollments = schoolClass.getStudents().stream()
                    .map(student -> Enrollment.builder()
                            .student(student)
                            .classSubjectTeacher(savedCst)
                            .build())
                    .toList();
            enrollmentRepository.saveAll(enrollments);
        }
    }



    @Transactional
    public void unassign(Long classId, Long subjectId, Long teacherId) {
        ClassSubjectTeacher cst = cstRepo.findBySchoolClassIdAndSubjectIdAndTeacherId(classId, subjectId, teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy"));

        // If having students, throw error
//        boolean hasEnrollments = enrollmentRepository.existsByClassSubjectTeacher_Id(cst.getId());
//        if (hasEnrollments) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT,
//                    "Cannot unassign subject from class: students are already enrolled");
//        }
        boolean hasSchedules = scheduleRepository.existsByClassSubjectTeacher_Id(cst.getId());
        if (hasSchedules) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bạn phải gỡ thời khoá biểu trước khi gỡ phân công");
        }


        // If no students, deleting
        cstRepo.delete(cst);
    }


    //change teacher
    @Transactional
    public void changeTeacher(Long classId, Long subjectId, Long newTeacherId) {
        ClassSubjectTeacher cst = cstRepo.findBySchoolClass_IdAndSubject_Id(classId, subjectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy với mã lớp là " + classId + ", môn học là " + subjectId
                ));

        //  Check existing of new teacher
        Teacher newTeacher = teacherRepo.findById(newTeacherId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy giáo viên có id=" + newTeacherId
                ));

        // If that teacher is teaching that subject, no change
        if (cst.getTeacher().getId().equals(newTeacherId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Giáo viên này đã dạy môn này rồi");
        }

        //Change teacher
        cst.setTeacher(newTeacher);
        cstRepo.save(cst);
    }





    //  List all teachers teaching a specific subject in all classes
    public List<TeacherDTO> listTeachersBySubject(Long subjectId) {
        subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học id=" + subjectId));

        return cstRepo.findBySubjectId(subjectId).stream()
                .map(ClassSubjectTeacher::getTeacher)
                .map(TeacherMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<SubjectDTO> listSubjectsByTeacher(Long teacherId) {
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên id=" + teacherId));

        return cstRepo.findByTeacherId(teacherId).stream()
                .map(ClassSubjectTeacher::getSubject)
                .map(SubjectMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<SubjectDTO> listSubjectsByClass(Long classId) {
        classRepo.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp id=" + classId));

        return cstRepo.findBySchoolClassId(classId).stream()
                .map(ClassSubjectTeacher::getSubject)
                .map(SubjectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClassSubjectTeacherDTO> listClassesBySubjectIdAndTeacherId(Long subjectId, Long teacherId) {
        subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học id=" + subjectId));
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên id=" + teacherId));

        List<ClassSubjectTeacher> list = cstRepo.findBySubject_IdAndTeacher_Id(subjectId,teacherId);

        return list.stream()
                .map(cst -> ClassSubjectTeacherDTO.builder()
                        .id(cst.getId())
                        .classId(cst.getSchoolClass().getId())
                        .className(cst.getSchoolClass().getName())
                        .subjectId(cst.getSubject().getId())
                        .subjectName(cst.getSubject().getName())
                        .teacherId(cst.getTeacher().getId())
                        .teacherName(cst.getTeacher().getFullName())
                        .build())
                .toList();
    }


    public List<ClassSubjectTeacherDTO> listByClassId(Long classId) {
        List<ClassSubjectTeacher> list = cstRepo.findBySchoolClass_Id(classId);

        return list.stream()
                .map(cst -> ClassSubjectTeacherDTO.builder()
                        .id(cst.getId())
                        .classId(cst.getSchoolClass().getId())
                        .className(cst.getSchoolClass().getName())
                        .subjectId(cst.getSubject().getId())
                        .subjectName(cst.getSubject().getName())
                        .teacherId(cst.getTeacher().getId())
                        .teacherName(cst.getTeacher().getFullName())
                        .build())
                .toList();
    }



    @Transactional(readOnly = true)
    public List<ClassSubjectTeacherDTO> findAllAssignments() {
        return cstRepo.findAll().stream()
                .map(cst -> ClassSubjectTeacherDTO.builder()
                        .id(cst.getId())
                        .classId(cst.getSchoolClass().getId())
                        .className(cst.getSchoolClass().getName())
                        .subjectId(cst.getSubject().getId())
                        .subjectName(cst.getSubject().getName())
                        .teacherId(cst.getTeacher().getId())
                        .teacherName(cst.getTeacher().getFullName())
                        .build())
                .collect(Collectors.toList());
    }




    //get classes
    public List<SchoolClassDTO> findAllClasses() {
        return classRepo.findAll().stream()
                .map(c -> new SchoolClassDTO( c.getId(),c.getName(),c.getGrade().getId()))
                .collect(Collectors.toList());
    }

    //get grades
    public List<GradeDTO> findAllGrades() {
        return gradeRepo.findAll().stream()
                .map(g -> new GradeDTO(g.getId(), g.getName()))
                .collect(Collectors.toList());
    }

    //count classes
    public long countClasses(){
        return classRepo.count();
    }

}
