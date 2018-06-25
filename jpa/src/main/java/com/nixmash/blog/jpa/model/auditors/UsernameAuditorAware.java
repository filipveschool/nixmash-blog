package com.nixmash.blog.jpa.model.auditors;

import com.nixmash.blog.jpa.model.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This component returns the username of the authenticated user.
 * <p>
 * From Petri Kainulainen's JPA Examples Project on GitHub
 * <p>
 * spring-data-jpa-examples/query-methods/
 * https://goo.gl/lY7sT5
 */
@Slf4j
public class UsernameAuditorAware implements AuditorAware<String> {

    protected static final String ANONYMOUS_USERNAME = "anonymous";
    protected static final String TESTGUY_USERNAME = "testguy";

    @Override
    public String getCurrentAuditor() {
        log.debug("Getting the username of authenticated user.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            // in testing mode, return admin
            return TESTGUY_USERNAME;
        }

        if (authentication.getPrincipal().equals("anonymousUser")) {
            log.debug("Current user is anonymous.");
            return ANONYMOUS_USERNAME;
        }

        String username = ((CurrentUser) authentication.getPrincipal()).getUsername();
        log.debug("Returning username: {}", username);

        return username;
    }
}
