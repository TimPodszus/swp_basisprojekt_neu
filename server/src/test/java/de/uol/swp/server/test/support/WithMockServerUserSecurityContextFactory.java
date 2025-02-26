package de.uol.swp.server.test.support;

import de.uol.swp.server.usermanagement.ServerUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

/**
 * This class sets the user in the SecurityContext for the test.
 * Based on the Spring Security <a href="https://docs.spring.io/spring-security/reference/servlet/test/method.html#test-method-withsecuritycontext">Documentation</a>
 *
 * @author Tilman Holube
 * @see WithMockServerUser
 * @since 2025-03-17
 */
public class WithMockServerUserSecurityContextFactory implements WithSecurityContextFactory<WithMockServerUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockServerUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        ServerUser principal = new ServerUser(customUser.value(), "password", Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

}
