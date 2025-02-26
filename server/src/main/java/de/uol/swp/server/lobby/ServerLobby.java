package de.uol.swp.server.lobby;

import de.uol.swp.server.usermanagement.ServerUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a lobby on the server
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ServerLobby {

    /**
     * The name of the lobby.
     * This is used to identify the lobby.
     *
     * @since 2025-03-17
     */
    @EqualsAndHashCode.Include
    private String name;

    /**
     * The owner of the lobby
     *
     * @since 2025-03-17
     */
    private ServerUser owner;

    /**
     * The users in the lobby including the owner
     *
     * @since 2025-03-17
     */
    private List<ServerUser> users = new ArrayList<>();

    /**
     * Creates a new lobby with the given name and owner.
     * The owner is automatically added to the list of users.
     *
     * @param name  the name of the lobby
     * @param owner the owner of the lobby
     * @since 2025-03-17
     */
    public ServerLobby(String name, ServerUser owner) {
        Objects.requireNonNull(name);
        this.name = name;
        this.owner = owner;
        this.users.add(owner);
    }

}
