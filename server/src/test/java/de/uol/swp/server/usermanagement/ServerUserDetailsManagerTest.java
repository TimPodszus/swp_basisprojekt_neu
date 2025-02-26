package de.uol.swp.server.usermanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerUserDetailsManagerTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Mock
    UserRepository repository;
    ServerUserDetailsManager manager;

    @BeforeEach
    void setup() {
        manager = new ServerUserDetailsManager(repository);
    }

    @Test
    void testLoadUserByUsername() {
        ServerUser serverUser = new ServerUser("username", "password", authorities);
        when(repository.findByUsername("username")).thenReturn(Optional.of(serverUser));

        ServerUser loadedUser = manager.loadUserByUsername("username");

        assertEquals(serverUser, loadedUser);
        assertNotSame(serverUser, loadedUser);
        verify(repository).findByUsername("username");
    }

    @Test
    void testLoadUserByUsernameWithNull() {
        when(repository.findByUsername(null)).thenThrow(NullPointerException.class);
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> manager.loadUserByUsername(null));
    }

    @Test
    void testLoadUserByUsernameWithNonexistentUser() {
        when(repository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> manager.loadUserByUsername("nonexistent"));
        verify(repository).findByUsername("nonexistent");
    }

    @Test
    void testCreateUser() {
        ServerUser serverUser = new ServerUser("username", "password", authorities);

        manager.createUser(serverUser);

        verify(repository).save(serverUser);
    }

    @Test
    void testCreateUserWithNull() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> manager.createUser(null));
    }

    @Test
    void testDeleteUser() {
        manager.deleteUser("username");

        verify(repository).deleteById("username");
    }

    @Test
    void testDeleteUserWithNull() {
        doThrow(NullPointerException.class).when(repository).deleteById(null);
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> manager.deleteUser(null));
    }

    @Test
    void testUserExists() {
        when(repository.existsById("username")).thenReturn(true);
        when(repository.existsById("nonexistent")).thenReturn(false);

        assertTrue(manager.userExists("username"));
        assertFalse(manager.userExists("nonexistent"));
        verify(repository).existsById("username");
        verify(repository).existsById("nonexistent");
    }

    @Test
    void testUserExistsWithNull() {
        when(repository.existsById(null)).thenThrow(NullPointerException.class);
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> manager.userExists(null));
    }

    @Test
    void testUpdateUser() {
        // TODO implement me
        manager.updateUser(null);
    }

    @Test
    void testChangePassword() {
        // TODO implement me
        manager.changePassword(null, null);
    }

}
