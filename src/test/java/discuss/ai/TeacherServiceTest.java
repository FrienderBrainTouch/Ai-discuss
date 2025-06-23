package discuss.ai;

import discuss.ai.teacher.dto.TeacherSignupRequestDto;
import discuss.ai.teacher.entity.Teacher;
import discuss.ai.teacher.repository.TeacherRepository;
import discuss.ai.teacher.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*; //verify, any 등이 쓰이는 라이브러리
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

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
    @DisplayName("선생님이 이메일과 비밀번호를 통해 회원가입에 성공한다.")
    void signup_success(){
        //given
        Teacher teacher = Teacher.createTeacher(requestDto);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        //when
        Teacher savedTeacher = teacherService.signup(requestDto);

        //then
        assertThat(savedTeacher.getUserEmail()).isEqualTo(requestDto.getEmail());
        // verify 뒤의 times는 해당 메소드가 몇 회 실시되었는가를 의미
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외가 발생한다.")
    void signup_fail_with_duplicate_email() {
        // given (주어진 상황)
        // "이메일이 이미 존재한다"는 상황을 시뮬레이션해야 합니다.
        // 1. 가짜 '기존 사용자' 객체를 하나 만듭니다.
        Teacher existingTeacher = Teacher.createTeacher(requestDto);

        // 2. teacherRepository.findByEmail(email)이 호출되면,
        //    null이 아닌 위에서 만든 '기존 사용자' 객체를 Optional에 담아 반환하도록 설정합니다.
        when(teacherRepository.findByUserEmail(requestDto.getEmail())).thenReturn(Optional.of(existingTeacher));

        // when & then (무엇을 할 때 & 결과가 어때야 하는가)
        /**
         * assertThrows에 들어가보면 필요 인자로 assertThrows(Class<T> expectedType, Executable executable)를 사용하는 것을 알 수 있다.
         * 앞 부분 T가 의미하는 것은 제너릭으로 어떤 클래스든 가능하다는 것이고, 뒤에 Executable은 함수형 인터페이스로 단 하나의 추상 메소드만을 가질 수 있다.
         * 그래서 우리는 executable 자리에 우리가 실행시켜서 예외를 발생시키는 메소드를 넣는 것이다!!
         */
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            teacherService.signup(requestDto);
        });
        /**
         * 나 : 그냥 바로 singup 던지면 안되나? 왜 굳이 람다를 써야하는가?
         * assertThrows는 첫번째 인자로 제너릭을 받고 두번째 인자로는 함수형 인터페이스를 받는다.
         * assertThrows는 내부적으로 동작할 때 앞에 제너릭을 try-catch의 catch 부분에 두고, 두번째 인자를 try에 둔다.
         * 즉 Executable은 함수형 인터페이스의 구현체이니 실행되어야할 메소드 그 자체가 들어와야한다. 나처럼 메소드를 집어넣으면 메소드가 연산한 값을 넣는 것이지 저 메소드 자체를 try 안에 두어 예외를 던지지 못한다는 것이다.
         */
        // IllegalStateException exception2 = assertThrows(IllegalStateException.class,teacherService.signup(email,password));
        assertThat(exception.getMessage()).isEqualTo("이미 사용중인 이메일입니다.");
        verify(teacherRepository, never()).save(any(Teacher.class));
    }
}
