package springboot.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long id;

    @NotNull(message = "Thứ phải bắt buộc")
    @Min(value = 2, message = "Thứ phải từ 2-7")
    @Max(value = 7, message = "Thứ phải từ 2-7")
    private Integer dayOfWeek;

    @NotNull(message = "Tiết bắt buộc")
    @Min(value = 1, message = "Tiết bắt đầu từ 1")
    private Integer period;

    @NotNull(message = "CST bắt buộc")
    private Long classSubjectTeacherId;

    // Optional
    private String className;
    private String subjectName;
    private String teacherName;
}
