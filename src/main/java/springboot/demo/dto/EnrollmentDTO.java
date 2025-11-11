package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private Long id;

    @NotNull(message = "Mã học sinh bắt buộc")
    private Long studentId;

    // Optional
    private String studentName;

    @NotNull(message = "CST bắt buộc")
    private Long classSubjectTeacherId;

    // Optional
    private String subjectName;
    private String className;
    private String teacherName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Điểm phải >= 0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Điểm phải <= 10")
    private Double grade;
}
