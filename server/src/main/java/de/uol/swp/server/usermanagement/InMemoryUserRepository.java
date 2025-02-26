package de.uol.swp.server.usermanagement;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In memory implementation of the UserRepository.
 * This implementation stores the users in a map in memory.
 *
 * @author Tilman Holube
 * @see ServerUserDetailsManager
 * @since 2025-03-17
 */
@Component
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, ServerUser> usersMap = new HashMap<>();

    @Override
    public ServerUser save(ServerUser user) {
        usersMap.put(user.getUsername(), new ServerUser(user));
        return user;
    }

    @Override
    public Optional<ServerUser> findByUsername(String username) {
        ServerUser user = usersMap.get(username);
        if (user == null)
            return Optional.empty();
        return Optional.of(new ServerUser(user));
    }

    @Override
    public boolean existsById(String username) {
        Objects.requireNonNull(username);
        return usersMap.containsKey(username);
    }

    @Override
    public void deleteById(String username) {
        Objects.requireNonNull(username);
        usersMap.remove(username);
    }

}
