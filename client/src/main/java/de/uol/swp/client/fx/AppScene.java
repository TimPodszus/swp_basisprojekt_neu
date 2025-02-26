package de.uol.swp.client.fx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for the different scenes of the application
 *
 * @author Tilman Holube
 * @see SceneManager
 * @since 2025-03-17
 */
@Getter
@RequiredArgsConstructor
public enum AppScene {

    LOGIN("/fxml/LoginView.fxml", "Login"),
    REGISTER("/fxml/RegistrationView.fxml", "Register"),
    MAIN("/fxml/MainMenuView.fxml", "Main");

    /**
     * The path to the FXML file of the scene
     *
     * @since 2025-03-17
     */
    private final String fxmlPath;

    /**
     * The title of the scene to be displayed on the window
     *
     * @since 2025-03-17
     */
    private final String title;

}
