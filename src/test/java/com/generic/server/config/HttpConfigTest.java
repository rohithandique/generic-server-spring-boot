package com.generic.server.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;

import static org.mockito.Mockito.*;

class HttpConfigTest {

    @Test
    void sendRedirect_withContinue_removesIt() throws IOException {
        // Arrange
        var delegate = mock(DefaultRedirectStrategy.class);
        var strategy = new HttpConfig.NoContinueRedirectStrategy();
        strategy.delegate = delegate;
        var request = mock(jakarta.servlet.http.HttpServletRequest.class);
        var response = mock(jakarta.servlet.http.HttpServletResponse.class);
        var url = "http://localhost:8080/some/path?continue";

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
        org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
            strategy.sendRedirect(request, response, url);
        });
    }
}