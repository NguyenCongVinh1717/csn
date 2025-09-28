package springboot.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private Long userId;
    private Long studentId;
    private Long teacherId;
}
