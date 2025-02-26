package de.uol.swp.client.fx;

import javafx.fxml.FXMLLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Class that provides instances of the FXMLLoader
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Component
@RequiredArgsConstructor
public class FXMLLoaderFactory {

    private final ApplicationContext context;

    /**
     * Creates a new FXMLLoader instance.
     * The controller factory is set to the Spring context.
     * This way the FXMLLoader can inject the controller with the dependencies it needs.
     *
     * @return A new FXMLLoader instance
     * @since 2025-03-17
     */
    public FXMLLoader create() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        return loader;
    }

}
