package de.uol.swp.server.lobby;

import de.uol.swp.server.model.LobbyDTO;
import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.test.support.WithMockServerUser;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class LobbyServiceTest {

    LobbyService lobbyService;

    @Mock
    SimpUser simpUser1;
    @Mock
    SimpUser simpUser2;
    Set<SimpUser> simpUsers;

    @Mock
    LobbyManagement lobbyManagement;
    @Mock
    SimpMessagingTemplate messagingTemplate;
    @Mock
    SimpUserRegistry userRegistry;
    @Mock
    UserMapping userMapping;
    @Mock
    LobbyMapping lobbyMapping;

    @BeforeEach
    void setup() {
        simpUsers = Set.of(simpUser1, simpUser2);
        lobbyService = new LobbyService(lobbyMapping, userMapping, userRegistry, messagingTemplate, lobbyManagement);
    }

    @Test
    @WithMockUser
    void testLobbyCreateWrongUser() { // This should never happen
        ResponseEntity<LobbyDTO> response = lobbyService.lobbyCreate("lobbyname");

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    @WithMockServerUser
    void testLobbyCreate() {
        ServerUser user = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        ServerLobby lobby = new ServerLobby("lobbyname", user);
        LobbyDTO lobbyDTO = new LobbyDTO().name("lobbyname").owner(new UserDTO().username("username"));
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.of(lobby));
        when(userRegistry.getUsers()).thenReturn(simpUsers);
        when(simpUser1.getName()).thenReturn("username1");
        when(simpUser2.getName()).thenReturn("username2");
        when(lobbyMapping.toDTO(lobby)).thenReturn(lobbyDTO);

        ResponseEntity<LobbyDTO> response = lobbyService.lobbyCreate("lobbyname");

        assertEquals(200, response.getStatusCode().value());
        verify(lobbyManagement).createLobby("lobbyname", user);
        verify(messagingTemplate).convertAndSendToUser("username1", "/topic/lobby/created", lobbyDTO);
        verify(messagingTemplate).convertAndSendToUser("username2", "/topic/lobby/created", lobbyDTO);
    }

    @Test
    @WithMockServerUser
    void testLobbyCreateAlreadyExists() {
        ServerUser user = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        ServerLobby lobby = new ServerLobby("lobbyname", user);
        LobbyDTO lobbyDTO = new LobbyDTO().name("lobbyname").owner(new UserDTO().username("username"));
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.of(lobby));
        when(userRegistry.getUsers()).thenReturn(simpUsers);
        when(simpUser1.getName()).thenReturn("username1");
        when(simpUser2.getName()).thenReturn("username2");
        when(lobbyMapping.toDTO(lobby)).thenReturn(lobbyDTO);

        lobbyService.lobbyCreate("lobbyname");

        doThrow(new IllegalArgumentException()).when(lobbyManagement).createLobby("lobbyname", user);

        ResponseEntity<LobbyDTO> response = lobbyService.lobbyCreate("lobbyname");

        assertEquals(400, response.getStatusCode().value());
        verify(lobbyManagement, times(2)).createLobby(eq("lobbyname"), any());
        verify(messagingTemplate, times(2)).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    @WithMockUser
    void testLobbyJoinWrongUser() { // This sould never happen
        ResponseEntity<LobbyDTO> response = lobbyService.lobbyJoin("lobbyname");

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    @WithMockServerUser
    void testLobbyJoin(@Mock ServerLobby lobby) {
        ServerUser user = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        ServerUser otherUser = new ServerUser("otherUsername", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        UserDTO userDTO = new UserDTO("username");
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.of(lobby));
        when(userMapping.toDTO(user)).thenReturn(userDTO);
        List<ServerUser> users = new ArrayList<>();
        users.add(otherUser);
        when(lobby.getUsers()).thenReturn(users);

        ResponseEntity<LobbyDTO> response = lobbyService.lobbyJoin("lobbyname");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby/join/lobbyname", new UserDTO().username("username"));
        verify(messagingTemplate).convertAndSendToUser("otherUsername", "/topic/lobby/join/lobbyname", new UserDTO().username("username"));
    }

    @Test
    @WithMockServerUser
    void testLobbyJoinAlreadyIn(@Mock ServerLobby lobby) {
        ServerUser user = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        ServerUser otherUser = new ServerUser("otherUsername", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        UserDTO userDTO = new UserDTO("username");
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.of(lobby));
        when(userMapping.toDTO(user)).thenReturn(userDTO);
        List<ServerUser> users = new ArrayList<>();
        users.add(otherUser);
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);

        ResponseEntity<LobbyDTO> response = lobbyService.lobbyJoin("lobbyname");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby/join/lobbyname", new UserDTO().username("username"));
        verify(messagingTemplate).convertAndSendToUser("otherUsername", "/topic/lobby/join/lobbyname", new UserDTO().username("username"));
    }

    @Test
    @WithMockServerUser
    void testLobbyJoinNonExistent() {
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.empty());

        ResponseEntity<LobbyDTO> response = lobbyService.lobbyJoin("lobbyname");

        assertEquals(404, response.getStatusCode().value());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    @Test
    @WithMockUser
    void testLobbyLeaveWrongUser() { // This should never happen
        ResponseEntity<Void> response = lobbyService.lobbyLeave("lobbyname");

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    @WithMockServerUser
    void testLobbyLeave(@Mock ServerLobby lobby) {
        ServerUser user = new ServerUser("username", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        ServerUser otherUser = new ServerUser("otherUsername", "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        UserDTO userDTO = new UserDTO("username");
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.of(lobby));
        when(userMapping.toDTO(user)).thenReturn(userDTO);
        List<ServerUser> users = new ArrayList<>();
        users.add(otherUser);
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);

        ResponseEntity<Void> response = lobbyService.lobbyLeave("lobbyname");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, users.size());
        assertTrue(users.contains(otherUser));
        verify(messagingTemplate).convertAndSendToUser("otherUsername", "/topic/lobby/leave/lobbyname", new UserDTO().username("username"));
    }

    @Test
    @WithMockServerUser
    void testLobbyLeaveNonExistent() {
        when(lobbyManagement.getLobby("lobbyname")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = lobbyService.lobbyLeave("lobbyname");

        assertEquals(404, response.getStatusCode().value());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

}
