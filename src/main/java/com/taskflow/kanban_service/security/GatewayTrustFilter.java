package com.taskflow.kanban_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class GatewayTrustFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(GatewayTrustFilter.class);
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean skip = path.contains("/v3/api-docs") || 
                       path.contains("/swagger-ui") || 
                       "OPTIONS".equalsIgnoreCase(request.getMethod());
        
        logger.info("shouldNotFilter - Path: {}, Method: {}, Skip: {}", path, request.getMethod(), skip);
        return skip;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String emailId = request.getHeader("X-User-Email");
        
        // DEBUG: Log all headers
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }

        logger.info("GatewayTrustFilter - Path: {}, X-User-Id: {}, X-User-Email: {}", 
                    request.getRequestURI(), userId, emailId);

        if (userId == null || userId.isEmpty()) {
            logger.error("Access Denied - Missing X-User-Id header for path: {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Request must come through Gateway");
            return;
        }

        logger.info("Authentication successful for userId: {}", userId);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
