package springboot.demo.mapper;

import lombok.experimental.UtilityClass;
import springboot.demo.dto.ScheduleDTO;
import springboot.demo.entity.ClassSubjectTeacher;
import springboot.demo.entity.Schedule;

@UtilityClass
public class ScheduleMapper {

    public ScheduleDTO toDTO(Schedule schedule) {
        if (schedule == null) return null;

        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setPeriod(schedule.getPeriod());

        if (schedule.getClassSubjectTeacher() != null) {
            dto.setClassSubjectTeacherId(schedule.getClassSubjectTeacher().getId());
            dto.setClassName(schedule.getClassSubjectTeacher().getSchoolClass().getName());
            dto.setSubjectName(schedule.getClassSubjectTeacher().getSubject().getName());
            dto.setTeacherName(schedule.getClassSubjectTeacher().getTeacher().getFullName());
        }

        return dto;
    }

    public Schedule toEntity(ScheduleDTO dto) {
        if (dto == null) return null;

        Schedule s = new Schedule();
        s.setId(dto.getId());
        s.setDayOfWeek(dto.getDayOfWeek());
        s.setPeriod(dto.getPeriod());

        if (dto.getClassSubjectTeacherId() != null) {
            ClassSubjectTeacher cst = new ClassSubjectTeacher();
            cst.setId(dto.getClassSubjectTeacherId());
            s.setClassSubjectTeacher(cst);
        }

        return s;
    }
}
