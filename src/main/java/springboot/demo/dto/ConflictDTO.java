package springboot.demo.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public class ConflictDTO {
    public Long existingSessionId;
    public LocalDate date;
    public LocalTime startTime;
    public LocalTime endTime;
    public Long roomId;
    public Long teacherId;
    public String reason;
}
