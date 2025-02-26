package de.uol.swp.client.websocket;

import de.uol.swp.client.websocket.handler.UserLoggedInHandler;
import de.uol.swp.client.websocket.handler.UserLoggedOutHandler;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for handling the Stomp session.
 * After the connection is established, the client subscribes to topics.
 *
 * @author Tilman Holube
 * @see WebSocketConnectionManager
 * @since 2025-03-17
 */
@Slf4j
@Component
@NonNullApi
@RequiredArgsConstructor
public class StompSessionHandler extends StompSessionHandlerAdapter {

    private final UserLoggedInHandler userLoggedInHandler;
    private final UserLoggedOutHandler userLoggedOutHandler;

    /**
     * This method is called after the STOMP session is connected.
     * It subscribes to topics and registers handlers for incoming messages.
     *
     * @param session          the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     * @since 2025-03-17
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/users/loggedIn", userLoggedInHandler);
        session.subscribe("/topic/users/loggedOut", userLoggedOutHandler);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error(exception.getMessage(), exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error(exception.getMessage(), exception);
    }

}
