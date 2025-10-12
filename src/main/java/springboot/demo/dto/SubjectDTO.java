package springboot.demo.dto;

import lombok.*;
import java.util.List;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "code is required")
    private String code;

    @NotNull(message = "credit is required")
    @Min(value = 0, message = "credit must be >= 0")
    private Integer credit;


    @NotNull(message = "gradeId is required")
    private Long gradeId;


    // optional
    private String gradeName;
    private List<Long> teacherIds;
    private List<String> teacherNames;
}
