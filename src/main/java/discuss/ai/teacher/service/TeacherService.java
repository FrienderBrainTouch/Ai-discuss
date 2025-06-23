package discuss.ai.teacher.service;

import discuss.ai.teacher.dto.TeacherSignupRequestDto;
import discuss.ai.teacher.entity.Teacher;
import discuss.ai.teacher.repository.TeacherRepository;
import discuss.ai.teacher.util.EmailService;
import discuss.ai.teacher.util.VerificationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final EmailService emailService;
    private final VerificationStorageService verificationStorage;

    /**
     * 이메일 송부를 담당하는 메소드
     * @param email
     */
    @Transactional(readOnly = true)
    public void sendEmail(String email){
        teacherRepository.findByUserEmail(email).ifPresent(teacher -> {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        });
        String verificationToken = UUID.randomUUID().toString();
        verificationStorage.save(email, verificationToken, Duration.ofMinutes(5));
        emailService.sendVerificationEmail(email, verificationToken);
    }

    public void verifyEmail(String email, String token) {
        // 1. Redis에서 토큰을 가져온다.
        String storedToken = verificationStorage.findByKey(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 정보가 만료되었거나 존재하지 않습니다."));
        // 2. 토큰이 일치하는지 확인. 다르면 예외.
        if (!storedToken.equals(token)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        // 3. 인증 성공 시, Redis에서 토큰 삭제
        verificationStorage.delete(email);
    }

    /**
     * 진짜 회원가입(메일 인증이 끝난 후)
     * @param requestDto
     * @return
     */
    @Transactional
    public Teacher signup(TeacherSignupRequestDto requestDto) {
        teacherRepository.findByUserEmail(requestDto.getEmail()).ifPresent(teacher -> {
        throw new IllegalStateException("이미 가입된 이메일입니다.");
    });
        Teacher newTeacher = Teacher.createTeacher(requestDto);
        return teacherRepository.save(newTeacher);
    }
}
