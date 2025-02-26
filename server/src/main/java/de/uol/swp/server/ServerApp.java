package de.uol.swp.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class handles the startup of the server.
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@Slf4j
@SpringBootApplication
public class ServerApp {

    /**
     * Main Method
     * <p>
     * This method handles the creation of the server components and the start of
     * the server
     *
     * @param args Any arguments given when starting the application
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

}
