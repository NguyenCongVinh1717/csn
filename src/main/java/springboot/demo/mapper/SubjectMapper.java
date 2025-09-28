package springboot.demo.mapper;


import springboot.demo.dto.SubjectDTO;
import springboot.demo.entity.Subject;

public class SubjectMapper {
    public static SubjectDTO toDto(Subject s){
        if (s==null) return null;
        Long teacherId = s.getTeacher() != null ? s.getTeacher().getId() : null;
        String teacherName = s.getTeacher() != null ? s.getTeacher().getFullName() : null;
        return SubjectDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .code(s.getCode())
                .credit(s.getCredit())
                .teacherId(teacherId)
                .teacherName(teacherName)
                .build();
    }
    public static Subject toEntity(SubjectDTO dto){
        if (dto==null) return null;
        return Subject.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .credit(dto.getCredit())
                .build(); // teacher handled in service (fetch by id)
    }
}

