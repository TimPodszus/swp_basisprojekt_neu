package de.uol.swp.server.usermanagement;

import de.uol.swp.server.lobby.LobbyMapping;
import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.test.support.WithMockServerUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class UserServiceTest {

    UserService userService;

    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    SimpUserRegistry userRegistry;
    @Mock
    UserMapping userMapping;
    @Mock
    LobbyMapping lobbyMapping;
    @Mock
    UserDetailsManager userDetailsManager;
    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userService = new UserService(userMapping, lobbyMapping, userRegistry, messagingTemplate, userDetailsManager, passwordEncoder);
    }

    @Test
    void testCreateUserSuccess() {
        when(userDetailsManager.userExists("username")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hash");

        ResponseEntity<Void> response = userService.userCreate("username", "password");

        assertEquals(200, response.getStatusCode().value());
        verify(userDetailsManager).createUser(
                new ServerUser("username", "hash", List.of(new SimpleGrantedAuthority("ROLE_USER")))
        );
    }

    @Test
    void testCreateUserExists() {
        when(userDetailsManager.userExists("username")).thenReturn(true);

        ResponseEntity<Void> response = userService.userCreate("username", "password");

        assertEquals(400, response.getStatusCode().value());
        verify(userDetailsManager, never()).createUser(any());
    }

    @Test
    void testUserList() {
        SimpUser simpUser1 = mock(SimpUser.class);
        SimpUser simpUser2 = mock(SimpUser.class);
        when(userRegistry.getUsers()).thenReturn(Set.of(simpUser1, simpUser2));
        when(simpUser1.getName()).thenReturn("username1");
        when(simpUser2.getName()).thenReturn("username2");

        ResponseEntity<List<UserDTO>> response = userService.userList();

        assertEquals(200, response.getStatusCode().value());
        List<UserDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        assertTrue(body.contains(new UserDTO("username1")));
        assertTrue(body.contains(new UserDTO("username2")));
    }

    @Test
    void testUserListEmpty() {
        when(userRegistry.getUsers()).thenReturn(Set.of());

        ResponseEntity<List<UserDTO>> response = userService.userList();

        assertEquals(200, response.getStatusCode().value());
        List<UserDTO> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    @Test
    @WithMockServerUser
    void testCurrentUser() {
        ServerUser serverUser = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userMapping.toDTO(serverUser)).thenReturn(new UserDTO("username"));

        ResponseEntity<UserDTO> response = userService.currentUser();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(new UserDTO("username"), response.getBody());
    }

    @Test
    @WithMockUser
    void testCurrentUserInternalError() { // This should never happen
        ResponseEntity<UserDTO> response = userService.currentUser();

        assertEquals(500, response.getStatusCode().value());
    }

}
