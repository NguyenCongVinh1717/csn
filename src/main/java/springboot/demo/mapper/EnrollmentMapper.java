package springboot.demo.mapper;


import springboot.demo.dto.EnrollmentDTO;
import springboot.demo.entity.Enrollment;

public class EnrollmentMapper {
    public static EnrollmentDTO toDto(Enrollment e){
        if (e==null) return null;
        Long studentId = e.getStudent()!=null ? e.getStudent().getId() : null;
        String studentName = e.getStudent()!=null ? e.getStudent().getFullName() : null;
        Long classSubjectTeacherId = e.getClassSubjectTeacher()!=null ? e.getClassSubjectTeacher().getId() : null;
        return EnrollmentDTO.builder()
                .id(e.getId())
                .studentId(studentId)
                .studentName(studentName)
                .classSubjectTeacherId(classSubjectTeacherId)
                .grade(e.getGrade())
                .build();
    }
}
