package de.uol.swp.server.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LobbiesIT {

    @Autowired
    MockMvc mvc;

    @Test
    void testLobbyCreate() throws Exception {
        mvc.perform(post("/api/lobbies")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("lobbyname"))
                .andExpect(jsonPath("$.owner.username").value("test0"))
                .andExpect(jsonPath("$.users.[0].username").value("test0"))
                .andExpect(jsonPath("$.users.length()").value(1));
    }

    @Test
    void testLobbyCreateUnauthorized() throws Exception {
        mvc.perform(post("/api/lobbies")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLobbyCreateAlreadyExists() throws Exception {
        mvc.perform(post("/api/lobbies")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyCreateMissingParam() throws Exception {
        mvc.perform(post("/api/lobbies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyCreateEmpty() throws Exception {
        mvc.perform(post("/api/lobbies")
                        .param("lobbyname", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyJoin() throws Exception {
        mvc.perform(post("/api/lobbies/join")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test1", "test1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("lobbyname"))
                .andExpect(jsonPath("$.owner.username").value("test0"))
                .andExpect(jsonPath("$.users.[0].username").value("test0"))
                .andExpect(jsonPath("$.users.[1].username").value("test1"))
                .andExpect(jsonPath("$.users.length()").value(2));
    }

    @Test
    void testLobbyJoinUnauthorized() throws Exception {
        mvc.perform(post("/api/lobbies/join")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLobbyJoinNotFound() throws Exception {
        mvc.perform(post("/api/lobbies/join")
                        .param("lobbyname", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test1", "test1")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLobbyJoinMissingParam() throws Exception {
        mvc.perform(post("/api/lobbies/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test1", "test1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyJoinEmpty() throws Exception {
        mvc.perform(post("/api/lobbies/join")
                        .param("lobbyname", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test1", "test1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyLeave() throws Exception {
        mvc.perform(post("/api/lobbies/leave")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isOk());
    }

    @Test
    void testLobbyLeaveUnauthorized() throws Exception {
        mvc.perform(post("/api/lobbies/leave")
                        .param("lobbyname", "lobbyname")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLobbyLeaveNotFound() throws Exception {
        mvc.perform(post("/api/lobbies/leave")
                        .param("lobbyname", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLobbyLeaveMissingParam() throws Exception {
        mvc.perform(post("/api/lobbies/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLobbyLeaveEmpty() throws Exception {
        mvc.perform(post("/api/lobbies/leave")
                        .param("lobbyname", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("test0", "test0")))
                .andExpect(status().isBadRequest());
    }

}
