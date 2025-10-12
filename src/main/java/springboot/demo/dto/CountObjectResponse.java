package springboot.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountObjectResponse {
    private long students;
    private long teachers;
    private long subjects;
    private long classes;
}
