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

    @NotBlank(message = "Tên lớp bắt buộc")
    private String name;

    @NotBlank(message = "Mã lớp bắt buộc")
    private String code;

    @NotNull(message = "Số tín chỉ bắt bộc")
    @Min(value = 1, message = "Số tín chỉ phải > 0")
    private Integer credit;


    @NotNull(message = "Mã khối bắt buộc")
    private Long gradeId;


    // optional
    private String gradeName;
    private List<Long> teacherIds;
    private List<String> teacherNames;
}
