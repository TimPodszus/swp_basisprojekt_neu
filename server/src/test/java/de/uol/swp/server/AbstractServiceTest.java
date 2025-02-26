package de.uol.swp.server;

import de.uol.swp.server.lobby.LobbyMapping;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractServiceTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    AbstractService service;

    @Mock
    LobbyMapping lobbyMapping;
    @Mock
    UserMapping userMapping;
    @Mock
    SimpUserRegistry userRegistry;
    @Mock
    SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setup() {
        service = new AbstractService(lobbyMapping, userMapping, userRegistry, messagingTemplate) {
        };
    }

    @Test
    void testSendTo() {
        service.sendTo("username", "/topic/lobby", "message");

        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby", "message");
    }

    @Test
    void testSendToNullName() {
        assertThrows(NullPointerException.class, () -> service.sendTo((String) null, "/topic/lobby", "message"));
    }

    @Test
    void testSendToNullTopic() {
        assertThrows(NullPointerException.class, () -> service.sendTo("username", null, "message"));
    }

    @Test
    void testSendToNullMessage() {
        assertThrows(NullPointerException.class, () -> service.sendTo("username", "/topic/lobby", null));
    }

    @Test
    void testSendToUser() {
        ServerUser user = new ServerUser("username", "password", authorities);

        service.sendTo(user, "/topic/lobby", "message");

        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby", "message");
    }

    @Test
    void testSendToUserNullUser() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> service.sendTo((ServerUser) null, "/topic/lobby", "message"));
    }

    @Test
    void testSendToUserNullTopic() {
        ServerUser user = new ServerUser("username", "password", authorities);

        assertThrows(NullPointerException.class, () -> service.sendTo(user, null, "message"));
    }

    @Test
    void testSendToMany() {
        ServerUser user = new ServerUser("username", "password", authorities);
        ServerUser user2 = new ServerUser("username2", "password", authorities);
        service.sendToMany(List.of(user, user2), "/topic/lobby", "message");

        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby", "message");
        verify(messagingTemplate).convertAndSendToUser("username2", "/topic/lobby", "message");
    }

    @Test
    void testSendToManyNullList() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> service.sendToMany(null, "/topic/lobby", "message"));
    }

    @Test
    void testSendToManyEmptyList() {
        service.sendToMany(new ArrayList<>(), "/topic/lobby", "message");

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), anyString());
    }

    @Test
    void testSendToManyNullUser() {
        ServerUser user = new ServerUser("username", "password", authorities);

        List<ServerUser> users = new ArrayList<>();
        users.add(user);
        users.add(null);
        assertThrows(NullPointerException.class, () -> service.sendToMany(users, "/topic/lobby", "message"));

        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby", "message");
    }

    @Test
    void testSendToManyNullUserNever() {
        ServerUser user = new ServerUser("username", "password", authorities);

        List<ServerUser> users2 = new ArrayList<>();
        users2.add(null);
        users2.add(user);
        assertThrows(NullPointerException.class, () -> service.sendToMany(users2, "/topic/lobby", "message"));

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), anyString());
    }

    @Test
    void testSendToManyNullTopic() {
        ServerUser user = new ServerUser("username", "password", authorities);
        ServerUser user2 = new ServerUser("username2", "password", authorities);
        List<ServerUser> users = List.of(user, user2);

        assertThrows(NullPointerException.class, () -> service.sendToMany(users, null, "message"));
    }

    @Test
    void testSendToAll(@Mock SimpUser user, @Mock SimpUser user2) {
        when(user.getName()).thenReturn("username");
        when(user2.getName()).thenReturn("username2");
        when(userRegistry.getUsers()).thenReturn(Set.of(user, user2));

        service.sendToAll("/topic/lobby", "message");

        verify(messagingTemplate).convertAndSendToUser("username", "/topic/lobby", "message");
        verify(messagingTemplate).convertAndSendToUser("username2", "/topic/lobby", "message");
    }

    @Test
    void testSendToAllNullTopic(@Mock SimpUser user, @Mock SimpUser user2) {
        when(userRegistry.getUsers()).thenReturn(Set.of(user, user2));

        assertThrows(NullPointerException.class, () -> service.sendToAll(null, "message"));
    }

    @Test
    void testSendToAllNullMessage(@Mock SimpUser user, @Mock SimpUser user2) {
        when(userRegistry.getUsers()).thenReturn(Set.of(user, user2));

        assertThrows(NullPointerException.class, () -> service.sendToAll("/topic/lobby", null));
    }

    @Test
    void testSendToAllEmpty() {
        when(userRegistry.getUsers()).thenReturn(Set.of());

        service.sendToAll("/topic/lobby", "message");

        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), anyString());
    }

}
