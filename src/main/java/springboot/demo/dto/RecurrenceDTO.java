package springboot.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class RecurrenceDTO {
    public String type; // "WEEKLY" supported
    public List<String> days; // ex ["MON","WED"]
    public LocalDate startDate;
    public LocalDate endDate;
}

