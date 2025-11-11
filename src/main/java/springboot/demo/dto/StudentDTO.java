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

    @NotBlank(message = "Mã học sinh bắt buộc")
    private String studentCode;

    @NotBlank(message = "Họ tên bắt buộc")
    private String fullName;

    @Past(message = "Ngày sinh phải hợp lệ")
    private LocalDate dob;

    private String gender;

    @Email(message = "Email phải hợp lệ ")
    private String email;

    private String phone;

    @NotNull(message = "Mã lớp bắt buộc")
    private Long classId;

    // optional
    private String classCode;
    private String className;
    private Long gradeId;
    private String gradeName;
}
