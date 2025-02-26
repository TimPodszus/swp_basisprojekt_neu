package de.uol.swp.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

/**
 * This class provides basic security configuration for Spring Security.
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final CsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    private final XorCsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new XorCsrfTokenRequestAttributeHandler();

    /**
     * Creates a new PasswordEncoder. This PasswordEncoder is used to encode
     * and verify passwords. It is primarily used by Spring Security to
     * automatically hash passwords to authenticate HTTP requests.
     *
     * @return A new PasswordEncoder
     * @since 2025-03-17
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Creates a new DaoAuthenticationProvider. This DaoAuthenticationProvider
     * is used to authenticate users based on the given UserDetailsService.
     * It is primarily used in the WebSocketSecurityConfig to authenticate
     * WebSocket connections for the JavaScript client.
     *
     * @return A new DaoAuthenticationProvider
     * @see WebSocketSecurityConfig
     * @since 2025-03-17
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * Creates a new SecurityFilterChain for the API. This SecurityFilterChain
     * is used to secure requests to the API. It requires all requests to
     * be authenticated using HTTP Basic authentication.
     * The endpoint "/api/users" with POST is excluded from authentication.
     * Additionally, it handles authentication for the WebSocket endpoint "/ws".
     *
     * @param http The HttpSecurity object to configure (provided by Spring)
     * @return A new SecurityFilterChain for the API
     * @throws Exception If an error occurs while building the SecurityFilterChain
     * @since 2025-03-17
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/ws")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Creates a new SecurityFilterChain for the WebSocket JavaScript client.
     * This SecurityFilterChain allows all requests to the Endpoint "/wsjs".
     * Authentication is handled by the WebSocketSecurityConfig.
     *
     * @param http The HttpSecurity object to configure (provided by Spring)
     * @return A new SecurityFilterChain for the WebSocket JavaScript client
     * @throws Exception If an error occurs while building the SecurityFilterChain
     * @see WebSocketSecurityConfig
     * @since 2025-03-17
     */
    @Bean
    @Order(1)
    public SecurityFilterChain wsjsFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/wsjs")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(csrfTokenRepository);
                    csrf.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler);
                })
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * Creates a new SecurityFilterChain for the JavaScript client.
     *
     * @param http The HttpSecurity object to configure (provided by Spring)
     * @return A new SecurityFilterChain for the JavaScript client
     * @throws Exception If an error occurs while building the SecurityFilterChain
     * @since 2025-03-17
     */
    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .logout(Customizer.withDefaults())
                .formLogin(c -> c.loginPage("/"))
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(csrfTokenRepository);
                    csrf.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler);
                });
        return http.build();
    }

}
