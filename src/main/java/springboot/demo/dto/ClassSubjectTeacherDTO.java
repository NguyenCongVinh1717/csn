package springboot.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSubjectTeacherDTO {
    private Long id;
    private Long classId;
    private String className;

    private Long subjectId;
    private String subjectName;

    private Long teacherId;
    private String teacherName;
}
