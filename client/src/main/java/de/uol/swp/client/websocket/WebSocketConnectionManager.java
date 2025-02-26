package de.uol.swp.client.websocket;

import de.uol.swp.client.api.DefaultApi;
import de.uol.swp.client.fx.AppScene;
import de.uol.swp.client.fx.SceneManager;
import de.uol.swp.client.websocket.event.LoggedInEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for managing the WebSocket connection.
 * It connects to the WebSocket and disconnects from it. It also
 * provides the StompSession that is used to send and receive messages.
 *
 * @author Tilman Holube
 * @see de.uol.swp.client.user.UserService
 * @see StompSessionHandler
 * @since 2025-03-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketConnectionManager {

    private final SceneManager sceneManager;
    private final ApplicationContext context;
    private final DefaultApi api;

    /**
     * This session can be used to subscribe to topics.
     * It is null if the connection is not established.
     *
     * @since 2025-03-17
     */
    @Getter
    private StompSession session = null;

    /**
     * Connects to the WebSocket using the given username and password
     * and authenticating with Basic Auth.
     * This method may return before the connection is established.
     *
     * @param username the username
     * @param password the password
     * @since 2025-03-17
     */
    public void connect(String username, String password) {
        log.debug("Connecting to WebSocket");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);

        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // taken from context to avoid circular dependency
        final StompSessionHandler stompSessionHandler = context.getBean(StompSessionHandler.class);

        CompletableFuture<StompSession> sessionCompletableFuture = stompClient.connectAsync(getURL(), headers, stompSessionHandler);
        sessionCompletableFuture.thenApply(stompSession -> {
            session = stompSession;
            sceneManager.showScene(AppScene.MAIN);
            context.publishEvent(new LoggedInEvent() {
            });
            log.debug("Connected to WebSocket");
            return session;
        });
        sessionCompletableFuture.exceptionally(throwable -> {
            sceneManager.showError("Login failed", "Login failed. Please check your credentials.");
            log.error("Failed to connect to WebSocket", throwable);
            return null;
        });
    }

    /**
     * Disconnects from the WebSocket.
     * This method may return before the connection is closed.
     *
     * @see de.uol.swp.client.user.UserService
     * @since 2025-03-17
     */
    public void disconnect() {
        log.debug("Disconnecting from WebSocket");
        if (session != null) {
            session.disconnect();
            session = null;
        }
        log.debug("Disconnected from WebSocket");
    }

    private String getURL() {
        return api.getApiClient().getBasePath()
                .replace("http:", "ws:")
                .replace("/api", "/ws");
    }

}
