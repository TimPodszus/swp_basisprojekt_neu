package de.uol.swp.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * This class provides configuration for websocket connections.
 *
 * @author Tilman Holube
 * @see WebSocketSecurityConfig
 * @since 2025-03-17
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker. This method enables the simple broker
     * for the "/topic" destination. This means that messages sent to "/topic"
     * will be broadcast to all connected clients.
     *
     * @param config The MessageBrokerRegistry to configure
     * @since 2025-03-17
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    /**
     * This method registers the "/ws" and "/wsjs" endpoints for websocket connections.
     * The "/ws" endpoint is used by the Java client, while the "/wsjs" endpoint is used
     * by the JavaScript client. Both endpoints are used to establish a websocket connection.
     * See the SecurityConfig and WebsocketSecurityConfig classes for more information on
     * how these endpoints are secured.
     *
     * @param registry The StompEndpointRegistry to configure (provided by Spring)
     * @see SecurityConfig
     * @see WebSocketSecurityConfig
     * @since 2025-03-17
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/wsjs");
    }

}
