package de.uol.swp.client.user;

import de.uol.swp.client.api.DefaultApi;
import de.uol.swp.client.model.UserDTO;
import de.uol.swp.client.websocket.WebSocketConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * This class is used to hide the communication details with the server.
 *
 * @author Marco Grawunder
 * @see ClientUserService
 * @since 2017-03-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserService implements ClientUserService {

    private final DefaultApi api;
    private final WebSocketConnectionManager webSocketConnectionManager;

    /**
     * Sets the username and password in the api and connects to the server
     * via the WebSocketConnectionManager
     *
     * @param username the name of the user
     * @param password the password of the user
     * @since 2017-03-17
     */
    @Override
    public void login(String username, String password) {
        api.getApiClient().setUsername(username);
        api.getApiClient().setPassword(password);

        webSocketConnectionManager.connect(username, password);
    }

    @Override
    public void logout() {
        api.getApiClient().setUsername(null);
        api.getApiClient().setPassword(null);

        webSocketConnectionManager.disconnect();
    }

    @Override
    public void createUser(String username, String password) {
        api.userCreateWithHttpInfo(username, password);
    }

    /**
     * Method to delete a users account
     * <p>
     * This method should send a request to delete a users account, but being not
     * implemented, it currently does nothing.
     */
    @Override
    public void dropUser() {
        //TODO: Implement me
    }

    @Override
    public void updateUser(UserDTO user) {
        // TODO implement me
    }

    @Override
    public List<UserDTO> retrieveAllUsers() {
        ResponseEntity<List<UserDTO>> response = api.userListWithHttpInfo();
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return Collections.emptyList();
        }
    }

}
