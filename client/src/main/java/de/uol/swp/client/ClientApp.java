package de.uol.swp.client;


import de.uol.swp.client.fx.AppScene;
import de.uol.swp.client.fx.SceneManager;
import de.uol.swp.client.user.ClientUserService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * The application class of the client
 * <p>
 * This class handles the startup of the application.
 *
 * @author Marco Grawunder
 * @see javafx.application.Application
 * @since 2017-03-17
 */
@Slf4j
public class ClientApp extends Application {

    private ClientUserService userService;

    // -----------------------------------------------------
    // Java FX Methods
    // -----------------------------------------------------

    @Override
    public void start(Stage primaryStage) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("de.uol.swp.client");
        context.refresh();
        userService = context.getBean(ClientUserService.class);
        SceneManager sceneManager = context.getBean(SceneManager.class);
        sceneManager.initScenes();
        sceneManager.setPrimaryStage(primaryStage);
        sceneManager.showScene(AppScene.LOGIN);
        primaryStage.show();
    }

    @Override
    public void stop() {
        userService.logout();
    }

    // -----------------------------------------------------
    // JavFX Help method
    // -----------------------------------------------------

    /**
     * Default startup method for javafx applications
     *
     * @param args Any arguments given when starting the application
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        launch(args);
    }

}
