package de.uol.swp.client.websocket.handler;

import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.model.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserLoggedInHandlerTest {

    UserLoggedInHandler userLoggedInHandler;

    @Mock
    MainMenuPresenter mainMenuPresenter;
    @Mock
    StompHeaders headers;

    @BeforeEach
    void setup() {
        userLoggedInHandler = new UserLoggedInHandler(mainMenuPresenter);
    }

    @Test
    void testGetPayloadType() {
        Type type = userLoggedInHandler.getPayloadType(headers);

        assertEquals(UserDTO.class, type);
    }

    @Test
    void testHandleFrame() {
        UserDTO userDTO = new UserDTO().username("username");

        userLoggedInHandler.handleFrame(headers, userDTO);

        verify(mainMenuPresenter).userLoggedIn(userDTO);
    }

    @Test
    void testHandleFrameWithNullPayload() {
        assertDoesNotThrow(() -> userLoggedInHandler.handleFrame(headers, null));

        verify(mainMenuPresenter, never()).userLoggedIn(any());
    }

}
