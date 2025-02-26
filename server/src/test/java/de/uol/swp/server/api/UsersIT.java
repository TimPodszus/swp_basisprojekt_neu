package de.uol.swp.server.api;

import de.uol.swp.server.usermanagement.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    SimpUserRegistry userRegistry;
    @Mock
    SimpUser simpUser;

    @Test
    void testCurrentUser() throws Exception {
        mvc.perform(get("/api/users")
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test0"));
    }

    @Test
    void testCurrentUserUnauthorized() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser() throws Exception {
        mvc.perform(post("/api/users")
                        .param("name", "test10")
                        .param("password", "test10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(userRepository.existsById("test10"));
    }

    @Test
    void testCreateUserAlreadyExisting() throws Exception {
        mvc.perform(post("/api/users")
                        .param("name", "test0")
                        .param("password", "test0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserEmpty() throws Exception {
        mvc.perform(post("/api/users")
                        .param("name", "")
                        .param("password", "test10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/users")
                        .param("name", "test10")
                        .param("password", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserMissing() throws Exception {
        mvc.perform(post("/api/users")
                        .param("password", "test10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/users")
                        .param("name", "test10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListUsers() throws Exception {
        when(userRegistry.getUsers()).thenReturn(Set.of(simpUser));
        when(simpUser.getName()).thenReturn("test0");

        mvc.perform(get("/api/users/list")
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("test0"));
    }

    @Test
    void testListUsersEmpty() throws Exception {
        when(userRegistry.getUsers()).thenReturn(Set.of());

        mvc.perform(get("/api/users/list")
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testListUsersUnauthorized() throws Exception {
        mvc.perform(get("/api/users/list"))
                .andExpect(status().isUnauthorized());
    }

}
