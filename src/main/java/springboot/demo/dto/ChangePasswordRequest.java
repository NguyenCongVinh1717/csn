package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Mật khẩu cũ bắt buộc")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới bắt buộc")
    @Size(min = 6, message = "Mật khẩu mới phải ít nhất 6 kí tự")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu bắt buộc")
    private String confirmPassword;
}
