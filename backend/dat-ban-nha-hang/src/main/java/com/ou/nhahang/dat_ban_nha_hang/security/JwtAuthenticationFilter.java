package com.ou.nhahang.dat_ban_nha_hang.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final Long userId;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtUtil.extractUsername(jwt);
            userId = jwtUtil.extractUserId(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt)) {
                    // Create lightweight authentication token wrapping the username and userId
                    UserDetails userDetails = new User(username, "", new ArrayList<>());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userId, // We place userId in the credentials object to extract later in Controller
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Let the filter pass. Security filter chain upstream will block it naturally
            // with 403 Forbidden.
        }

        filterChain.doFilter(request, response);
    }
}
