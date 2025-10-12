package springboot.demo.mapper;

import springboot.demo.dto.StudentDTO;
import springboot.demo.entity.Student;
import springboot.demo.entity.SchoolClass;

public class StudentMapper {
    public static StudentDTO toDto(Student s) {
        if (s == null) return null;
        StudentDTO.StudentDTOBuilder b = StudentDTO.builder()
                .id(s.getId())
                .studentCode(s.getStudentCode())
                .fullName(s.getFullName())
                .dob(s.getDob())
                .gender(s.getGender())
                .email(s.getEmail())
                .phone(s.getPhone());

        SchoolClass sc = s.getSchoolClass();
        if (sc != null) {
            b.classId(sc.getId())
                    .classCode(sc.getClassCode())
                    .className(sc.getName());
            if (sc.getGrade() != null) {
                b.gradeId(sc.getGrade().getId())
                        .gradeName(sc.getGrade().getName());
            }
        }

        return b.build();
    }

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) return null;
        // Không set schoolClass ở đây (service sẽ load SchoolClass và set vào Student)
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
