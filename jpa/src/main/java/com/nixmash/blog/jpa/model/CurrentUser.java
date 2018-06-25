
package com.nixmash.blog.jpa.model;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

import static com.nixmash.blog.jpa.components.ApplicationContextUI.getAppSettingsFromContext;

@Getter
@Setter
public class CurrentUser
        extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 7828419298616811182L;

    private ApplicationSettings applicationSettings = getAppSettingsFromContext();

    private User user;

    private Long id;

    public CurrentUser(User user) {
        super(user.getUsername(), user.getPassword(),
                user.getAuthorities());
        this.user = user;
    }

    public String getFullName() {
        return user.getFirstName() + ' ' + user.getLastName();
    }

    public String getProfileIconUrl() {

        String iconUrl = "/images/user32x32.png";
        if (this.user.isHasAvatar()) {
            iconUrl = applicationSettings.getProfileIconUrlRoot() + user.getUserKey();
        }
        return iconUrl;
    }

    // region Roles and Permissions

    public boolean canAccessAdmin() {
        return (isAdmin() || isPostUser());
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isPostUser() {
        return hasRole("POSTS");
    }

    private boolean hasRole(String role) {
        Collection<Authority> authorities = this.getUser().getAuthorities();
        boolean hasAuthority = false;
        for (Authority authority: authorities) {
            if (authority.getAuthority().toUpperCase().contains(role))
                hasAuthority = true;
        }
        return hasAuthority;
    }

    // endregion

    public String getProfileImageUrl() {

        String iconUrl = "/images/user.png";
        if (this.user.isHasAvatar()) {
            iconUrl = applicationSettings.getProfileImageUrlRoot() + user.getUserKey();
        }
        return iconUrl;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                '}' + super.toString();
    }
}