package de.uol.swp.server.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServerUserTest {

    @Test
    void testConstructor() {
        Collection<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        ServerUser serverUser = new ServerUser("username", "password", authorities);

        assertEquals("username", serverUser.getUsername());
        assertEquals("password", serverUser.getPassword());
        assertEquals(authorities, serverUser.getAuthorities());
    }

    @Test
    void testConstructorWithNullValues() {
        Collection<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        assertThrows(IllegalArgumentException.class, () -> new ServerUser(null, "password", authorities));
        assertThrows(IllegalArgumentException.class, () -> new ServerUser("username", null, authorities));
        assertThrows(IllegalArgumentException.class, () -> new ServerUser("username", "password", null));
    }

    @Test
    void testCopyConstructor() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add((GrantedAuthority) () -> "ROLE_USER");
        ServerUser serverUser = new ServerUser("username", "password", authorities);
        ServerUser copy = new ServerUser(serverUser);

        assertEquals(serverUser.getUsername(), copy.getUsername());
        assertEquals(serverUser.getPassword(), copy.getPassword());
        assertEquals(serverUser.getAuthorities(), copy.getAuthorities());
        assertNotSame(serverUser.getAuthorities(), copy.getAuthorities());
    }

    @Test
    void testCopyConstructorWithNullValue() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> new ServerUser(null));
    }

}
