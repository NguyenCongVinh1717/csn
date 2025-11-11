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

    @NotBlank(message = "Mã giáo viên bắt buộc")
    private String teacherCode;

    @NotBlank(message = "Họ tên bắt buộc")
    private String fullName;

    @Past(message = "Ngày sinh phải hợp lệ")
    private LocalDate dob;

    private String gender;

    @Email(message = "Email phải hợp lệ")
    private String email;

    private String phone;
}
