package de.uol.swp.server.usermanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    InMemoryUserRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void testSave() {
        ServerUser serverUser = new ServerUser("username", "password", authorities);

        ServerUser returnedUser = repository.save(serverUser);
        ServerUser savedUser = repository.findByUsername("username").orElseThrow();

        assertEquals(serverUser, savedUser);
        assertEquals(serverUser, returnedUser);
        assertNotSame(serverUser, savedUser);
        assertSame(serverUser, returnedUser);
    }

    @Test
    void testSaveNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    @Test
    void testSaveWithOtherUsers() {
        ServerUser serverUser1 = new ServerUser("username1", "password", authorities);
        ServerUser serverUser2 = new ServerUser("username2", "password", authorities);

        repository.save(serverUser1);
        repository.save(serverUser2);

        ServerUser savedUser1 = repository.findByUsername("username1").orElseThrow();
        ServerUser savedUser2 = repository.findByUsername("username2").orElseThrow();

        assertEquals(serverUser1, savedUser1);
        assertNotSame(serverUser1, savedUser1);
        assertEquals(serverUser2, savedUser2);
        assertNotSame(serverUser2, savedUser2);
    }

    @Test
    void testFindByUsernameNonExistent() {
        assertTrue(repository.findByUsername("nonexistent").isEmpty());
    }

    @Test
    void testExistsById() {
        assertFalse(repository.existsById("username"));

        ServerUser serverUser = new ServerUser("username", "password", authorities);

        repository.save(serverUser);

        assertTrue(repository.existsById("username"));
        assertFalse(repository.existsById("nonexistent"));
    }

    @Test
    void testExistsByIdWithNull() {
        assertThrows(NullPointerException.class, () -> repository.existsById(null));
    }

    @Test
    void testDeleteById() {
        ServerUser serverUser = new ServerUser("username", "password", authorities);

        repository.save(serverUser);
        repository.deleteById("username");

        assertFalse(repository.existsById("username"));
    }

    @Test
    void testDeleteByIdWithNull() {
        assertThrows(NullPointerException.class, () -> repository.deleteById(null));
    }

}
