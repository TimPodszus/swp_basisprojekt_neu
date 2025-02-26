package de.uol.swp.server.lobby;

import de.uol.swp.server.model.LobbyDTO;
import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyMappingTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    @InjectMocks
    LobbyMapping mapper = new LobbyMappingImpl();

    @Mock
    UserMapping userMapping;

    @Test
    void testToDTO() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        UserDTO ownerDTO = new UserDTO("username");
        when(userMapping.toDTO(owner)).thenReturn(ownerDTO);
        ServerLobby serverLobby = new ServerLobby("lobbyname", owner);

        LobbyDTO lobbyDTO = mapper.toDTO(serverLobby);

        assertEquals("lobbyname", lobbyDTO.getName());
        assertEquals(ownerDTO, lobbyDTO.getOwner());
        assertEquals(1, lobbyDTO.getUsers().size());
        assertEquals(ownerDTO, lobbyDTO.getUsers().getFirst());
    }

    @Test
    void testToDTONull() {
        LobbyDTO lobbyDTO = mapper.toDTO(null);
        assertNull(lobbyDTO);
    }

    @Test
    void testToDTOWithUsers() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        ServerUser user = new ServerUser("username2", "password2", authorities);
        UserDTO ownerDTO = new UserDTO("username");
        UserDTO userDTO = new UserDTO("username2");
        when(userMapping.toDTO(owner)).thenReturn(ownerDTO);
        when(userMapping.toDTO(user)).thenReturn(userDTO);
        ServerLobby serverLobby = new ServerLobby("lobbyname", owner);
        serverLobby.getUsers().add(user);

        LobbyDTO lobbyDTO = mapper.toDTO(serverLobby);

        assertEquals("lobbyname", lobbyDTO.getName());
        assertEquals(ownerDTO, lobbyDTO.getOwner());
        assertEquals(2, lobbyDTO.getUsers().size());
        assertTrue(lobbyDTO.getUsers().contains(ownerDTO));
        assertTrue(lobbyDTO.getUsers().contains(userDTO));
    }

    @Test
    void testToDTOWithUsersNull() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        ServerUser user = new ServerUser("username2", "password2", authorities);
        UserDTO ownerDTO = new UserDTO("username");
        UserDTO userDTO = new UserDTO("username2");
        when(userMapping.toDTO(owner)).thenReturn(ownerDTO);
        when(userMapping.toDTO(user)).thenReturn(userDTO);
        ServerLobby serverLobby = new ServerLobby("lobbyname", owner);
        serverLobby.getUsers().add(user);

        LobbyDTO lobbyDTO = mapper.toDTO(serverLobby);

        assertEquals("lobbyname", lobbyDTO.getName());
        assertEquals(ownerDTO, lobbyDTO.getOwner());
        assertEquals(2, lobbyDTO.getUsers().size());
        assertTrue(lobbyDTO.getUsers().contains(ownerDTO));
        assertTrue(lobbyDTO.getUsers().contains(userDTO));
    }

}
