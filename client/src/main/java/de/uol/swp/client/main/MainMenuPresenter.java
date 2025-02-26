package de.uol.swp.client.main;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.model.UserDTO;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.client.websocket.event.LoggedInEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
@Slf4j
@Component
public class MainMenuPresenter extends AbstractPresenter {

    private final LobbyService lobbyService;

    @FXML
    private ListView<String> usersView;

    private ObservableList<String> users;

    /**
     * Creates a new MainMenuPresenter
     *
     * @param userService  The injected ClientUserService
     * @param lobbyService The injected LobbyService
     * @since 2025-03-17
     */
    public MainMenuPresenter(ClientUserService userService, LobbyService lobbyService) {
        super(userService);
        this.lobbyService = lobbyService;
    }

    /**
     * If a LoggedInEvent is published using the ApplicationEventPublisher, this method
     * is called. It retrieves all currently logged-in users and updates the user
     * list in the main menu.
     *
     * @param ignored The LoggedInEvent that was published
     * @see de.uol.swp.client.websocket.WebSocketConnectionManager
     * @since 2025-03-17
     */
    @EventListener
    public void onLoggedInEvent(LoggedInEvent ignored) {
        updateUsersList(userService.retrieveAllUsers());
    }

    /**
     * Updates the main menus user list according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @param userList A list of UserDTO objects including all currently logged-in users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.client.model.UserDTO
     * @since 2019-08-29
     */
    private void updateUsersList(List<UserDTO> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }

    /**
     * Method called when the create lobby button is pressed
     * <p>
     * If the create lobby button is pressed, this method requests the lobby service
     * to create a new lobby. Therefore, it currently uses the lobby name "test".
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby() {
        lobbyService.createLobby("test");
    }

    /**
     * Method called when the join lobby button is pressed
     * <p>
     * If the join lobby button is pressed, this method requests the lobby service
     * to join a specified lobby. Therefore, it currently uses the lobby name "test".
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onJoinLobby() {
        lobbyService.joinLobby("test");
    }

    /**
     * Method is called when a new user logs in
     *
     * @param user The user that logged in
     * @see de.uol.swp.client.websocket.handler.UserLoggedInHandler
     * @since 2025-03-17
     */
    public void userLoggedIn(UserDTO user) {
        Platform.runLater(() -> users.add(user.getUsername()));
    }

    /**
     * Method is called when a new logs out
     *
     * @param user The user that logged out
     * @see de.uol.swp.client.websocket.handler.UserLoggedOutHandler
     * @since 2025-03-17
     */
    public void userLoggedOut(UserDTO user) {
        Platform.runLater(() -> users.remove(user.getUsername()));
    }

}
