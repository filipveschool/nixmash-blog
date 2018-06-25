package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.enums.SignInProvider;
import com.nixmash.blog.jpa.model.Authority;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.model.validators.ExtendedEmailValidator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Basic;
import java.util.Collection;

@Getter
@Setter
public class UserDTO {

    private Long userId;

    private boolean updateChildren = true;

    private boolean isEnabled = true;

    @Length(min = User.MIN_LENGTH_USERNAME, max = User.MAX_LENGTH_USERNAME)
    private String username = "";

    @Basic
    @ExtendedEmailValidator
    @Length(max = User.MAX_LENGTH_EMAIL_ADDRESS)
    private String email = "";

    @Length(min = User.MIN_LENGTH_PASSWORD, max = User.MAX_LENGTH_PASSWORD)
    private String password = "";

    @NotEmpty
    @Length(min = User.MIN_LENGTH_FIRST_NAME, max = User.MAX_LENGTH_FIRST_NAME)
    private String firstName = "";

    @NotEmpty
    @Length(min = User.MIN_LENGTH_LAST_NAME, max = User.MAX_LENGTH_LAST_NAME)
    private String lastName = "";

    private boolean hasAvatar;
    private String userKey;

    private SignInProvider signInProvider;

    private String repeatedPassword = "";

    private Collection<Authority> authorities;

    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public UserDTO() {
    }


    public boolean isNew() {
        return (this.userId == null);
    }

    @Override
    public String toString() {
        return "UserCreateForm{" +
                ", username=" + username +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                '}';
    }
}
