package springboot.demo.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public class SessionDTO {
    public Long id;
    public Long subjectId;
    public Long teacherId;
    public Long roomId;
    public LocalDate date;       // for single occurrence
    public LocalTime startTime;
    public LocalTime endTime;
    public RecurrenceDTO recurrence; // optional
    public String notes;
}

