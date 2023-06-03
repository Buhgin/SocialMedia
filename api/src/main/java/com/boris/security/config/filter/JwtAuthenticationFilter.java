package com.boris.security.config.filter;

import com.boris.dao.repository.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwt;
        final String userEmail;
        try {
            // Do authentication filter if the user exists
            if (doFiltration(request, response, filterChain, authHeader))
                return;

            // Get token and extract user email from token
            jwt = authHeader.replace(BEARER, "");
            userEmail = jwtService.extractUsername(jwt);

            // Authentication the user
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                setAuthenticationDetails(request, jwt, userEmail);
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Error logging in: {}", exception.getMessage());
            catchException(response, exception);
        }
    }

    private void setAuthenticationDetails(HttpServletRequest request, String jwt, String userEmail) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        boolean isTokenValid = getTokenValidity(jwt);

        if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
            UsernamePasswordAuthenticationToken authenticationToken = getUsernamePasswordAuthenticationToken(userDetails);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private boolean doFiltration(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                                 String authHeader) throws IOException, ServletException {
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private Boolean getTokenValidity(String jwt) {
        return tokenRepository.findByToken(jwt)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
    }

    private void catchException(HttpServletResponse response, Exception exception) throws IOException {
        response.setHeader("error", exception.getMessage());
        response.setStatus(FORBIDDEN.value());

        Map<String, String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), error);
    }
}
