package com.lms.LeaveManagementSystem.security;

import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// @Component
@AllArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("JwtAuthFilter: incoming request {} {}",
                request.getMethod(), request.getRequestURI());

        String token = extractTokenFromHeader(request);
        if (token != null) {
            log.debug("JwtAuthFilter: got Bearer token, validating…");
            String email = jwtUtil.extractEmail(token);
            if (email != null && jwtUtil.validateToken(token, email)) {
                User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
                if (user != null) {
                    String roleName = user.getRole().name(); // e.g. "EMPLOYEE"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);
                    MyUserDetails userDetails = new MyUserDetails(user);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, // <-- principal is now MyUserDetails
                            null,
                            userDetails.getAuthorities());

                    // (Re‑enable if you want request details)
                    // auth.setDetails(new WebAuthenticationDetailsSource()
                    // .buildDetails(request));

                    // 🔍 Log the actual username and authorities on the auth token:
                    log.debug("Authenticating user={}, authorities={}",
                            userDetails.getUsername(),
                            userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // Extract JWT token from the Authorization header
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Extract email from the request's token
    private String extractEmailFromRequest(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            // Use JwtUtil to extract the email from the token
            return jwtUtil.extractEmail(token);
        }
        return null;
    }
}
