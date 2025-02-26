package de.uol.swp.server.usermanagement;

import de.uol.swp.server.model.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMappingTest {

    final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    UserMapping mapper;

    @BeforeEach
    void setup() {
        mapper = new UserMappingImpl();
    }

    @Test
    void testUserToDTO() {
        ServerUser serverUser = new ServerUser("username", "password", authorities);

        UserDTO userDTO = mapper.toDTO(serverUser);

        assertEquals(serverUser.getUsername(), userDTO.getUsername());
    }

    @Test
    void testNullUserToDTO() {
        UserDTO userDTO = mapper.toDTO(null);
        assertNull(userDTO);
    }

}
