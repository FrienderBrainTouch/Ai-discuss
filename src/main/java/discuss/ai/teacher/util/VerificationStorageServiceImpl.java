package discuss.ai.teacher.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class VerificationStorageServiceImpl implements VerificationStorageService{
    private final StringRedisTemplate redisTemplate;
    @Override
    public void save(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public Optional<String> findByKey(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.of(value);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
