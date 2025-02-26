package de.uol.swp.server.lobby;

import de.uol.swp.server.usermanagement.ServerUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LobbyManagementTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    LobbyManagement lobbyManagement;

    @BeforeEach
    void setup() {
        lobbyManagement = new LobbyManagement();
    }

    @Test
    void testCreateLobby() {
        ServerUser owner = new ServerUser("username", "password", authorities);

        lobbyManagement.createLobby("lobbyname", owner);

        ServerLobby lobby = lobbyManagement.getLobby("lobbyname").orElseThrow();
        assertEquals("lobbyname", lobby.getName());
        assertEquals(owner, lobby.getOwner());
    }

    @Test
    void testCreateLobbyMultiple() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        ServerUser owner2 = new ServerUser("username2", "password2", authorities);

        lobbyManagement.createLobby("lobbyname", owner);
        lobbyManagement.createLobby("lobbyname2", owner2);

        ServerLobby lobby = lobbyManagement.getLobby("lobbyname").orElseThrow();
        assertEquals("lobbyname", lobby.getName());
        assertEquals(owner, lobby.getOwner());
        ServerLobby lobby2 = lobbyManagement.getLobby("lobbyname2").orElseThrow();
        assertEquals("lobbyname2", lobby2.getName());
        assertEquals(owner2, lobby2.getOwner());
    }

    @Test
    void testCreateLobbyDuplicateName() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        lobbyManagement.createLobby("lobbyname", owner);

        assertThrows(IllegalArgumentException.class, () -> lobbyManagement.createLobby("lobbyname", owner));
    }

    @Test
    void testGetLobbyEmpty() {
        Optional<ServerLobby> optLobby = lobbyManagement.getLobby("nonexistent");
        assertTrue(optLobby.isEmpty());
    }

    @Test
    void testGetLobbyEmptyWithOthers() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        lobbyManagement.createLobby("lobbyname", owner);

        Optional<ServerLobby> optLobby = lobbyManagement.getLobby("nonexistent");
        assertTrue(optLobby.isEmpty());
    }

    @Test
    void testDropLobby() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        lobbyManagement.createLobby("lobbyname", owner);

        lobbyManagement.dropLobby("lobbyname");

        Optional<ServerLobby> lobby = lobbyManagement.getLobby("lobbyname");
        assertTrue(lobby.isEmpty());
    }

    @Test
    void testDropLobbyNonExistent() {
        assertThrows(IllegalArgumentException.class, () -> lobbyManagement.dropLobby("nonexistent"));
    }

}
