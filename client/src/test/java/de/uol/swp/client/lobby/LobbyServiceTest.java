package de.uol.swp.client.lobby;

import de.uol.swp.client.api.DefaultApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    LobbyService lobbyService;

    @Mock
    DefaultApi api;

    @BeforeEach
    void setup() {
        lobbyService = new LobbyService(api);
    }

    @Test
    void testCreateLobby() {
        lobbyService.createLobby("name");

        verify(api).lobbyCreateWithHttpInfo("name");
    }

    @Test
    void testJoinLobby() {
        lobbyService.joinLobby("name");

        verify(api).lobbyJoinWithHttpInfo("name");
    }

}
