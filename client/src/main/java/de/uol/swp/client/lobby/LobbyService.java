package de.uol.swp.client.lobby;

import de.uol.swp.client.api.DefaultApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class that manages lobbies
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Component
@RequiredArgsConstructor
public class LobbyService {

    private final DefaultApi api;

    /**
     * Calls the API to create a new lobby
     *
     * @param name Name chosen for the new lobby
     * @since 2025-03-17
     */
    public void createLobby(String name) {
        api.lobbyCreateWithHttpInfo(name);
    }

    /**
     * Calls the API to join a specified lobby
     *
     * @param name Name of the lobby the user wants to join
     * @since 2025-03-17
     */
    public void joinLobby(String name) {
        api.lobbyJoinWithHttpInfo(name);
    }

}
