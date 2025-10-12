package springboot.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassDTO {
    private Long id;
    private String name;
    private Long gradeId;
}
