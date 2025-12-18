package com.generic.server.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.csrf.CsrfToken;

class HttpConfigTest {

  @Test
  void sendRedirect_withContinue_removesIt() throws IOException {
    // Arrange
    var delegate = mock(DefaultRedirectStrategy.class);
    var strategy = new HttpConfig.NoContinueRedirectStrategy();
    strategy.delegate = delegate;
    var request = mock(jakarta.servlet.http.HttpServletRequest.class);
    var response = mock(jakarta.servlet.http.HttpServletResponse.class);
    var url = "http://localhost:8080/some/path?continue&continue";

    // Act
    strategy.sendRedirect(request, response, url);

    // Assert
    verify(delegate).sendRedirect(request, response, "http://localhost:8080/some/path");
  }

  @Test
  void sendRedirect_withoutContinue_doesNothing() throws IOException {
    // Arrange
    var delegate = mock(DefaultRedirectStrategy.class);
    var strategy = new HttpConfig.NoContinueRedirectStrategy();
    strategy.delegate = delegate;
    var request = mock(jakarta.servlet.http.HttpServletRequest.class);
    var response = mock(jakarta.servlet.http.HttpServletResponse.class);
    var url = "http://localhost:8080/some/path";

    // Act
    strategy.sendRedirect(request, response, url);

    // Assert
    verify(delegate).sendRedirect(request, response, url);
  }

  @Test
  void sendRedirect_whenDelegateThrowsException_propagatesIt() throws IOException {
    // Arrange
    var delegate = mock(DefaultRedirectStrategy.class);
    var strategy = new HttpConfig.NoContinueRedirectStrategy();
    strategy.delegate = delegate;
    var request = mock(jakarta.servlet.http.HttpServletRequest.class);
    var response = mock(jakarta.servlet.http.HttpServletResponse.class);
    var url = "http://localhost:8080/some/path";
    doThrow(new IOException("Test exception")).when(delegate).sendRedirect(request, response, url);

    // Act & Assert
    assertThrows(IOException.class, () -> strategy.sendRedirect(request, response, url));
  }

  @Test
  void csrfCookieFilter_withToken_getsToken() throws ServletException, IOException {
    // Arrange
    var filter = new HttpConfig.CsrfCookieFilter();
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var filterChain = mock(FilterChain.class);
    var csrfToken = mock(CsrfToken.class);
    when(request.getAttribute(CsrfToken.class.getName())).thenReturn(csrfToken);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(csrfToken).getToken();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void csrfCookieFilter_withoutToken_doesNothing() throws ServletException, IOException {
    // Arrange
    var filter = new HttpConfig.CsrfCookieFilter();
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var filterChain = mock(FilterChain.class);
    var csrfToken = mock(CsrfToken.class);
    when(request.getAttribute(CsrfToken.class.getName())).thenReturn(null);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(csrfToken, never()).getToken();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void csrfCookieFilter_nullResponse_throwsException() {
    var filter = new HttpConfig.CsrfCookieFilter();
    var request = mock(HttpServletRequest.class);
    var filterChain = mock(FilterChain.class);

    // Assert that passing null to the @NonNull response parameter throws an exception
    assertThrows(
        NullPointerException.class,
        () -> {
          filter.doFilterInternal(request, null, filterChain);
        });
  }

  @Test
  void csrfCookieFilter_nullFilterChain_throwsException() {
    var filter = new HttpConfig.CsrfCookieFilter();
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);

    // Assert that passing null to the @NonNull filterChain parameter throws an exception
    assertThrows(
        NullPointerException.class,
        () -> {
          filter.doFilterInternal(request, response, null);
        });
  }
}
