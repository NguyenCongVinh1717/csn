package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
public class LoginRequest {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;
}
