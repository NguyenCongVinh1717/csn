package springboot.demo.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi ResponseStatusException (thường throw trong service)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getReason());
        body.put("timestamp", new Date());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    // Lỗi validation từ @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("timestamp", new Date());
        body.put("details", errors);

        if (!errors.isEmpty()) {
            body.put("message", errors.get(0).get("message"));
        } else {
            body.put("message", "Dữ liệu không hợp lệ");
        }

        return ResponseEntity.badRequest().body(body);
    }

    // Lỗi ràng buộc database (như unique constraint)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Database constraint violation");
        body.put("timestamp", new Date());
        body.put("message", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // Lỗi chung (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Unexpected error");
        body.put("message", ex.getMessage());
        body.put("timestamp", new Date());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("timestamp", new Date());

        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            // kiểm tra nếu là LocalDate
            if (ife.getTargetType() == java.time.LocalDate.class) {
                body.put("error", "Invalid date format");
                body.put("message", "Ngày sinh không hợp lệ");
                body.put("value", ife.getValue()); // giá trị mà người dùng gửi
            } else {
                body.put("error", "Invalid value");
                body.put("message", ife.getOriginalMessage());
                body.put("value", ife.getValue());
            }
        } else {
            body.put("error", "Malformed JSON");
            body.put("message", ex.getMessage());
        }

        return ResponseEntity.badRequest().body(body);
    }


}

