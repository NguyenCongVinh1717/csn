package springboot.demo.dto;

import lombok.*;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private Long id;

    @NotBlank(message = "studentCode is required")
    private String studentCode;

    @NotBlank(message = "fullName is required")
    private String fullName;

    @Past(message = "dob must be in the past")
    private LocalDate dob;

    private String gender;

    @Email(message = "email must be valid")
    private String email;

    private String phone;

    @NotNull(message = "classId is required")
    private Long classId;

    // optional
    private String classCode;
    private String className;
    private Long gradeId;
    private String gradeName;
}
