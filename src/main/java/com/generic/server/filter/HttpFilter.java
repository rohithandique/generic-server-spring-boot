package com.generic.server.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HttpFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String requestUri = request.getRequestURI();
    if (requestUri.endsWith("/") && !requestUri.equals("/")) {
      String newUrl = requestUri.substring(0, requestUri.length() - 1);
      response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
      response.setHeader(HttpHeaders.LOCATION, newUrl);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
