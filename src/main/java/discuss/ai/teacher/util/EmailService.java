package discuss.ai.teacher.util;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
}
