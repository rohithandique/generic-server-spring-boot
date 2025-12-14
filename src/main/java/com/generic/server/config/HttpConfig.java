package com.generic.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class HttpConfig {

    @Bean
    @Profile("local")
    public SecurityFilterChain localLoginAndRedirectFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
        );
        http.formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/actuator/hawtio", false)
        );
        http.httpBasic(httpBasic -> {});
        return http.build();
    }

    /**
     * Configuration for the 'prod' profile (Default Authentication)
     * - This bean is unchanged and enforces login for all non-local profiles.
     */
    @Bean
    @Profile("!local")
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize -> authorize
                // All requests must be authenticated
                .anyRequest().authenticated()
        );

        http.formLogin(formLogin -> {});
        http.httpBasic(httpBasic -> {});

        return http.build();
    }
}