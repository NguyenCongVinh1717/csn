package springboot.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.demo.entity.Schedule;

import java.util.Arrays;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByClassSubjectTeacher_Teacher_IdAndDayOfWeekAndPeriod(Long id, Integer day, Integer period);

    boolean existsByClassSubjectTeacher_SchoolClass_IdAndDayOfWeekAndPeriod(Long id, Integer day, Integer period);

    boolean existsByClassSubjectTeacher_Teacher_IdAndDayOfWeekAndPeriodAndIdNot(Long id, Integer day, Integer period, Long id1);

    boolean existsByClassSubjectTeacher_SchoolClass_IdAndDayOfWeekAndPeriodAndIdNot(Long id, Integer day, Integer period, Long id1);

    List<Schedule> findByClassSubjectTeacher_SchoolClass_Id(Long classId);
    List<Schedule> findByClassSubjectTeacher_Teacher_Id(Long teacherId);

    boolean existsByClassSubjectTeacher_Id(Long id);
}
