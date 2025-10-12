package springboot.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "schedules",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_schedule_cst_day_period",
                columnNames = {"class_teacher_subject_id","day_of_week","period"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "period", nullable = false)
    private Integer period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_teacher_subject_id", nullable = false)
    private ClassSubjectTeacher classSubjectTeacher;
}
