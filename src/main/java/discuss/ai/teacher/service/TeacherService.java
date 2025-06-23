package discuss.ai.teacher.service;

import discuss.ai.teacher.entity.Teacher;
import discuss.ai.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    /**
     *     public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
     *         if (value != null) {
     *             return value;
     *         } else {
     *             throw exceptionSupplier.get();
     *         }
     *     }
     *     위가 orElseThrow의 원 코드인데 원래 얘는 Optional 객체가 비어있을 때 예외를 던져주는 역할을 한다. 첨 알았다.
     *     ifPresent 얘는 Consumer<? super T> action 이걸 인자로 받는다.
     *     컨수머는 인자로 받은 놈을 가지고 아무것도 반환하지 않는다. void다. 그래서 이걸 가지고 어떤 일을 처리할 뿐이다.
     *     그런데 ifPresent는 말 그대로 값이 있을 때 우리가 람다로 지정한 메소드의 기능을 따른다. 그래서 사용자가 있는거니까 이미 사용중인 이메일이다. 라고 할 수 있고 이를 accept가 잡아 일을 처리할 뿐인 것이다.
     */
    @Transactional
    public Teacher signup(String email, String password){
        teacherRepository.findByUserEmail(email).ifPresent(teacher -> {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");});
        Teacher teacher = Teacher.createTeacher(email, password);
        return teacherRepository.save(teacher);
    }
}
