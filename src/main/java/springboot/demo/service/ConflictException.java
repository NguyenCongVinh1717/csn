package springboot.demo.service;



import springboot.demo.dto.ConflictDTO;

import java.util.List;

public class ConflictException extends RuntimeException {
    private final List<ConflictDTO> conflicts;
    public ConflictException(List<ConflictDTO> conflicts) {
        super("Conflicts: " + (conflicts == null ? 0 : conflicts.size()));
        this.conflicts = conflicts;
    }
    public List<ConflictDTO> getConflicts() { return conflicts; }
}

