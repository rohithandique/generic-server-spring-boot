package com.generic.server.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpFilterTest {

    @InjectMocks
    private HttpFilter httpFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternal_ShouldRedirect_WhenUriEndsWithSlashAndIsNotRoot() throws ServletException, IOException {
        // Arrange
        String requestUri = "/api/resource/";
        when(request.getRequestURI()).thenReturn(requestUri);

        // Act
        httpFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpStatus.PERMANENT_REDIRECT.value());
        verify(response).setHeader(HttpHeaders.LOCATION, "/api/resource");
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_ShouldChain_WhenUriDoesNotEndWithSlash() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/resource");

        // Act
        httpFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldChain_WhenUriIsRoot() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/");

        // Act
        httpFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }
}