package springboot.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "oldPassword is required")
    private String oldPassword;

    @NotBlank(message = "newPassword is required")
    @Size(min = 8, message = "newPassword must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "confirmPassword is required")
    private String confirmPassword;
}
