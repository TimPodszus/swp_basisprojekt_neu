package de.uol.swp.server.lobby;

import de.uol.swp.server.AbstractService;
import de.uol.swp.server.api.LobbiesApi;
import de.uol.swp.server.api.LobbiesApiDelegate;
import de.uol.swp.server.model.LobbyDTO;
import de.uol.swp.server.model.UserDTO;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import io.github.springwolf.bindings.stomp.annotations.StompAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles the requests sent to endpoints starting with /lobby
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Component
public class LobbyService extends AbstractService implements LobbiesApiDelegate {

    private static final String LOBBY_CREATED_TOPIC = "/topic/lobby/created";
    private static final String LOBBY_JOIN_TOPIC = "/topic/lobby/join";
    private static final String LOBBY_LEAVE_TOPIC = "/topic/lobby/leave";

    private final LobbyManagement lobbyManagement;

    /**
     * Creates a new instance of the LobbyService
     *
     * @param lobbyManagement   The management class for creating, storing and deleting
     *                          lobbies
     * @param lobbyMapping      Mapping class for converting between ServerLobby and LobbyDTO
     * @param userMapping       Mapping class for converting between ServerUser and UserDTO
     * @param userRegistry      Registry for all users connected via websocket
     * @param messagingTemplate Template for sending messages to users
     * @since 2025-03-17
     */
    public LobbyService(LobbyMapping lobbyMapping, UserMapping userMapping, SimpUserRegistry userRegistry, SimpMessagingTemplate messagingTemplate, LobbyManagement lobbyManagement) {
        super(lobbyMapping, userMapping, userRegistry, messagingTemplate);
        this.lobbyManagement = lobbyManagement;
    }

    /**
     * POST /lobbies : Create Lobby
     * Create a new lobby
     *
     * @param lobbyname (required)
     * @return Lobby created. (status code 201)
     * or Lobby with name already exists. (status code 400)
     * or Unauthorized. (status code 401)
     * @see LobbiesApi#lobbyCreate
     */
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = LOBBY_CREATED_TOPIC,
            description = "A new lobby has been created",
            payloadType = LobbyDTO.class
    ))
    @StompAsyncOperationBinding
    @Override
    public ResponseEntity<LobbyDTO> lobbyCreate(String lobbyname) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(user instanceof ServerUser serverUser))
            return ResponseEntity.internalServerError().build();
        if (lobbyname.isBlank())
            return ResponseEntity.badRequest().build();

        try {
            lobbyManagement.createLobby(lobbyname, serverUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        ServerLobby lobby = lobbyManagement.getLobby(lobbyname).orElseThrow();
        sendToAll(LOBBY_CREATED_TOPIC, lobbyMapping.toDTO(lobby));
        return ResponseEntity.ok(lobbyMapping.toDTO(lobby));
    }

    /**
     * POST /lobbies/join : Join Lobby
     * Join a lobby
     *
     * @param lobbyname (required)
     * @return Lobby joined. (status code 200)
     * or Unauthorized. (status code 401)
     * or Lobby not found. (status code 404)
     * @see LobbiesApi#lobbyJoin
     */
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/user" + LOBBY_JOIN_TOPIC + "/{lobbyname}",
            description = "A user has joined a lobby",
            payloadType = UserDTO.class
    ))
    @StompAsyncOperationBinding
    @Override
    public ResponseEntity<LobbyDTO> lobbyJoin(String lobbyname) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(user instanceof ServerUser serverUser))
            return ResponseEntity.internalServerError().build();
        if (lobbyname.isBlank())
            return ResponseEntity.badRequest().build();

        Optional<ServerLobby> lobby = lobbyManagement.getLobby(lobbyname);
        if (lobby.isPresent()) {
            if (!lobby.get().getUsers().contains(serverUser)) {
                lobby.get().getUsers().add(serverUser);
            }
            sendToMany(lobby.get().getUsers(), LOBBY_JOIN_TOPIC + "/" + lobbyname, userMapping.toDTO(serverUser));
            return ResponseEntity.ok(lobbyMapping.toDTO(lobby.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /lobbies/leave : Leave Lobby
     * Leave a lobby
     *
     * @param lobbyname (required)
     * @return Left lobby successfully. (status code 200)
     * or Unauthorized. (status code 401)
     * or Lobby not found. (status code 404)
     * @see LobbiesApi#lobbyLeave
     */
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/user" + LOBBY_LEAVE_TOPIC + "/{lobbyname}",
            description = "A user has left a lobby",
            payloadType = UserDTO.class
    ))
    @StompAsyncOperationBinding
    @Override
    public ResponseEntity<Void> lobbyLeave(String lobbyname) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(user instanceof ServerUser serverUser))
            return ResponseEntity.internalServerError().build();
        if (lobbyname.isBlank())
            return ResponseEntity.badRequest().build();

        Optional<ServerLobby> lobby = lobbyManagement.getLobby(lobbyname);
        if (lobby.isPresent()) {
            lobby.get().getUsers().remove(serverUser);
            sendToMany(lobby.get().getUsers(), LOBBY_LEAVE_TOPIC + "/" + lobbyname, userMapping.toDTO(serverUser));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
