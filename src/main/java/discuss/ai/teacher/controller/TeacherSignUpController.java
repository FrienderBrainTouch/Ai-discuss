package discuss.ai.teacher.controller;

import discuss.ai.teacher.service.TeacherSignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sign-up/teachers")
public class TeacherSignUpController {
    private final TeacherSignUpService teacherSignUpService;

    @PostMapping("/mails")
    public ResponseEntity<Void> requestAuthenticMails(@RequestParam String email){
        teacherSignUpService.sendEmail(email);
        return ResponseEntity.ok().build();
    }
}
