package discuss.ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // IllegalStateException이 발생하는 모든 예외는 이 메소드가 처리합니다.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        // 409 Conflict: 요청이 서버의 현재 상태와 충돌 (중복된 리소스)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // IllegalArgumentException 처리 핸들러도 여기에 추가할 수 있습니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 400 Bad Request: 클라이언트의 요청 자체가 잘못됨 (예: 토큰 불일치, 형식 오류)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
