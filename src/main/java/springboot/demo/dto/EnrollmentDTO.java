package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private Long id;

    @NotNull(message = "studentId is required")
    private Long studentId;

    // Optional
    private String studentName;

    @NotNull(message = "classSubjectTeacherId is required")
    private Long classSubjectTeacherId;

    // Optional
    private String subjectName;
    private String className;
    private String teacherName;

    @DecimalMin(value = "0.0", inclusive = true, message = "grade must be >= 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "grade must be <= 10")
    private Double grade;
}
