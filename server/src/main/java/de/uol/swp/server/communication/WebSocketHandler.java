package de.uol.swp.server.communication;

import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import io.github.springwolf.bindings.stomp.annotations.StompAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * This class handles websocket events.
 * It listens for new connections and disconnections and sends a list
 * of all currently logged-in users to all logged-in users.
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler {

    private static final String LOGGED_IN_TOPIC = "/topic/users/loggedIn";
    private static final String LOGGED_OUT_TOPIC = "/topic/users/loggedOut";

    private final SimpMessagingTemplate messagingTemplate;
    private final UserMapping userMapping;
    private final UserDetailsManager userDetailsManager;

    /**
     * This method is called when a new websocket connection is established.
     * This event is published by the Spring framework.
     *
     * @param event The event that triggered this method
     * @since 2025-03-17
     */
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = LOGGED_IN_TOPIC,
            description = "A user has logged in",
            payloadType = UserDTO.class
    ))
    @StompAsyncOperationBinding
    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user == null) { // This should never happen
            log.warn("Unauthenticated websocket connected");
            return;
        }
        log.info("New connection: {}", user.getName());
        ServerUser serverUser = (ServerUser) userDetailsManager.loadUserByUsername(user.getName());
        messagingTemplate.convertAndSend(LOGGED_IN_TOPIC, userMapping.toDTO(serverUser));
    }

    /**
     * This method is called when a websocket connection is closed.
     * This event is published by the Spring framework.
     *
     * @param event The event that triggered this method
     * @since 2025-03-17
     */
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = LOGGED_OUT_TOPIC,
            description = "A user has logged out",
            payloadType = UserDTO.class
    ))
    @StompAsyncOperationBinding
    @EventListener
    public void onDisconnected(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user == null) { // This should never happen
            log.warn("Unauthenticated websocket disconnected");
            return;
        }
        log.info("Disconnected: {}", user.getName());
        ServerUser serverUser = (ServerUser) userDetailsManager.loadUserByUsername(user.getName());
        messagingTemplate.convertAndSend(LOGGED_OUT_TOPIC, userMapping.toDTO(serverUser));
    }

}
