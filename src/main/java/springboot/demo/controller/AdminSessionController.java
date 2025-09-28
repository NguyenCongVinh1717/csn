package springboot.demo.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot.demo.dto.SessionDTO;
import springboot.demo.entity.ClassSession;
import springboot.demo.service.ConflictException;
import springboot.demo.service.TimetableService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/sessions")
public class AdminSessionController {

    private final TimetableService service;

    public AdminSessionController(TimetableService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SessionDTO dto,
                                    @RequestHeader(value="X-User-Id", required=false) Long userId) {
        try {
            List<ClassSession> created = service.createSessions(dto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ConflictException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("conflicts", ex.getConflicts()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public List<ClassSession> list(@RequestParam String from, @RequestParam String to,
                                   @RequestParam(required=false) Long teacherId,
                                   @RequestParam(required=false) Long roomId) {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        return service.listSessions(f, t, teacherId, roomId);
    }

}

