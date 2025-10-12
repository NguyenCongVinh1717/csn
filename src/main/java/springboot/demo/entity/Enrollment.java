package springboot.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "class_subject_teacher_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_subject_id", nullable = false)
    private ClassSubjectTeacher classSubjectTeacher;

    private Double grade;
}
