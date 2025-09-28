package springboot.demo.mapper;


import springboot.demo.dto.StudentDTO;
import springboot.demo.entity.Student;

public class StudentMapper {
    public static StudentDTO toDto(Student s) {
        if (s == null) return null;
        return StudentDTO.builder()
                .id(s.getId())
                .studentCode(s.getStudentCode())
                .fullName(s.getFullName())
                .dob(s.getDob())
                .gender(s.getGender())
                .email(s.getEmail())
                .phone(s.getPhone())
                .build();
    }
    public static Student toEntity(StudentDTO dto) {
        if (dto == null) return null;
        return Student.builder()
                .id(dto.getId())
                .studentCode(dto.getStudentCode())
                .fullName(dto.getFullName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}

