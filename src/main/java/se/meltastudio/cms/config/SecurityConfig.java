package se.meltastudio.cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfigurationSource;
import se.meltastudio.cms.security.CustomUserDetailsService;
import se.meltastudio.cms.security.JwtRequestFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter, CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll() // 🔹 Alla kan logga in
                        .requestMatchers("/api/auth/forgot-password").permitAll() // 🔹 Password reset request
                        .requestMatchers("/api/auth/reset-password").permitAll() // 🔹 Password reset confirmation
                        .requestMatchers("/api/users/register/**").permitAll() // 🔹 Alla kan registrera sig
                        .requestMatchers("/api/util/**").permitAll() // 🔹 Utility endpoints
                        .requestMatchers("/api/users").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN")
                        .requestMatchers("/api/users/mechanics").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "MECHANIC", "OFFICE")
                        .requestMatchers("/api/users/create").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN")
                        .requestMatchers("/api/endcustomers").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/endcustomers/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/endcustomers/details/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN")
                        .requestMatchers("/api/vehicles/details/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/vehicles/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE", "MECHANIC")
                        .requestMatchers("/api/service/vehicle/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE", "MECHANIC")
                        .requestMatchers("/api/service-intervals").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE", "MECHANIC")
                        .requestMatchers("/api/service-intervals/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE", "MECHANIC")
                        .requestMatchers(HttpMethod.PATCH, "/api/workorders/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/workorders/statuses/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/workorders/vehicles/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/bookings").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/bookings/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/mechanics/**").hasAnyRole("SUPER_ADMIN", "MECHANIC")
                        .requestMatchers("/api/company/**").hasAnyRole("SUPER_ADMIN", "CUSTOMER_ADMIN", "WORKPLACE_ADMIN", "OFFICE")
                        .requestMatchers("/api/**").authenticated() // 🔹 Alla andra API:er kräver inloggning
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable()) // 🔹 Stäng av formulärbaserad inloggning
                .httpBasic(httpBasic -> httpBasic.disable()); // 🔹 Stäng av Basic Auth

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
