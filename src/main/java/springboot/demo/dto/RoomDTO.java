package springboot.demo.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoomDTO {
    public Long id;

    @NotBlank
    @Size(max = 100)
    public String code;

    @Size(max = 255)
    public String name;

    public Integer capacity;
}

