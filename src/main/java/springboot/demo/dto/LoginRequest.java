package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
public class LoginRequest {
    @NotBlank(message = "Tên đăng nhập bắt buộc")
    private String username;

    @NotBlank(message = "Mật khẩu bắt buộc")
    private String password;
}
