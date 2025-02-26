package de.uol.swp.server.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketInterceptorTest {

    WebSocketInterceptor interceptor;

    @Mock
    MessageChannel channel;
    @Mock
    DaoAuthenticationProvider daoAuthenticationProvider;
    @Mock
    Authentication authentication;

    @BeforeEach
    void setup() {
        interceptor = new WebSocketInterceptor(daoAuthenticationProvider);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void testPreSend() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        when(daoAuthenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
        headers.setLogin("username");
        headers.setPasscode("password");
        headers.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        interceptor.preSend(message, channel);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(authentication, auth);

    }

    @Test
    void testPreSendUnableToCreateToken() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        when(daoAuthenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
        headers.setLogin("username");
        headers.setPasscode("password");
        headers.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, channel));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testPreSendAlreadyAuthenticated() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
        headers.setUser(authentication);
        headers.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        interceptor.preSend(message, channel);

        verify(daoAuthenticationProvider, never()).authenticate(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testPreSendNoAccessor(@Mock MessageHeaders headers) {
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers);

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, channel));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testPreSendDifferentCommand() {
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        assertDoesNotThrow(() -> interceptor.preSend(message, channel));

        verify(daoAuthenticationProvider, never()).authenticate(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
