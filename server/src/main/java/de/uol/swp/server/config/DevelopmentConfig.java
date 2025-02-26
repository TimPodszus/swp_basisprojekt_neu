package de.uol.swp.server.config;

import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Configuration for development environment.
 * Adds 10 test users to the database.
 * This configuration is only active when the "dev" profile is active.
 *
 * @author Tilman Holube
 * @see UserRepository
 * @since 2025-03-17
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is called by the Spring Framework.
     * It adds 10 test users to the database.
     *
     * @since 2025-03-17
     */
    @PostConstruct
    public void userDetailsService() {
        for (int i = 0; i < 10; i++) {
            userRepository.save(new ServerUser(
                    "test" + i,
                    passwordEncoder.encode("test" + i),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            ));
        }
    }

}
