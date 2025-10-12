package springboot.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long id;

    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    private Integer dayOfWeek;

    @NotNull(message = "Period is required")
    @Min(value = 1, message = "Period must be at least 1")
    private Integer period;

    @NotNull(message = "ClassSubjectTeacher ID must not be null")
    private Long classSubjectTeacherId;

    // Optional
    private String className;
    private String subjectName;
    private String teacherName;
}
