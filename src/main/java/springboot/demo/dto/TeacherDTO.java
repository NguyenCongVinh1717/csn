package springboot.demo.dto;


import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDTO {
    private Long id;
    private String teacherCode;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
}
