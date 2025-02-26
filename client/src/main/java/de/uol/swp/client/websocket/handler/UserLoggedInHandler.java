package de.uol.swp.client.websocket.handler;

import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.model.UserDTO;
import de.uol.swp.client.websocket.StompSessionHandler;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * This handler is responsible for handling the user that is sent by the server
 * when a user logs in. It expects an UserDTO object and updates the user
 * list in the main menu.
 *
 * @author Tilman Holube
 * @see MainMenuPresenter
 * @see StompSessionHandler
 * @since 2025-03-17
 */
@Slf4j
@Component
@NonNullApi
@RequiredArgsConstructor
public class UserLoggedInHandler implements StompFrameHandler {

    private final MainMenuPresenter mainMenuPresenter;

    /**
     * Returns the type of the payload. In this case it is an UserDTO object.
     *
     * @param headers the headers of a message
     * @return the type of the payload
     * @since 2025-03-17
     */
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return UserDTO.class;
    }

    /**
     * Handles the frame that was received. It expects an UserDTO object and
     * updates the user list in the main menu.
     *
     * @param headers the headers of the frame
     * @param payload the payload, or {@code null} if there was no payload
     * @since 2025-03-17
     */
    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
        if (payload == null) {
            log.warn("Received unexpected null payload");
            return;
        }
        log.debug("Received user: {}", payload);
        UserDTO user = (UserDTO) payload;
        mainMenuPresenter.userLoggedIn(user);
    }

}
