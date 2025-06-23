package discuss.ai.teacher.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TeacherSignupRequestDto {
    private String name;
    private String email;
    private String password;
    private String school;
    private String phoneNumber;
}
