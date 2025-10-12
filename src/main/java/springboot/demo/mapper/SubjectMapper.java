package springboot.demo.mapper;

import springboot.demo.dto.SubjectDTO;
import springboot.demo.entity.Subject;
import springboot.demo.entity.Grade;

import java.util.List;
import java.util.stream.Collectors;

public class SubjectMapper {
    public static SubjectDTO toDto(Subject s) {
        if (s == null) return null;

        SubjectDTO.SubjectDTOBuilder b = SubjectDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .code(s.getCode())
                .credit(s.getCredit());

        // Map grade
        Grade g = s.getGrade();
        if (g != null) {
            b.gradeId(g.getId()).gradeName(g.getName());
        }

        // Map teacher
        if (s.getClassSubjectTeachers() != null) {
            List<Long> teacherIds = s.getClassSubjectTeachers().stream()
                    .map(cst -> cst.getTeacher().getId())
                    .distinct()
                    .collect(Collectors.toList());
            List<String> teacherNames = s.getClassSubjectTeachers().stream()
                    .map(cst -> cst.getTeacher().getFullName())
                    .distinct()
                    .collect(Collectors.toList());
            b.teacherIds(teacherIds);
            b.teacherNames(teacherNames);
        }

        return b.build();
    }


    public static Subject toEntity(SubjectDTO dto) {
        if (dto == null) return null;
        return Subject.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .credit(dto.getCredit())
                .build();
    }
}
