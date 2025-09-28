package springboot.demo.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot.demo.entity.ClassSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    @Query("""
      SELECT cs FROM ClassSession cs
      WHERE cs.date = :date
        AND (:roomId IS NULL OR cs.roomId = :roomId)
        AND (:teacherId IS NULL OR cs.teacherId = :teacherId)
        AND (:excludeId IS NULL OR cs.id <> :excludeId)
        AND NOT (cs.endTime <= :newStart OR cs.startTime >= :newEnd)
      """)
    List<ClassSession> findConflicts(
            @Param("date") LocalDate date,
            @Param("roomId") Long roomId,
            @Param("teacherId") Long teacherId,
            @Param("newStart") LocalTime newStart,
            @Param("newEnd") LocalTime newEnd,
            @Param("excludeId") Long excludeId
    );

    List<ClassSession> findByTeacherIdAndDateBetween(Long teacherId, LocalDate from, LocalDate to);
    List<ClassSession> findBySubjectIdInAndDateBetween(List<Long> subjectIds, LocalDate from, LocalDate to);
}
