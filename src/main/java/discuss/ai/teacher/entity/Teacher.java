package discuss.ai.teacher.entity;

import discuss.ai.teacher.dto.TeacherSignupRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teacherId;
    @Column(nullable = false)
    private String userEmail;
    private String username;
    private String password;
    private String school;
    private String phoneNumber;

    public static Teacher createTeacher(TeacherSignupRequestDto dto){
        return Teacher.builder()
                .userEmail(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getName())
                .school(dto.getSchool())
                .phoneNumber(dto.getPhoneNumber())
                .build();
    }
}
