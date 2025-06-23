package discuss.ai.teacher.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender javaMailSender;
    @Async
    @Override
    public void sendVerificationEmail(String email, String token) {
        log.info("인증 메일 발송 시작. 수신자: {}", email);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true: 멀티파트 메시지

            // 메일 제목 설정
            helper.setSubject("[토론 보조 서비스] 회원가입 인증 메일입니다.");

            // 수신자 설정
            helper.setTo(email);

            helper.setText(token, true); // true: HTML 형식으로 전송

            // 메일 발송
            javaMailSender.send(message);
            log.info("인증 메일 발송 성공. 수신자: {}", email);

        } catch (MessagingException e) {
            log.error("메일 발송 중 오류 발생: {}", e.getMessage());
            // 실제 서비스에서는 더 정교한 예외 처리가 필요합니다.
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }
}
