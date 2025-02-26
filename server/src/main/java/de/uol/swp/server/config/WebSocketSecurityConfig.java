package de.uol.swp.server.config;

import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * This class provides security configuration for websocket connections.
 * It makes sure that only authenticated users can connect to the websocket.
 * <p>
 * This configuration is based on the Spring Documentation, which can be found
 * <a href="https://docs.spring.io/spring-framework/reference/web/websocket/stomp/authentication-token-based.html">here</a>
 *
 * @author Tilman Holube
 * @see SecurityConfig
 * @see WebSocketConfig
 * @see WebSocketInterceptor
 * @since 2025-03-17
 */
@Slf4j
@NonNullApi
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final DaoAuthenticationProvider daoAuthenticationProvider;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketInterceptor(daoAuthenticationProvider));
    }

}
