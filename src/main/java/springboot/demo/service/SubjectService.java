package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import springboot.demo.dto.SubjectDTO;
import springboot.demo.entity.Subject;
import springboot.demo.entity.Teacher;
import springboot.demo.mapper.SubjectMapper;
import springboot.demo.repository.SubjectRepository;
import springboot.demo.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;


    public List<SubjectDTO> findAll() {
        return subjectRepo.findAll().stream().map(SubjectMapper::toDto).collect(Collectors.toList());
    }


    public SubjectDTO findById(Long id) {
        Subject s = subjectRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
        return SubjectMapper.toDto(s);
    }


    public SubjectDTO create(SubjectDTO dto) {
        Subject s = SubjectMapper.toEntity(dto);
        if (dto.getTeacherId() != null) {
            Teacher t = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
            s.setTeacher(t);
        } else s.setTeacher(null);
        Subject saved = subjectRepo.save(s);
        return SubjectMapper.toDto(saved);
    }


    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject exist = subjectRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
        exist.setName(dto.getName());
        exist.setCode(dto.getCode());
        exist.setCredit(dto.getCredit());
        if (dto.getTeacherId() != null) {
            Teacher t = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
            exist.setTeacher(t);
        } else exist.setTeacher(null);
        return SubjectMapper.toDto(subjectRepo.save(exist));
    }


    public void delete(Long id) { subjectRepo.deleteById(id); }


    public List<SubjectDTO> findByTeacherId(Long teacherId) {
        return subjectRepo.findByTeacherId(teacherId).stream().map(SubjectMapper::toDto).collect(Collectors.toList());
    }

    public int createBatch(List<SubjectDTO> dtos) {
        int count = 0;
        for (SubjectDTO dto : dtos) {
            if (dto.getName() == null || dto.getName().isBlank() || dto.getCode() == null || dto.getCode().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name and code required");
            }
            if (subjectRepo.existsByCode(dto.getCode())) {
                continue;
            }
            Subject s = SubjectMapper.toEntity(dto);
            if (dto.getTeacherId() != null) {
                Teacher t = teacherRepo.findById(dto.getTeacherId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher not found"));
                s.setTeacher(t);
            } else {
                s.setTeacher(null);
            }
            subjectRepo.save(s);
            count++;
        }
        return count;
    }

}

