package de.uol.swp.server.usermanagement;

import de.uol.swp.server.AbstractService;
import de.uol.swp.server.api.UsersApi;
import de.uol.swp.server.api.UsersApiDelegate;
import de.uol.swp.server.lobby.LobbyMapping;
import de.uol.swp.server.model.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the requests sent to endpoints starting with /users
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Slf4j
@Service
public class UserService extends AbstractService implements UsersApiDelegate {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new instance of the UserService
     *
     * @param lobbyMapping       Mapping for converting between Lobby and LobbyDTO
     * @param userMapping        Mapping for converting between ServerUser and UserDTO
     * @param userRegistry       Registry for all users connected via websocket
     * @param messagingTemplate  Template for sending messages to users via websockets
     * @param userDetailsManager Manager for user details
     * @param passwordEncoder    Encoder for hashing passwords
     * @since 2025-03-17
     */
    public UserService(UserMapping userMapping, LobbyMapping lobbyMapping, SimpUserRegistry userRegistry, SimpMessagingTemplate messagingTemplate, UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        super(lobbyMapping, userMapping, userRegistry, messagingTemplate);
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /users : Create User
     * Create a new user
     *
     * @param name     (required)
     * @param password (required)
     * @return User created. (status code 201)
     * or User already exists or invalid input. (status code 400)
     * @see UsersApi#userCreate
     */
    @Override
    public ResponseEntity<Void> userCreate(String name, String password) {
        if (userDetailsManager.userExists(name) || name.isBlank() || password.isBlank()) {
            log.info("User already exists: {}", name);
            return ResponseEntity.badRequest().build();
        }

        userDetailsManager.createUser(new ServerUser(name, passwordEncoder.encode(password),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))));
        log.info("User created: {}", name);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /users/list : List of users
     * Get a list of all logged-in users
     *
     * @return List of users. (status code 200)
     * or Unauthorized. (status code 401)
     * @see UsersApi#userList
     */
    @Override
    public ResponseEntity<List<UserDTO>> userList() {
        List<UserDTO> loggedInUsernames = userRegistry.getUsers().stream()
                .map(SimpUser::getName)
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(loggedInUsernames);
    }

    /**
     * GET /users : Get the current user
     * Returns an object of the currently authenticated user.
     *
     * @return OK (status code 200)
     * or Unauthorized. (status code 401)
     * @see UsersApi#currentUser
     */
    @Override
    public ResponseEntity<UserDTO> currentUser() {
        Object principalObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principalObject instanceof ServerUser serverUser) {
            return ResponseEntity.ok(userMapping.toDTO(serverUser));
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
