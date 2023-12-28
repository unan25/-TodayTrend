package com.todaytrend.apigatewayserver.config;

import com.todaytrend.apigatewayserver.config.exceptionpath.ExceptionPathManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

import static jakarta.ws.rs.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ServerAuthenticationConverter converter,
                                                         ReactiveAuthenticationManager manager) {

        AuthenticationWebFilter webFilter = getAuthenticationWebFilter(converter, manager);

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(formLoginSpec -> formLoginSpec.authenticationManager(manager))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler()))
                .authenticationManager(manager)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/users/follow").hasAuthority("USER")
                        .pathMatchers("/api/auth/deactivate").hasAuthority("USER")
                        .pathMatchers("/api/auth/change-password").hasAuthority("USER")
                        .pathMatchers("/api/users/updateProfile").hasAuthority("USER")
                        .pathMatchers("/api/post").hasAuthority("USER")
                        .pathMatchers("/api/post/like").hasAuthority("USER")
                        .pathMatchers("/api/post/liked").hasAuthority("USER")
                        .pathMatchers("/api/post",POST, PUT, DELETE).hasAuthority("USER")
                        .pathMatchers("/api/post/likeposts").hasAuthority("USER")
                        .pathMatchers("/api/post/comments",POST).hasAuthority("USER")
                        .pathMatchers("/api/post/comments/delete").hasAuthority("USER")
                        .pathMatchers("/api/post/comments/like").hasAuthority("USER")
                        .pathMatchers("/api/post/comments/liked").hasAuthority("USER")
                        .pathMatchers("/api/images**",POST, PUT, DELETE).hasAuthority("USER")
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/users/**").permitAll()
                        .pathMatchers("/api/post/**").permitAll()
                        .pathMatchers("/api/images/**").permitAll()
                        .pathMatchers("/api/notification/**").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(
                        exceptionHandlingSpec -> exceptionHandlingSpec
                                .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> {
                                    log.error("SecurityWebFilterChain 401 ", exchange.getRequest().getURI(), ex);
                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                }))
                                .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> {
                                    log.error("SecurityWebFilterChain 403 {}", exchange.getRequest().getURI(), denied);
                                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                })));

        return http.build();
    }

    // 인증 객체 컨버터와 인증 객체 매니저를 이용한 필터를 구성하는 내부 메서드
    private static AuthenticationWebFilter getAuthenticationWebFilter(ServerAuthenticationConverter converter, ReactiveAuthenticationManager manager) {
        // 인증 객체 매니저를 이용하여 필터 생성
        // 인증 객체 컨버터를 필터 등록
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(manager);
        webFilter.setServerAuthenticationConverter(converter);
        // 필터에 의해 인증이 실패했을 때 어떻게 처리할건지 설정
        webFilter.setAuthenticationFailureHandler(
                (exchange, exception) -> Mono.fromRunnable(() -> {
                    log.error("SecurityWebFilterChain 401 {}", exchange.getExchange().getRequest().getURI(), exception);
                    exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                })
        );

        return webFilter;
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (exchange, authentication) -> {
            ServerHttpResponse response = exchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.OK);
            log.info("logoutSuccessHandler 실행");
            return response.setComplete();
        };
    }
}
