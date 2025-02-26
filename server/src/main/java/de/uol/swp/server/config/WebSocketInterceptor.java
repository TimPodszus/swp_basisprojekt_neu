package de.uol.swp.server.config;

import io.micrometer.common.lang.NonNullApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Intercepts WebSocket messages and authenticates users.
 * <p>
 * This class is responsible for intercepting WebSocket messages and authenticating users when
 * they connect to the WebSocket. It uses the {@link DaoAuthenticationProvider} to authenticate
 * users based on their username and password. If the user is already authenticated via HTTP Basic,
 * the user is not authenticated again.
 * This class is used by the {@link WebSocketSecurityConfig} to intercept WebSocket messages.
 *
 * @author Tilman Holube
 * @see WebSocketSecurityConfig
 * @since 2025-03-17
 */
@Slf4j
@NonNullApi
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class WebSocketInterceptor implements ChannelInterceptor {

    private final DaoAuthenticationProvider daoAuthenticationProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.debug("preSend: {}", message);
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) { // This should never happen
            throw new AccessDeniedException("Access denied; Internal error");
        }

        // This is true if the user has already authenticated via HTTP Basic
        if (accessor.getUser() != null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            final String username = accessor.getLogin();
            final String password = accessor.getPasscode();

            var token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticatedUser = daoAuthenticationProvider.authenticate(token);
            if (authenticatedUser == null) {
                throw new AccessDeniedException("Access denied");
            }
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            accessor.setUser(authenticatedUser);
            log.debug("User {} authenticated", username);
        }
        return message;
    }

}
