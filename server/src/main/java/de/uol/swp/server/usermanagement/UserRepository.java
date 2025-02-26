package de.uol.swp.server.usermanagement;

import java.util.Optional;

/**
 * The repository for the users.
 * This interface defines the methods that are needed to interact with the user repository.
 *
 * @author Tilman Holube
 * @see InMemoryUserRepository
 * @see ServerUserDetailsManager
 * @since 2025-03-17
 */
public interface UserRepository {

    /**
     * Saves a user in the repository.
     *
     * @param user The user to save
     * @return The saved user
     * @since 2025-03-17
     */
    ServerUser save(ServerUser user);

    /**
     * Finds a user by its username.
     *
     * @param username The username of the user
     * @return The user with the given username
     * @since 2025-03-17
     */
    Optional<ServerUser> findByUsername(String username);

    /**
     * Checks if a user with the given username exists.
     *
     * @param username The username to check
     * @return True if a user with the given username exists, false otherwise
     * @since 2025-03-17
     */
    boolean existsById(String username);

    /**
     * Deletes a user by its username.
     *
     * @param username The username of the user to delete
     * @since 2025-03-17
     */
    void deleteById(String username);

}
