package springboot.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.demo.dto.ConflictDTO;
import springboot.demo.dto.RecurrenceDTO;
import springboot.demo.dto.SessionDTO;
import springboot.demo.entity.ClassSession;
import springboot.demo.repository.ClassSessionRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final ClassSessionRepository repo;

    public TimetableService(ClassSessionRepository repo) {
        this.repo = repo;
    }

    public List<LocalDate> expandRecurrence(RecurrenceDTO rec) {
        if (rec == null) return Collections.emptyList();
        if (!"WEEKLY".equalsIgnoreCase(rec.type)) throw new IllegalArgumentException("Only WEEKLY supported");
        LocalDate cur = rec.startDate;
        LocalDate end = rec.endDate;
        Set<DayOfWeek> wanted = rec.days.stream().map(this::toDayOfWeek).collect(Collectors.toSet());
        List<LocalDate> out = new ArrayList<>();
        while (!cur.isAfter(end)) {
            if (wanted.contains(cur.getDayOfWeek())) out.add(cur);
            cur = cur.plusDays(1);
        }
        return out;
    }

    private DayOfWeek toDayOfWeek(String s) {
        return switch (s.toUpperCase()) {
            case "MON", "MONDAY" -> DayOfWeek.MONDAY;
            case "TUE", "TUESDAY" -> DayOfWeek.TUESDAY;
            case "WED", "WEDNESDAY" -> DayOfWeek.WEDNESDAY;
            case "THU", "THURSDAY" -> DayOfWeek.THURSDAY;
            case "FRI", "FRIDAY" -> DayOfWeek.FRIDAY;
            case "SAT", "SATURDAY" -> DayOfWeek.SATURDAY;
            case "SUN", "SUNDAY" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Invalid day: " + s);
        };
    }

    public List<ConflictDTO> checkConflictsForDate(LocalDate date, Long roomId, Long teacherId,
                                                   LocalTime start, LocalTime end, Long excludeId) {
        List<ConflictDTO> out = new ArrayList<>();
        if (roomId != null) {
            List<ClassSession> roomConf = repo.findConflicts(date, roomId, null, start, end, excludeId);
            for (ClassSession cs : roomConf) {
                if (Objects.equals(cs.getRoomId(), roomId)) {
                    ConflictDTO c = new ConflictDTO();
                    c.existingSessionId = cs.getId();
                    c.date = cs.getDate();
                    c.startTime = cs.getStartTime();
                    c.endTime = cs.getEndTime();
                    c.roomId = cs.getRoomId();
                    c.teacherId = cs.getTeacherId();
                    c.reason = "Room conflict";
                    out.add(c);
                }
            }
        }
        if (teacherId != null) {
            List<ClassSession> teacherConf = repo.findConflicts(date, null, teacherId, start, end, excludeId);
            for (ClassSession cs : teacherConf) {
                if (Objects.equals(cs.getTeacherId(), teacherId)) {
                    ConflictDTO c = new ConflictDTO();
                    c.existingSessionId = cs.getId();
                    c.date = cs.getDate();
                    c.startTime = cs.getStartTime();
                    c.endTime = cs.getEndTime();
                    c.roomId = cs.getRoomId();
                    c.teacherId = cs.getTeacherId();
                    c.reason = "Teacher conflict";
                    out.add(c);
                }
            }
        }
        return out;
    }

    @Transactional
    public List<ClassSession> createSessions(SessionDTO dto, Long createdBy) {
        if (dto.startTime == null || dto.endTime == null) throw new IllegalArgumentException("Start/End required");
        if (!dto.startTime.isBefore(dto.endTime))
            throw new IllegalArgumentException("startTime must be before endTime");

        List<LocalDate> dates;
        if (dto.recurrence != null) {
            dates = expandRecurrence(dto.recurrence);
            if (dates.isEmpty()) throw new IllegalArgumentException("Recurrence generated no dates");
        } else {
            if (dto.date == null) throw new IllegalArgumentException("date required for single session");
            dates = List.of(dto.date);
        }

        List<ConflictDTO> conflicts = new ArrayList<>();
        for (LocalDate d : dates) {
            conflicts.addAll(checkConflictsForDate(d, dto.roomId, dto.teacherId, dto.startTime, dto.endTime, null));
        }
        if (!conflicts.isEmpty()) {
            throw new ConflictException(conflicts);
        }

        List<ClassSession> saved = new ArrayList<>();
        Long seriesId = dto.recurrence != null ? System.currentTimeMillis() : null;
        for (LocalDate d : dates) {
            ClassSession cs = new ClassSession();
            cs.setSeriesId(seriesId);
            cs.setSubjectId(dto.subjectId);
            cs.setTeacherId(dto.teacherId);
            cs.setRoomId(dto.roomId);
            cs.setDate(d);
            cs.setStartTime(dto.startTime);
            cs.setEndTime(dto.endTime);
            cs.setNotes(dto.notes);
            cs.setCreatedBy(createdBy);
            saved.add(repo.save(cs));
        }
        return saved;
    }

    public List<ClassSession> teacherTimetable(Long teacherId, LocalDate from, LocalDate to) {
        return repo.findByTeacherIdAndDateBetween(teacherId, from, to);
    }

    public List<ClassSession> studentTimetable(List<Long> subjectIds, LocalDate from, LocalDate to) {
        return repo.findBySubjectIdInAndDateBetween(subjectIds, from, to);
    }

    public List<ClassSession> listSessions(LocalDate from, LocalDate to, Long teacherId, Long roomId) {
        return repo.findAll().stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .filter(s -> teacherId == null || Objects.equals(s.getTeacherId(), teacherId))
                .filter(s -> roomId == null || Objects.equals(s.getRoomId(), roomId))
                .collect(Collectors.toList());
    }
}
