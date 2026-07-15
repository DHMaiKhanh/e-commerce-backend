package com.yourdomain.ecommerce.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String reqId = request.getHeader("X-Request-Id");
        if (reqId == null || reqId.isBlank()) {
            reqId = UUID.randomUUID().toString();
        }
        MDC.put(REQUEST_ID, reqId);
        response.setHeader("X-Request-Id", reqId);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start;
            log.info("{} {} -> {} ({}ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), took);
            MDC.remove(REQUEST_ID);
        }
    }
}
