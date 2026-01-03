package se.meltastudio.cms.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.meltastudio.cms.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        se.meltastudio.cms.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Användare hittades inte: " + username));
        // 🔹 Konvertera roller till Spring Securitys `GrantedAuthority`
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // Prefixa med "ROLE_"
                .collect(Collectors.toSet());

        System.out.println("Laddar användare" + username);
        System.out.println("Tilldelade roller: " + authorities);

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()) // BCrypt-hashat lösenord
                .authorities(authorities) // 🔹 Använd set av roller
                .build();

        /*return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()) // Ska vara hashat med BCrypt
                .roles(user.getRole().toUpperCase()) // Konvertera roll till uppercase ("ADMIN", "USER")
                .build();*/
    }
}
