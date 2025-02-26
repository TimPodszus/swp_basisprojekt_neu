package de.uol.swp.client.fx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class that manages which window/scene is currently shown
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Slf4j
@Component
public class SceneManager {

    private static final String DIALOG_STYLE_SHEET = "fxml/css/myDialog.css";

    private final FXMLLoaderFactory fxmlLoaderFactory;

    private final Map<AppScene, Scene> scenes = new EnumMap<>(AppScene.class);

    @Setter
    private Stage primaryStage;

    /**
     * Creates a new SceneManager.
     *
     * @param fxmlLoaderFactory The FXMLLoaderFactory to create FXMLLoaders
     * @see FXMLLoaderFactory
     * @see AppScene
     * @since 2025-03-17
     */
    public SceneManager(FXMLLoaderFactory fxmlLoaderFactory) {
        this.fxmlLoaderFactory = fxmlLoaderFactory;
    }

    // -----------------------------------------------------
    // Scene Initialization
    // -----------------------------------------------------

    /**
     * This method is called by the ClientApp to initialize all scenes.
     *
     * @throws IOException If the FXML file could not be loaded
     * @since 2025-03-17
     */
    public void initScenes() throws IOException {
        for (AppScene appScene : AppScene.values()) {
            Scene scene = initScene(appScene);
            scenes.put(appScene, scene);
        }
    }

    /**
     * Subroutine creating scenes from FXML files
     * <p>
     * This Method tries to create a scene from the FXML file specified by
     * the AppScene given to it. If the LOG-Level is set to Debug or higher loading
     * is written to the log.
     * If it fails to load the view an IOException is thrown.
     *
     * @return The Scene created from the FXML file
     * @throws IOException If the FXML file could not be loaded
     * @since 2025-03-17
     */
    private Scene initScene(AppScene appScene) throws IOException {
        Parent rootPane;
        FXMLLoader fxmlLoader = fxmlLoaderFactory.create();
        try {
            URL url = getClass().getResource(appScene.getFxmlPath());
            log.debug("Loading {}", url);
            fxmlLoader.setLocation(url);
            rootPane = fxmlLoader.load();
        } catch (IOException e) {
            throw new IOException(String.format("Could not load View! %s", e.getMessage()), e);
        }
        return new Scene(rootPane);
    }

    // -----------------------------------------------------
    // Error Dialog Handling
    // -----------------------------------------------------

    /**
     * Shows an error message inside an error alert
     *
     * @param title   The title of the error alert
     * @param message The error message
     * @since 2025-03-17
     */
    public void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, title + message);
            // based on: https://stackoverflow.com/questions/28417140/styling-default-javafx-dialogs/28421229#28421229
            DialogPane pane = a.getDialogPane();
            pane.getStylesheets().add(DIALOG_STYLE_SHEET);
            a.showAndWait();
        });
    }

    // -----------------------------------------------------
    // Scene Switching
    // -----------------------------------------------------

    /**
     * Switches the current scene and title to the given ones
     * <p>
     * This method switches the current scene and title to the ones given in the
     * AppScene object. It does so by setting the title of the primaryStage to the
     * title of the AppScene and the scene to the one stored in the scenes map for
     * the given AppScene.
     *
     * @param appScene The AppScene to switch to
     * @since 2025-03-17
     */
    public void showScene(AppScene appScene) {
        Platform.runLater(() -> {
            primaryStage.setTitle(appScene.getTitle());
            primaryStage.setScene(scenes.get(appScene));
            primaryStage.show();
        });
    }

}
