package springboot.demo.controller;


import org.springframework.web.bind.annotation.*;
import springboot.demo.entity.ClassSession;
import springboot.demo.service.TimetableService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
public class TimetableController {
    private final TimetableService service;
    public TimetableController(TimetableService service) { this.service = service; }

    @GetMapping("/teacher/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<ClassSession> teacher(@PathVariable Long id,
                                      @RequestParam String from, @RequestParam String to) {
        return service.teacherTimetable(id, LocalDate.parse(from), LocalDate.parse(to));
    }

    @GetMapping("/student")
    // client passes subjectIds extracted from enrollment
    public List<ClassSession> student(@RequestParam List<Long> subjectIds,
                                      @RequestParam String from, @RequestParam String to) {
        return service.studentTimetable(subjectIds, LocalDate.parse(from), LocalDate.parse(to));
    }
}
