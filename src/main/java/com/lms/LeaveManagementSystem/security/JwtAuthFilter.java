package com.lms.LeaveManagementSystem.security;

import com.lms.LeaveManagementSystem.entity.User;
import com.lms.LeaveManagementSystem.repository.UserRepository;
import com.lms.LeaveManagementSystem.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromHeader(request);
        if (token != null) {
            String email = jwtUtil.extractEmail(token);
            if (email != null && jwtUtil.validateToken(token, email)) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    // Map your Role enum to a Spring authority
                    String roleName = user.getRole().name(); // "MANAGER"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user, null, List.of(authority));
                    // auth.setDetails(new WebAuthenticationDetailsSource()
                    // .buildDetails(request));
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
