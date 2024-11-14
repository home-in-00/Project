package com.example.actionprice.security.config;

import com.example.actionprice.security.CustomUserDetailService;
import com.example.actionprice.security.UrlPathManager;
import com.example.actionprice.security.handler.LoginSuccessHandler;
import com.example.actionprice.security.filter.LoginFilter;
import com.example.actionprice.security.filter.RefreshTokenFilter;
import com.example.actionprice.security.filter.TokenCheckFilter;
import com.example.actionprice.security.jwt.accessToken.AccessTokenService;
import com.example.actionprice.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 보안 관련된 거의 모든 설정이 있는 곳
 * @author 연상훈
 * @created 24/10/01 13:46
 * @updated 24/10/14 05:26 LoginSuccessHandler가 rememberMe 토큰 생성을 막고 있어서 제거. 그것을 대체하는 LoginFilter - successfulAuthentication 생성함
 * @updated 2024-10-14 오후 12:05 LoginSuccessHandler가 없으면 또 로그인에 문제 생겨서 다시 생성함. 그리고 대부분의 객체를 Bean으로 관리하도록 수정
 * @updated 2024-10-17 오후 7:15 : 리멤버미 삭제
 * @updated 2024-10-19 오후 5:19 : logoutSuccessHandler 추가. jwtUtil을 RefreshTokenService로 통합. RefreshTokenService 안에 jwtUtil 있음
 * @updated 2024-11-07 오후 11:30 : 보안을 적극적으로 적용하면서 대폭 수정. url path 수정, 필터 수정 등
 * @updated 2024-11-13 오전 1:23 [연상훈] : bean과 url path 정리. 지나치게 길고 많은 url들을 UrlPathManager에서 관리하도록 함
 */
@SuppressWarnings("ALL")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Log4j2
public class CustomSecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;

    /**
     * @author : 연상훈
     * @created : 2024-10-05 오후 9:27
     * @info component나 configuration으로 등록하기엔 자잘한 것들을 여기서 bean으로 설정해서 관리함 
     * @info 세션 비활성화
     * @info cors 활성화
     * @info accessDeniedHandler - 접근 거절 시 리다이렉트 비활성화
     * @info 토큰 필터 순서에 주의. 리프레시 토큰 필터 > 토큰 체크 필터 > 로그인/로그아웃 필터 > UsernamePasswordAuthenticationFilter
     * @info 폼로그인 비활성화 - 접근 거절 시 리다이렉트 비활성화
     * @info 요청 url path 설정에 주의
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      log.info("------------ security configuration --------------");

      //유저 권한 password 검증
      AuthenticationManager authenticationManager = authenticationManager(http);

      UrlPathManager urlPathManager = new UrlPathManager();

      http.sessionManagement(sessionPolicy -> sessionPolicy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())) // corsConfigurationSource
          .csrf((csrfconfig) -> csrfconfig.disable())
          .exceptionHandling(exceptionHandler -> {
            // accessDeniedHandler가 리다이렉트하지 않도록 만들어서 인증 실패 후 계속 이상한 곳으로 요청 보내지 않도록 함
            exceptionHandler.accessDeniedHandler(accessDeniedHandler());
            // 사용자가 허락되지 않은 경로로 강제 이동 시의 처리를 진행
            exceptionHandler.authenticationEntryPoint(new Http403ForbiddenEntryPoint());
          })
          .authorizeHttpRequests((authz) -> authz
                      .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                      .requestMatchers(urlPathManager.getPATH_ADMIN()).hasRole("ADMIN")
                      .requestMatchers(urlPathManager.getPATH_ANONYMOUSE()).anonymous() // 로그인을 안 한 사람만 이동 가능
                      .requestMatchers(urlPathManager.getPATH_AUTHENTICATED()).authenticated() // 로그인을 한 사람만 이동 가능
                      .requestMatchers(urlPathManager.getPATH_PERMIT_ALL()).permitAll()
                      .anyRequest().authenticated())
          .authenticationManager(authenticationManager)
          .addFilterBefore(new TokenCheckFilter(userDetailsService, accessTokenService), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(new RefreshTokenFilter(accessTokenService), TokenCheckFilter.class)
          .addFilterBefore(loginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
          .addFilterBefore(logoutFilter(), UsernamePasswordAuthenticationFilter.class) // 필터 순서에 주의. 기본적으로 나중에 입력한 것일수록 뒤에 실행됨
          .formLogin((formLogin) -> formLogin.disable());
      return http.build();

    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
      AuthenticationManagerBuilder authenticationManagerBuilder =
              http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder.userDetailsService(userDetailsService)
              .passwordEncoder(passwordEncoder());

      return authenticationManagerBuilder.build();
    }

    /**
     * 로그인 필터. 로그인 절차를 임의로 수정하기 위해서 필요함
     * @author 연상훈
     * @created 2024-11-07 오후 11:37
     * @info 인증을 위해서 userDetailsService와 authenticationManager는 필수
     * @info 로그인 후의 로직을 임의로 구성하려면 loginSuccessHandler 필요
     * @info userRepository는 로그인 실패 횟수 체크하려고 쓰는 것
     */
    @Bean
    LoginFilter loginFilter(AuthenticationManager authenticationManager) throws Exception {
      return new LoginFilter(
              "/api/user/login",
              userDetailsService,
              new LoginSuccessHandler(accessTokenService),
              userRepository,
              authenticationManager
      );
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
      return (request, response, accessDeniedException) -> {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
      };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    /**
     * @author 연상훈
     * @created 24/10/04
     * @updated 24/10/04
     * @see : 리액트-스프링부트 연결을 위한 cors config
     */
    private CorsConfigurationSource corsConfigurationSource() { // corsConfigurationSource
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080")); // 허용할 도메인 설정
        configuration.setAllowedMethods(Arrays.asList("*")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    /**
     * 로그아웃 필터
     * @author 연상훈
     * @created 2024-11-08 오전 1:01
     * @info 스프링부트 세큐리티에서 로그아웃 필터는 기본적으로 거의 맨 앞에 배치함
     * > 지금 세션은 사용하지 않고 토큰으로만 인증하기 때문에 거의 맨 앞이면 토큰 인증을 못 받음
     * > 필터 순서를 토큰 필터 뒤로 옮기려고 생성한 필터
     * @info 로그아웃 후 별도의 리다이렉트가 없고, 인증 정보를 모두 삭제하도록 구성함
     */
    private LogoutFilter logoutFilter() {
      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
      LogoutSuccessHandler logoutSuccessHandler = new LogoutSuccessHandler() {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            SecurityContextHolder.clearContext();
            log.info("----------------- 로그아웃 ----------------------");
            response.setStatus(HttpServletResponse.SC_OK);  // 200 OK 응답
            response.getWriter().write("logout success");  // JSON 응답
        }
      };
  
      LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessHandler, logoutHandler);
      logoutFilter.setFilterProcessesUrl("/api/user/logout");
  
      return logoutFilter;
    }

}
