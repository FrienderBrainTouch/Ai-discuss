package discuss.ai;

import discuss.ai.teacher.dto.TeacherSignupRequestDto;
import discuss.ai.teacher.entity.Teacher;
import discuss.ai.teacher.repository.TeacherRepository;
import discuss.ai.teacher.service.TeacherService;
import discuss.ai.teacher.util.EmailService;
import discuss.ai.teacher.util.VerificationStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*; //verify, any 등이 쓰이는 라이브러리
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;
    @InjectMocks
    private TeacherService teacherService;
    @Mock
    private EmailService emailService;
    @Mock
    private VerificationStorageService verificationStorage;
    private TeacherSignupRequestDto requestDto;
    @BeforeEach
    void setUp() {
        requestDto = new TeacherSignupRequestDto();
        requestDto.setName("김선생");
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("password123");
        requestDto.setSchool("코딩중학교");
        requestDto.setPhoneNumber("010-1234-5678");
    }

    @Test
    @DisplayName("이미 가입된 이메일로 인증을 요청하면 예외가 발생한다.")
    void sendEmail_fail_when_email_is_duplicate() {
        // given
        String existingEmail = "exist@test.com";
        // findByUserEmail로 existingEmail을 조회하면, 이미 사용자가 있다고 설정
        when(teacherRepository.findByUserEmail(existingEmail)).thenReturn(Optional.of(Teacher.createTeacher(requestDto)));
        // when & then
        // requestVerification(existingEmail)을 실행하면, IllegalStateException이 터져야 한다!
        assertThrows(IllegalStateException.class, () -> {
            teacherService.sendEmail(existingEmail);
        });
        // emailService의 메일 발송 메소드는 절대 호출되면 안 된다.
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("새로운 이메일로 인증을 요청하면, 토큰 저장 및 메일 발송을 요청한다.")
    void sendEmail_success_when_email_is_new() {
        // given
        String newEmail = "new@test.com";
        when(teacherRepository.findByUserEmail(newEmail)).thenReturn(Optional.empty());
        // when
        teacherService.sendEmail(newEmail);
        // then
        verify(verificationStorage, times(1)).save(eq(newEmail), anyString(), any(Duration.class));
        verify(emailService, times(1)).sendVerificationEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("올바른 토큰으로 인증을 요청하면, 성공하고 토큰은 삭제된다.")
    void verifyEmail_success_when_token_is_valid() {
        // given
        String email = "test@test.com";
        String token = "valid-token-123";
        when(verificationStorage.findByKey(email)).thenReturn(Optional.of(token));
        // when & then
        // void 메소드가 예외를 던지지 않는 것을 검증하는 가장 좋은 방법
        assertDoesNotThrow(() -> teacherService.verifyEmail(email, token));
        // 토큰이 삭제되었는지 검증
        verify(verificationStorage, times(1)).delete(email);
    }

    @Test
    @DisplayName("인증 완료 후 모든 정보로 최종 회원가입을 요청하면 성공한다.")
    void signup_success_after_verification() {
        // given
        when(teacherRepository.findByUserEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // when
        Teacher savedTeacher = teacherService.signup(requestDto);
        // then
        verify(teacherRepository, times(1)).save(any(Teacher.class));
        assertThat(savedTeacher.getUserEmail()).isEqualTo(requestDto.getEmail());
    }

    @Test
    @DisplayName("사용자가 메일로 전달받은 인증 토큰 확인 요청했으나, 토큰이 유효하지 않아 인증에 실패한다.")
    void invalidToken(){
        // given: '상황 설정'
        String email = "test@test.com";
        String providedToken = "WRONG-token-789";   // 사용자가 입력한 '틀린' 토큰
        String storedToken = "CORRECT-token-123"; // Redis에 저장되어 있는 '올바른' 토큰
        // 1. Redis(가짜 저장소)에 '올바른' 토큰이 저장되어 있다고 가정합니다.
        when(verificationStorage.findByKey(email)).thenReturn(Optional.of(storedToken));
        // when & then: '행동'과 '검증'을 동시에
        // 2. '틀린' 토큰으로 verifyEmail을 실행하면, "인증 코드가 일치하지 않습니다." 예외가 발생해야 합니다.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            teacherService.verifyEmail(email, providedToken);
        });
        assertThat(exception.getMessage()).isEqualTo("인증 코드가 일치하지 않습니다.");
        // 3. (추가 검증) 인증에 실패했으므로, 토큰은 삭제되지 않았어야 합니다.
        verify(verificationStorage, never()).delete(email);
    }
    @Test
    @DisplayName("인증 토큰이 만료되었거나 존재하지 않아 인증에 실패한다.")
    void timeOutAuthentication() {
        // given: '상황 설정'
        String email = "test@test.com";
        String providedToken = "any-token-because-it-doesnt-matter";
        // 1. Redis(가짜 저장소)를 조회하면, 비어있는 Optional을 반환하도록 설정합니다.
        // -> 이것이 '토큰이 없거나 만료된' 상황을 흉내 내는 것입니다.
        when(verificationStorage.findByKey(email)).thenReturn(Optional.empty());
        // when & then: '행동'과 '검증'을 동시에
        // 2. verifyEmail을 실행하면, "인증 정보가 만료..." 예외가 발생해야 합니다.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            teacherService.verifyEmail(email, providedToken);
        });
        assertThat(exception.getMessage()).isEqualTo("인증 정보가 만료되었거나 존재하지 않습니다.");
        // 3. (추가 검증) 당연히 토큰이 없었으므로, 삭제 로직도 호출될 일이 없습니다.
        verify(verificationStorage, never()).delete(email);
    }
}
