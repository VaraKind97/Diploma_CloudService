package ru.netology.cloudservice.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtTokenFilter.class);

    private static final String AUTH_TOKEN_HEADER = "auth-token";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtBlacklistService blacklistService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider,
                          JwtBlacklistService blacklistService) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.blacklistService = blacklistService;
    }

    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.debug(
                "Processing {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        String token = request.getHeader(AUTH_TOKEN_HEADER);

        if (token != null) {

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (blacklistService.isBlacklisted(token)) {
                log.warn("Blacklisted JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {

                if (jwtTokenProvider.validateToken(token)) {

                    Claims claims = jwtTokenProvider.getClaimsFromToken(token);

                    String username = claims.getSubject();

                    List<String> roles =
                            (List<String>) claims.get("roles");

                    if (roles == null) {
                        roles = List.of();
                    }

                    List<SimpleGrantedAuthority> authorities =
                            roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    log.debug(
                            "User '{}' authenticated by JWT",
                            username
                    );
                }

            } catch (Exception e) {

                log.warn(
                        "Invalid JWT token",
                        e
                );

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        }

        filterChain.doFilter(request, response);
    }
}