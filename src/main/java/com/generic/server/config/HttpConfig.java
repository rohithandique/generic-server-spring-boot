package com.generic.server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class HttpConfig {

  private static final String[] SWAGGER_PATHS = {"/swagger-ui/**", "/v3/api-docs/**"};

  private static final String[] ACTUATOR_CSRF_EXCLUDED_PATHS = {
    "/actuator/hawtio/**", "/actuator/jolokia/**",
  };

  @Bean
  public SecurityFilterChain localLoginAndRedirectFilterChain(HttpSecurity http) throws Exception {

    http.csrf(
        csrf ->
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(ACTUATOR_CSRF_EXCLUDED_PATHS));

    http.addFilterAfter(
        new CsrfCookieFilter(),
        org.springframework.security.web.access.intercept.AuthorizationFilter.class);

    http.authorizeHttpRequests(
        authorize ->
            authorize
                .requestMatchers(HttpMethod.GET, SWAGGER_PATHS)
                .authenticated()
                .requestMatchers("/actuator/**")
                .hasAnyRole("ACTUATOR", "ADMIN")
                .anyRequest()
                .authenticated());
    SavedRequestAwareAuthenticationSuccessHandler handler =
        new SavedRequestAwareAuthenticationSuccessHandler();
    handler.setDefaultTargetUrl("/actuator/hawtio");
    handler.setAlwaysUseDefaultTargetUrl(false);
    handler.setRedirectStrategy(new NoContinueRedirectStrategy());
    http.formLogin(formLogin -> formLogin.successHandler(handler));
    http.httpBasic(httpBasic -> {});
    return http.build();
  }

  static class NoContinueRedirectStrategy
      implements org.springframework.security.web.RedirectStrategy {
    org.springframework.security.web.DefaultRedirectStrategy delegate =
        new org.springframework.security.web.DefaultRedirectStrategy();

    @Override
    public void sendRedirect(
        jakarta.servlet.http.HttpServletRequest request,
        jakarta.servlet.http.HttpServletResponse response,
        String url)
        throws java.io.IOException {
      String cleanedUrl = url.replace("?continue", "").replace("&continue", "");
      delegate.sendRedirect(request, response, cleanedUrl);
    }
  }

  /** Filter to force the loading of the deferred CSRF token so it is sent as a cookie. */
  static class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {
      CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
      if (csrfToken != null) {
        // Accessing the token forces its generation and inclusion in the response cookie
        csrfToken.getToken();
      }
      filterChain.doFilter(request, response);
    }
  }
}
