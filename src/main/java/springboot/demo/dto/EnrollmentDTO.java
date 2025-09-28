package springboot.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName; // optional
    private Long subjectId;
    private String subjectName; // optional
    private Double grade;
}

