package de.uol.swp.server.test.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mock a user in the security context
 * Based on the Spring Security <a href="https://docs.spring.io/spring-security/reference/servlet/test/method.html#test-method-withsecuritycontext">Documentation</a>
 *
 * @author Tilman Holube
 * @see WithMockServerUserSecurityContextFactory
 * @since 2025-03-17
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockServerUserSecurityContextFactory.class)
public @interface WithMockServerUser {

    /**
     * The username of the user to mock
     *
     * @return The username of the user to mock
     */
    String value() default "username";

}