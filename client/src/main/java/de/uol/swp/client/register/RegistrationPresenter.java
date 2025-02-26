package de.uol.swp.client.register;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.fx.AppScene;
import de.uol.swp.client.fx.SceneManager;
import de.uol.swp.client.user.ClientUserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

/**
 * Manages the registration window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
@Component
public class RegistrationPresenter extends AbstractPresenter {

    private final SceneManager sceneManager;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    /**
     * Creates a new RegistrationPresenter
     *
     * @param userService  The injected ClientUserService
     * @param sceneManager The injected SceneManager
     * @since 2025-03-17
     */
    public RegistrationPresenter(ClientUserService userService, SceneManager sceneManager) {
        super(userService);
        this.sceneManager = sceneManager;
    }

    /**
     * Method called when the cancel button is pressed
     * <p>
     * This Method is called when the cancel button is pressed. It tells the SceneManager
     * to show the login scene.
     *
     * @see de.uol.swp.client.fx.SceneManager
     * @since 2019-09-02
     */
    @FXML
    void onCancelButtonPressed() {
        sceneManager.showScene(AppScene.LOGIN);
    }

    /**
     * Method called when the register button is pressed
     * <p>
     * This Method is called when the register button is pressed. It tells the SceneManager
     * to show errors, if one of the fields is empty or the password fields are not equal.
     * If everything is filled in correctly the user service is requested to create
     * a new user.
     *
     * @see de.uol.swp.client.fx.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2019-09-02
     */
    @FXML
    void onRegisterButtonPressed() {
        final String username = loginField.getText();
        final String password = passwordField1.getText();
        final String title = "Invalid Credentials";
        if (username == null || username.isBlank()) {
            sceneManager.showError(title, "Username cannot be empty");
        } else if (!passwordField1.getText().equals(passwordField2.getText())) {
            sceneManager.showError(title, "Passwords are not equal");
        } else if (password == null || password.isBlank()) {
            sceneManager.showError(title, "Password cannot be empty");
        } else {
            userService.createUser(loginField.getText(), passwordField1.getText());
        }
    }

}
