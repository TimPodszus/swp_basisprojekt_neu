package de.uol.swp.client.auth;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.fx.AppScene;
import de.uol.swp.client.fx.SceneManager;
import de.uol.swp.client.user.ClientUserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

/**
 * Manages the login window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-08
 */
@Component
public class LoginPresenter extends AbstractPresenter {

    private final SceneManager sceneManager;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    /**
     * Creates a new LoginPresenter
     *
     * @param userService  The UserService to handle user related requests
     * @param sceneManager The SceneManager to handle scene changes
     * @since 2025-03-17
     */
    public LoginPresenter(ClientUserService userService, SceneManager sceneManager) {
        super(userService);
        this.sceneManager = sceneManager;
    }

    /**
     * Method called when the login button is pressed
     * <p>
     * This Method is called when the login button is pressed. It takes the text
     * that was entered in the login and password fields and sends it to the UserService
     *
     * @see de.uol.swp.client.user.UserService
     * @since 2019-08-13
     */
    @FXML
    private void onLoginButtonPressed() {
        userService.login(loginField.getText(), passwordField.getText());
    }

    /**
     * Method called when the register button is pressed
     * <p>
     * This Method is called when the register button is pressed. It tells the SceneManager
     * to show the register scene.
     *
     * @see AppScene
     * @see de.uol.swp.client.fx.SceneManager
     * @since 2019-08-13
     */
    @FXML
    private void onRegisterButtonPressed() {
        sceneManager.showScene(AppScene.REGISTER);
    }

}
