package se.meltastudio.cms.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("Intercepted request: " + request.getRequestURI());
        System.out.println("Authorization Header: " + authorizationHeader); // Debug-logga

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("No JWT token found, skipping authentication for this request.");
            chain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);
        System.out.println("Extracted JWT Token: " + token);

        try {
            final String username = jwtUtil.extractUsername(token);
            System.out.println("Token belongs to user: " + username);
            final Set<String> roles = jwtUtil.extractRoles(token); // 🔹 Hämta roller från token

            final Long companyId = jwtUtil.extractCompanyId(token);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                // 🔹 Konvertera roller till Spring Securitys `GrantedAuthority`
                Set<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

                if (jwtUtil.validateToken(token)) {
                    CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, companyId); // 🔹 Skapa en instans av CustomUserDetails


                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("User authenticated: " + username + " with roles: " + roles + ", Company ID: " + companyId);

                } else {
                    System.out.println("Invalid JWT token");
                }
            }
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage());
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
