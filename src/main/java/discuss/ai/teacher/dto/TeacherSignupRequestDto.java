package discuss.ai.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class TeacherSignupRequestDto {
    private String name;
    private String email;
    private String password;
    private String school;
    private String phoneNumber;
}
