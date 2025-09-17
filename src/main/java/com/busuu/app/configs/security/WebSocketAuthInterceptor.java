package com.busuu.app.configs.security;

import com.busuu.app.components.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        try {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String token = servletRequest.getParameter("auth-token");

            if (token == null || token.isBlank()) {
                log.warn("Missing WebSocket auth token");
                return false;
            }

            if (jwtTokenUtil.isTokenExpired(token)) {
                log.warn("Expired WebSocket token");
                return false;
            }

            String userId = jwtTokenUtil.extractUserId(token);

            Principal principal = () -> userId;
            attributes.put("principal", principal);

            log.info("WebSocket handshake success: userId={}", userId);
            return true;

        } catch (Exception e) {
            log.error("WebSocket auth failed: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }
}


