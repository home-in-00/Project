package com.example.actionprice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;

// TODO 현재 스웨거테스트용으로 "/hello/getTest" 경로 열어둠. 그 외의 다른 웹과 연결을 위해 다른 것도 추가해야 함
// TODO 아예 어느 정도 구현이 끝나기 전까지 보안을 막아두는 것도 고려해 봐야 함
/**
 * @author 연상훈
 * @created 24/10/01 13:46
 * @updated 24/10/01 13:46
 * @info SpringSecurity를 그레이들에 추가해두었기 때문에 스웨거를 그냥 사용할 수 없음.
 */

@EnableWebSecurity
@Configuration
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/public/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/hello/getTest")
                        .permitAll()
                        .anyRequest().authenticated());
        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

