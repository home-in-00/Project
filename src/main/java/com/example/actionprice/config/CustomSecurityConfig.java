package com.example.actionprice.config;

import com.example.actionprice.handler.LoginSuccessHandler;
import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.security.filter.LoginFilter;
import com.example.actionprice.security.filter.RefreshTokenFilter;
import com.example.actionprice.security.filter.TokenCheckFilter;
import com.example.actionprice.user.UserRepository;
import com.example.actionprice.util.JWTUtil;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @author 연상훈
 * @created 24/10/01 13:46
 * @updated 24/10/05 20:42
 * @info 어느 정도 개발이 완성되기 전까지는 보안을 포괄적으로 다 열어뒀음. 토큰관련 로직이 추가로 더 필요하긴 함.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Log4j2
public class CustomSecurityConfig {

    // field - @Autowired
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    // method - @Bean
    /**
     * @author : 연상훈
     * @created : 2024-10-05 오후 9:27
     * @updated : 2024-10-06 오후 1:23
     * @see : 로직을 일부 개편함.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("------------ security configuration --------------");

        // AuthenticationManager 설정. 얘가 일종의 매표소 직원. 출입하는 사람들의 권한 부여 및 확인 역할
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Login Success Handler
        LoginSuccessHandler successHandler = new LoginSuccessHandler(jwtUtil, userRepository);

        // Login Filter
        // "/generateToken"라는 경로를 호출하면 LoginFilter가 실행됨
        // 아직 로그인 기능을 구현하지 않았으니 토큰만 따로 발급하려고 이렇게 했지만,
        // 로그인을 위해서는 new LoginFilter()에 들어갈 경로와 formLogin.loginProcessingUrl()에 들어갈 경로를 일치시켜야 함
        LoginFilter loginFilter = new LoginFilter("/generateToken");
        loginFilter.setAuthenticationManager(authenticationManager);
        loginFilter.setAuthenticationSuccessHandler(successHandler);

        http.cors(httpSecurityCorsConfigurer -> {httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());}) // corsConfigurationSource
            .csrf((csrfconfig) -> csrfconfig.disable())
            .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/user/login", "/user/register", "/sendVerificationCode", "/tempGenerateToken").permitAll()
                        .anyRequest().permitAll())
            .authenticationManager(authenticationManager)
            .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class) // LoginFilter가 내가 추가하는 필터고, UsernamePasswordAuthenticationFilter.class는 기본 내장 필터. UsernamePasswordAuthenticationFilter보다 LoginFilter를 먼저 실행시키겠다
            .addFilterBefore(tokenCheckFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil), TokenCheckFilter.class)
            .rememberMe(httpSecurityRememberMeConfigurer -> {httpSecurityRememberMeConfigurer.rememberMeParameter("rememberMe")
                .tokenRepository(persistentTokenRepository()) // persistentTokenRepository
                .tokenValiditySeconds(60);}) // 토큰 기능의 테스트를 위해 rememberMe 기능의 토큰 유효 시간을 1분으로 설정
            .formLogin((formLogin) -> formLogin.loginPage("/user/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .failureUrl("/user/login") // TODO failureForwardUrl는 기존 url을 유지하면서 이동. 거기에 failureHandler를 쓰면 로그인 실패 횟수를 체크할 수 있을 것 같은데?
                    .loginProcessingUrl("/api/user/login") // 프론트와 맞춰야 함
                    .defaultSuccessUrl("/", true)
                    .permitAll())
            .logout((logout) -> logout.logoutSuccessUrl("/user/login").permitAll());
        return http.build();

    }

    /**
     * @author 연상훈
     * @created 24/10/05 20:20
     * @updated 24/10/05 20:20
     * @see : 간편한 jwt 사용을 위한 레포지토리
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() { // persistentTokenRepository
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    /**
     * @author 연상훈
     * @created 24/10/04
     * @updated 24/10/04
     * @see : 리액트-스프링부트 연결을 위한 cors config
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // corsConfigurationSource
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private TokenCheckFilter tokenCheckFilter(){
        return new TokenCheckFilter(userDetailsService, jwtUtil);
    }

}
