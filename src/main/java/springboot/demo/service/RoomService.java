package springboot.demo.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.demo.dto.RoomDTO;
import springboot.demo.entity.Room;
import springboot.demo.mapper.RoomMapper;
import springboot.demo.repository.RoomRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RoomService {

    private final RoomRepository repo;
    private RoomMapper roomMapper;


    public List<RoomDTO> listAll() {
        return repo.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }


    public Optional<RoomDTO> findDtoById(Long id) {
        return repo.findById(id).map(roomMapper::toDto);
    }


    public RoomDTO create(RoomDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Room data is required");

        String code = dto.code != null ? dto.code.trim() : null;
        if (code == null || code.isEmpty()) throw new IllegalArgumentException("Room code is required");

        if (repo.existsByCode(code)) {
            throw new IllegalArgumentException("Room code already exists: " + code);
        }

        Room r = roomMapper.fromDto(dto);
        r.setId(null); // ensure identity generation
        Room saved = repo.save(r);
        return roomMapper.toDto(saved);
    }


    public RoomDTO update(Long id, RoomDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Room data is required");
        }


        Optional<Room> opt = repo.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Room not found: " + id);
        }

        Room existing = opt.get();


        String newCode = dto.code != null ? dto.code.trim() : null;
        if (newCode == null || newCode.isEmpty()) {
            throw new IllegalArgumentException("Room code is required");
        }


        if (!newCode.equalsIgnoreCase(existing.getCode())) {
            if (repo.existsByCode(newCode)) {
                throw new IllegalArgumentException("Room code already exists: " + newCode);
            }
            existing.setCode(newCode);
        }

        if (dto.name != null) {
            existing.setName(dto.name);
        }
        if (dto.capacity != null) {
            existing.setCapacity(dto.capacity);
        }

        Room saved = repo.save(existing);
        return roomMapper.toDto(saved);
    }



    public void delete(Long id) {
        repo.deleteById(id);
    }

}
