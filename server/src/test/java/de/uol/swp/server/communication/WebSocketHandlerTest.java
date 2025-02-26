package de.uol.swp.server.communication;

import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketHandlerTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    WebSocketHandler webSocketHandler;

    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    UserMapping userMapping;
    @Mock
    UserDetailsManager userDetailsManager;
    @Mock
    Principal principal;

    @BeforeEach
    void setup() {
        webSocketHandler = new WebSocketHandler(messagingTemplate, userMapping, userDetailsManager);
    }

    @Test
    void testOnConnected(@Mock SessionConnectedEvent event) {
        when(event.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("username");
        ServerUser serverUser = new ServerUser("username", "password", authorities);
        when(userDetailsManager.loadUserByUsername("username")).thenReturn(serverUser);
        UserDTO userDTO = new UserDTO("username");
        when(userMapping.toDTO(serverUser)).thenReturn(userDTO);

        webSocketHandler.onConnected(event);

        verify(messagingTemplate).convertAndSend("/topic/users/loggedIn", userDTO);
    }

    @Test
    void testOnConnectedNull(@Mock SessionConnectedEvent event) {
        when(event.getUser()).thenReturn(null);

        webSocketHandler.onConnected(event);

        verify(userDetailsManager, never()).loadUserByUsername(anyString());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void testOnDisconnected(@Mock SessionDisconnectEvent event) {
        when(event.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("username");
        ServerUser serverUser = new ServerUser("username", "password", authorities);
        when(userDetailsManager.loadUserByUsername("username")).thenReturn(serverUser);

        webSocketHandler.onDisconnected(event);

        verify(messagingTemplate).convertAndSend("/topic/users/loggedOut", userMapping.toDTO(serverUser));
    }

    @Test
    void testOnDisconnectedNull(@Mock SessionDisconnectEvent event) {
        when(event.getUser()).thenReturn(null);

        webSocketHandler.onDisconnected(event);

        verify(userDetailsManager, never()).loadUserByUsername(anyString());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

}
