package springboot.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ClassSession", schema = "dbo") // map chính xác tới bảng dbo.ClassSession
@Getter
@Setter
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seriesId;

    @Column(nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private Long teacherId;

    private Long roomId;

    @Column(nullable = false,name = "SessionDate")
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(columnDefinition = "nvarchar(max)")
    private String notes;

    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
