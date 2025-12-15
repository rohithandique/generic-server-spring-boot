package com.generic.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
public class HttpConfig {

    @Bean
    public SecurityFilterChain localLoginAndRedirectFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
        );
        SavedRequestAwareAuthenticationSuccessHandler handler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/actuator/hawtio");
        handler.setAlwaysUseDefaultTargetUrl(false);
        handler.setRedirectStrategy(new NoContinueRedirectStrategy());
        http.formLogin(formLogin -> formLogin
                .successHandler(handler)
        );
        http.httpBasic(httpBasic -> {});
        return http.build();
    }

    static class NoContinueRedirectStrategy implements org.springframework.security.web.RedirectStrategy {
        org.springframework.security.web.DefaultRedirectStrategy delegate =
                new org.springframework.security.web.DefaultRedirectStrategy();
        @Override
        public void sendRedirect(
                jakarta.servlet.http.HttpServletRequest request,
                jakarta.servlet.http.HttpServletResponse response,
                String url
        ) throws java.io.IOException {
            String cleanedUrl = url.replace("\\?continue", "");
            delegate.sendRedirect(request, response, cleanedUrl);
        }
    }

}