package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springboot.demo.dto.SchoolClassDTO;
import springboot.demo.dto.SubjectDTO;
import springboot.demo.entity.Grade;
import springboot.demo.entity.Subject;
import springboot.demo.entity.ClassSubjectTeacher;
import springboot.demo.entity.Teacher;
import springboot.demo.mapper.SubjectMapper;
import springboot.demo.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepo;
    private final GradeRepository gradeRepo;
    private final ClassSubjectTeacherRepository cstRepo;
    private final EnrollmentRepository enrollmentRepo;

    // FIND ALL
    @Transactional(readOnly = true)
    public List<SubjectDTO> findAll() {
       return subjectRepo.findAll()
                .stream()
                .map(SubjectMapper::toDto)
                .collect(Collectors.toList());
    }



    //  FIND BY ID
    @Transactional(readOnly = true)
    public SubjectDTO findById(Long id) {
        Subject s = subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found id=" + id));
        return SubjectMapper.toDto(s);
    }


    //find by grade
    public List<SubjectDTO> findByGrade(Long gradeID){
        return subjectRepo.findByGradeId(gradeID)
                .stream()
                .map(SubjectMapper::toDto)
                .collect(Collectors.toList());
    }

    // CREATE
    public SubjectDTO create(SubjectDTO dto) {
        Grade grade = gradeRepo.findById(dto.getGradeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade not found id=" + dto.getGradeId()));

        if (subjectRepo.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject code already exists");
        }

        Subject subject = SubjectMapper.toEntity(dto);
        subject.setGrade(grade);

        Subject saved = subjectRepo.save(subject);
        return SubjectMapper.toDto(saved);
    }

    // UPDATE
    @Transactional
    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject exist = subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Subject not found id=" + id));

        // check dupicate
        if (!dto.getCode().equals(exist.getCode())) {
            if (subjectRepo.existsByCode(dto.getCode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject code already exists");
            }
            exist.setCode(dto.getCode());
        }
        exist.setName(dto.getName());
        exist.setCredit(dto.getCredit());

        //if having enrollment, throw error
        Long currentGradeId = exist.getGrade() != null ? exist.getGrade().getId() : null;
        if (!dto.getGradeId().equals(currentGradeId)) {
            boolean hasEnrollments = enrollmentRepo.existsByClassSubjectTeacher_Subject_Id(id);
            if (hasEnrollments) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "This subject has enrolled students, please unenroll before updating grade");
            }

            Grade grade = gradeRepo.findById(dto.getGradeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Grade not found id=" + dto.getGradeId()));
            exist.setGrade(grade);
        }

        Subject saved = subjectRepo.save(exist);
        return SubjectMapper.toDto(saved);
    }


    //  DELETE
    @Transactional
    public void delete(Long id) {
        Subject subject = subjectRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Subject not found id=" + id));

        //if having enrollment, throw
        if (enrollmentRepo.existsByClassSubjectTeacher_Subject_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete subject: there are students enrolled in this subject");
        }

        // if having in cst, throw error
        if (cstRepo.existsBySubject_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete subject: it is currently assigned to classes or teachers");
        }

        // delete subject
        subjectRepo.delete(subject);
    }

    //countSubjects
    public long countSubjects() {
        return subjectRepo.count();
    }


    //  BATCH CREATE
    public int createBatch(List<SubjectDTO> dtos) {
        int count = 0;
        for (SubjectDTO dto : dtos) {
            if (dto == null || dto.getName() == null || dto.getCode() == null || dto.getGradeId() == null) continue;
            if (subjectRepo.existsByCode(dto.getCode())) continue;

            Grade grade = gradeRepo.findById(dto.getGradeId()).orElse(null);
            if (grade == null) continue;

            Subject s = SubjectMapper.toEntity(dto);
            s.setGrade(grade);
            subjectRepo.save(s);
            count++;
        }
        return count;
    }
}
