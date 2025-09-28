package springboot.demo.mapper;

import org.springframework.stereotype.Component;
import springboot.demo.dto.RoomDTO;
import springboot.demo.entity.Room;
@Component
public class RoomMapper {
    // mapping helpers
    public RoomDTO toDto(Room r) {
        RoomDTO dto = new RoomDTO();
        dto.id = r.getId();
        dto.code = r.getCode();
        dto.name = r.getName();
        dto.capacity = r.getCapacity();
        return dto;
    }

    public Room fromDto(RoomDTO dto) {
        Room r = new Room();
        r.setId(dto.id);
        r.setCode(dto.code);
        r.setName(dto.name);
        r.setCapacity(dto.capacity);
        return r;
    }
}
