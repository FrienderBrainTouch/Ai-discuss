package discuss.ai.teacher.entity;

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

    public static Teacher createTeacher(String email, String password){
        return Teacher.builder()
                .username(email)
                .password(password).build();
    }
}
