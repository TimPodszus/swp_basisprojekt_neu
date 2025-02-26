package de.uol.swp.server.lobby;

import de.uol.swp.server.usermanagement.ServerUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServerLobbyTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Test
    void testCreateLobby() {
        ServerUser owner = new ServerUser("username", "password", authorities);

        ServerLobby lobby = new ServerLobby("lobbyname", owner);

        assertEquals("lobbyname", lobby.getName());
        assertEquals(owner, lobby.getOwner());
        assertEquals(1, lobby.getUsers().size());
        assertEquals(owner, lobby.getUsers().getFirst());
    }

    @Test
    void testCreateLobbyNameNull() {
        ServerUser owner = new ServerUser("username", "password", authorities);

        assertThrows(NullPointerException.class, () -> new ServerLobby(null, owner));
    }

    @Test
    void testAddUser() {
        ServerUser owner = new ServerUser("username", "password", authorities);
        ServerUser user = new ServerUser("username", "password", authorities);
        ServerLobby lobby = new ServerLobby("lobbyname", owner);

        lobby.getUsers().add(user);

        assertEquals("lobbyname", lobby.getName());
        assertEquals(owner, lobby.getOwner());
        assertEquals(2, lobby.getUsers().size());
        assertTrue(lobby.getUsers().contains(owner));
        assertTrue(lobby.getUsers().contains(user));
    }

}
