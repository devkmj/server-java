package kr.hhplus.be.server.infrastructure.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                MDC.put("userId", userId);
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}