package de.uol.swp.server.lobby;

import de.uol.swp.server.usermanagement.ServerUser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages creation, deletion and storing of lobbies
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@Component
public class LobbyManagement {

    private final Map<String, ServerLobby> lobbies = new HashMap<>();

    /**
     * Creates a new lobby and adds it to the list
     *
     * @param name  the name of the lobby to create
     * @param owner the user who wants to create a lobby
     * @throws IllegalArgumentException name already taken
     * @implNote the primary key of the lobbies is the name therefore the name has
     * to be unique
     * @see ServerUser
     * @since 2019-10-08
     */
    public void createLobby(String name, ServerUser owner) {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " already exists!");
        }
        lobbies.put(name, new ServerLobby(name, owner));
    }

    /**
     * Deletes lobby with requested name
     *
     * @param name String containing the name of the lobby to delete
     * @throws IllegalArgumentException there exists no lobby with the  requested
     *                                  name
     * @since 2019-10-08
     */
    public void dropLobby(String name) {
        if (!lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " not found!");
        }
        lobbies.remove(name);
    }

    /**
     * Searches for the lobby with the requested name
     *
     * @param name String containing the name of the lobby to search for
     * @return either empty Optional or Optional containing the lobby
     * @see Optional
     * @since 2019-10-08
     */
    public Optional<ServerLobby> getLobby(String name) {
        ServerLobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

}
