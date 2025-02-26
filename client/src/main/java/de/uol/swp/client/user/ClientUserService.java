package de.uol.swp.client.user;

import de.uol.swp.client.model.UserDTO;

import java.util.List;

/**
 * An interface for all methods of the client user service
 * <p>
 * As the communication with the server is based on events, the
 * returns of the call must be handled by events
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public interface ClientUserService {

    /**
     * Login with username and password
     *
     * @param username the name of the user
     * @param password the password of the user
     * @since 2017-03-17
     */
    void login(String username, String password);

    /**
     * Log out from server
     *
     * @since 2017-03-17
     */
    void logout();

    /**
     * Create a new persistent user
     *
     * @param username the name of the user
     * @param password the password of the user
     * @since 2019-09-02
     */
    void createUser(String username, String password);

    /**
     * Deletes a user
     *
     * @since 2019-10-10
     */
    void dropUser();

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, these fields are not updated
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    void updateUser(UserDTO user);

    /**
     * Retrieve the list of all current logged-in users
     *
     * @return a list of all users
     * @since 2017-03-17
     */
    List<UserDTO> retrieveAllUsers();

}
