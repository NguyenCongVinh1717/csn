package springboot.demo.mapper;


import springboot.demo.dto.TeacherDTO;
import springboot.demo.entity.Teacher;

public class TeacherMapper {
    public static TeacherDTO toDto(Teacher t){
        if (t==null) return null;
        return TeacherDTO.builder()
                .id(t.getId())
                .teacherCode(t.getTeacherCode())
                .fullName(t.getFullName())
                .dob(t.getDob())
                .gender(t.getGender())
                .email(t.getEmail())
                .phone(t.getPhone())
                .build();
    }
    public static Teacher toEntity(TeacherDTO dto){
        if (dto==null) return null;
        return Teacher.builder()
                .id(dto.getId())
                .teacherCode(dto.getTeacherCode())
                .fullName(dto.getFullName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}

