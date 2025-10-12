package springboot.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springboot.demo.dto.ScheduleDTO;
import springboot.demo.entity.ClassSubjectTeacher;
import springboot.demo.entity.Schedule;
import springboot.demo.mapper.ScheduleMapper;
import springboot.demo.repository.ClassSubjectTeacherRepository;
import springboot.demo.repository.ScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepo;
    private final ClassSubjectTeacherRepository cstRepo;


    public List<ScheduleDTO> findAll() {
        return scheduleRepo.findAll().stream()
                .map(ScheduleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ScheduleDTO findById(Long id) {
        Schedule s = scheduleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found id=" + id));
        return ScheduleMapper.toDTO(s);
    }


    @Transactional
    public ScheduleDTO create(ScheduleDTO dto) {
        // get ClassSubjectTeacher from DB
        ClassSubjectTeacher cst = cstRepo.findById(dto.getClassSubjectTeacherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Class-Subject-Teacher assignment not found."));

        Schedule schedule = Schedule.builder()
                .classSubjectTeacher(cst)
                .dayOfWeek(dto.getDayOfWeek())
                .period(dto.getPeriod())
                .build();

        // Check schedule of teacher
        if (scheduleRepo.existsByClassSubjectTeacher_Teacher_IdAndDayOfWeekAndPeriod(
                cst.getTeacher().getId(), schedule.getDayOfWeek(), schedule.getPeriod())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher has another class at this time.");
        }

        // Check schedule of class
        if (scheduleRepo.existsByClassSubjectTeacher_SchoolClass_IdAndDayOfWeekAndPeriod(
                cst.getSchoolClass().getId(), schedule.getDayOfWeek(), schedule.getPeriod())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Class has another subject at this time.");
        }

        Schedule saved = scheduleRepo.save(schedule);
        return ScheduleMapper.toDTO(saved);
    }



    @Transactional
    public ScheduleDTO update(Long id, ScheduleDTO dto) {
        Schedule existing = scheduleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found id=" + id));

        Schedule schedule = ScheduleMapper.toEntity(dto);

        Long cstId = schedule.getClassSubjectTeacher().getId();
        Integer day = schedule.getDayOfWeek();
        Integer period = schedule.getPeriod();

        // Check CST exist
        ClassSubjectTeacher cst = cstRepo.findById(cstId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Class-Subject-Teacher assignment not found"));

        // Check schedule of teacher
        if (scheduleRepo.existsByClassSubjectTeacher_Teacher_IdAndDayOfWeekAndPeriodAndIdNot(
                cst.getTeacher().getId(), day, period, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher has another class at this time.");
        }

        // Check schedule of class
        if (scheduleRepo.existsByClassSubjectTeacher_SchoolClass_IdAndDayOfWeekAndPeriodAndIdNot(
                cst.getSchoolClass().getId(), day, period, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Class has another subject at this time.");
        }

        // Create schedule
        existing.setClassSubjectTeacher(cst);
        existing.setDayOfWeek(day);
        existing.setPeriod(period);

        try {
            Schedule saved = scheduleRepo.save(existing);
            return ScheduleMapper.toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Schedule conflict (database constraint).", ex);
        }
    }



    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Schedule not found id=" + id));
        scheduleRepo.delete(schedule);
    }


    @Transactional(readOnly = true)
    public List<ScheduleDTO> findByStudentClass(Long classId) {
        List<Schedule> schedules = scheduleRepo.findByClassSubjectTeacher_SchoolClass_Id(classId);
        return schedules.stream()
                .map(ScheduleMapper::toDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<ScheduleDTO> findByTeacherId(Long teacherId) {
        // get schedules of teacher
        List<Schedule> schedules = scheduleRepo.findByClassSubjectTeacher_Teacher_Id(teacherId);
        return schedules.stream()
                .map(ScheduleMapper::toDTO)
                .toList();
    }


}


