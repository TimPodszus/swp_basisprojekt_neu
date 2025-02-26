package de.uol.swp.server;

import de.uol.swp.server.lobby.LobbyMapping;
import de.uol.swp.server.usermanagement.ServerUser;
import de.uol.swp.server.usermanagement.UserMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.Collection;
import java.util.Objects;

/**
 * This class is the base for creating a new Service.
 * <p>
 * This class prepares the child classes to have the EventBus set and methods post
 * and sendToAll implemented in order to reduce unnecessary code repetition.
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@RequiredArgsConstructor
public abstract class AbstractService {

    protected final LobbyMapping lobbyMapping;
    protected final UserMapping userMapping;

    protected final SimpUserRegistry userRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Sends a message to a specific user with the given topic.
     *
     * @param username The username of the user to send the message to
     * @param topic    The topic to send the message to (e.g. "/topic/lobby")
     * @param message  The message to send
     * @since 2025-03-17
     */
    protected void sendTo(String username, String topic, Object message) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(topic);
        Objects.requireNonNull(message);
        messagingTemplate.convertAndSendToUser(username, topic, message);
    }

    /**
     * Sends a message to a specific user with the given topic.
     *
     * @param user    The user to send the message to
     * @param topic   The topic to send the message to (e.g. "/topic/lobby")
     * @param message The message to send
     * @since 2025-03-17
     */
    protected void sendTo(ServerUser user, String topic, Object message) {
        sendTo(user.getUsername(), topic, message);
    }

    /**
     * Sends a message to multiple users with the given topic.
     *
     * @param users   The users to send the message to
     * @param topic   The topic to send the message to (e.g. "/topic/lobby")
     * @param message The message to send
     * @since 2025-03-17
     */
    protected void sendToMany(Collection<ServerUser> users, String topic, Object message) {
        users.forEach(user -> sendTo(user, topic, message));
    }

    /**
     * Sends a message to all users logged-in with the given topic.
     *
     * @param topic   The topic to send the message to (e.g. "/topic/lobby")
     * @param message The message to send
     * @since 2025-03-17
     */
    protected void sendToAll(String topic, Object message) {
        userRegistry.getUsers().forEach(user -> sendTo(user.getName(), topic, message));
    }

}
