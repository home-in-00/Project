package com.example.actionprice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 연상훈
 * @created 24/10/01 13:40
 * @updated 24/10/01 13:40
 * @info 스웨거 기본 세팅
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI openAPI(){

        // api 보안 스키마 선언.
        SecurityScheme apiKeyScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY) // 키 보안 스키마의 타입. api 타입의 보안 스키마 사용하겠다
            .in(SecurityScheme.In.HEADER) // http의 header 내에 api 키의 위치해야 한다
            .name("Authorization") // 스키마 이름(스웨거 내 헤더). Swagger 문서에서 사용할 헤더 이름을 "Authorization"으로 지정
            .scheme("Bearer Token"); // api 키의 타입 : Bearer 토큰

        // 보안 요구사항 설정.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token"); // "apiKey" 보안 스키마 사용

        return new OpenAPI().components(new Components().addSecuritySchemes("Bearer Token", apiKeyScheme)) // 보안 스키마를 openapi 컴포넌트에 추가
            .addSecurityItem(securityRequirement) // 보안 요구사항을 openapi에 추가
            .info(apiInfo());
    }

    // 특정 경로에 대한 그룹화 및 설정. openAPI와 별도로 기능함
    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder()
            .group("public")
            .packagesToScan("com.example.actionprice")
            .pathsToMatch("/**") // 오류뜨면 /**
            .build();
    }

    private Info apiInfo(){
        return new Info()
            .title("Boot 01 Proeject Swagger")
            .description("Swagger")
            .version("1.0.0");
    }

}
