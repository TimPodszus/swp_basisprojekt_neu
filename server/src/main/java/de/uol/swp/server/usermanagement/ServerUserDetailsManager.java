package de.uol.swp.server.usermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * Manages the user details on the server.
 * This class implements the UserDetailsManager interface from Spring Security
 * and is responsible for loading, creating, updating and deleting users.
 * It is used by the Spring Security framework to authenticate users and
 * by the UserService to implement api endpoints for user management.
 *
 * @author Tilman Holube
 * @see UserRepository
 * @see UserService
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class ServerUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public ServerUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ServerUser> optUser = userRepository.findByUsername(username);
        if (optUser.isPresent()) {
            ServerUser user = optUser.get();
            return new ServerUser(user.getUsername(), user.getPassword(),
                    Collections.unmodifiableCollection(user.getAuthorities()));
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public void createUser(UserDetails user) {
        userRepository.save(new ServerUser(user.getUsername(), user.getPassword(),
                Collections.unmodifiableCollection(user.getAuthorities())));
    }

    @Override
    public void updateUser(UserDetails user) {
        // TODO implement me
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO implement me
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }

}
