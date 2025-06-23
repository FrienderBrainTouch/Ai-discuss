package discuss.ai.teacher.util;

import java.time.Duration;
import java.util.Optional;

public interface VerificationStorageService {
    void save(String key, String value, Duration duration);
    Optional<String> findByKey(String key);
    void delete(String key);
}

