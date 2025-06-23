package discuss.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 웹 보안을 활성화하겠다는 어노테이션
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // (2) 요청별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/sign-up/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}