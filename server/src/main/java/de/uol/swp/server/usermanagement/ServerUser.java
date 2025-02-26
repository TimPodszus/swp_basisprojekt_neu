package de.uol.swp.server.usermanagement;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Represents a user on the server.
 * This class extends the User class from Spring Security and
 * is a layer of abstraction between the Spring Security User
 * and the ServerUser.
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
public class ServerUser extends User {

    /**
     * Creates a new ServerUser with the given username, password and authorities.
     *
     * @param username    the username of the user
     * @param password    the password of the user (hashed)
     * @param authorities the authorities of the user
     * @throws IllegalArgumentException if any of the parameters is null
     * @since 2025-03-17
     */
    public ServerUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    /**
     * Creates a new ServerUser from an existing ServerUser.
     *
     * @param serverUser the ServerUser to copy
     * @throws NullPointerException if the given ServerUser is null
     * @since 2025-03-17
     */
    ServerUser(ServerUser serverUser) {
        super(serverUser.getUsername(), serverUser.getPassword(), serverUser.getAuthorities().stream().toList());
    }

}
