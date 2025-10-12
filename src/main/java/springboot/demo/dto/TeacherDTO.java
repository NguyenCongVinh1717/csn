package springboot.demo.dto;

import lombok.*;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDTO {
    private Long id;

    @NotBlank(message = "teacherCode is required")
    private String teacherCode;

    @NotBlank(message = "fullName is required")
    private String fullName;

    @Past(message = "dob must be in the past")
    private LocalDate dob;

    private String gender;

    @Email(message = "email must be valid")
    private String email;

    private String phone;
}
