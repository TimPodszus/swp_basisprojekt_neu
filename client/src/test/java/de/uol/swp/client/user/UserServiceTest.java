package de.uol.swp.client.user;

import de.uol.swp.client.ApiClient;
import de.uol.swp.client.api.DefaultApi;
import de.uol.swp.client.model.UserDTO;
import de.uol.swp.client.websocket.WebSocketConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService;

    @Mock
    DefaultApi api;
    @Mock
    ApiClient apiClient;
    @Mock
    WebSocketConnectionManager webSocketConnectionManager;

    @BeforeEach
    void setup() {
        userService = new UserService(api, webSocketConnectionManager);
    }

    @Test
    void testLogin() {
        when(api.getApiClient()).thenReturn(apiClient);

        userService.login("username", "password");

        verify(apiClient).setUsername("username");
        verify(apiClient).setPassword("password");
        verify(webSocketConnectionManager).connect("username", "password");
    }

    @Test
    void testLogout() {
        when(api.getApiClient()).thenReturn(apiClient);

        userService.logout();

        verify(apiClient).setUsername(null);
        verify(apiClient).setPassword(null);
        verify(webSocketConnectionManager).disconnect();
    }

    @Test
    void testCreateUser() {
        userService.createUser("username", "password");

        verify(api).userCreateWithHttpInfo("username", "password");
    }

    @Test
    void testDropUser() {
        // TODO: Implement this test
        userService.dropUser();
    }

    @Test
    void testUpdateUser() {
        // TODO: Implement this test
        userService.updateUser(null);
    }

    @Test
    void testRetrieveAllUsers() {
        List<UserDTO> originalUsers = List.of(new UserDTO().username("username1"), new UserDTO().username("username2"));
        ResponseEntity<List<UserDTO>> response = ResponseEntity.ok(originalUsers);
        when(api.userListWithHttpInfo()).thenReturn(response);

        List<UserDTO> users = userService.retrieveAllUsers();

        verify(api).userListWithHttpInfo();
        assertEquals(originalUsers, users);
    }

    @Test
    void testRetrieveAllUsersFailed() {
        ResponseEntity<List<UserDTO>> response = ResponseEntity.badRequest().build();
        when(api.userListWithHttpInfo()).thenReturn(response);

        List<UserDTO> users = userService.retrieveAllUsers();

        verify(api).userListWithHttpInfo();
        assertEquals(List.of(), users);
    }

}
