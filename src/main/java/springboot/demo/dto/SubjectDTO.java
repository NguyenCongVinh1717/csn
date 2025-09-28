package springboot.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;
    private String name;
    private String code;
    private Integer credit;
    private Long teacherId;     // reference to teacher
    private String teacherName; // optional, for response convenience
}

