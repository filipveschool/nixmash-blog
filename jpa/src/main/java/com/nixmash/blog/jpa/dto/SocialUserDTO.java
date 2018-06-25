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
public class SocialUserDTO {

    @Length(min = User.MIN_LENGTH_USERNAME, max = User.MAX_LENGTH_USERNAME)
    private String username = "";

    @Basic
    @ExtendedEmailValidator
    @Length(max = User.MAX_LENGTH_EMAIL_ADDRESS)
    private String email = "";

    private String password = "";

    private SignInProvider signInProvider;

    @NotEmpty
    @Length(min = User.MIN_LENGTH_FIRST_NAME, max = User.MAX_LENGTH_FIRST_NAME)
    private String firstName = "";

    @NotEmpty
    @Length(min = User.MIN_LENGTH_LAST_NAME, max = User.MAX_LENGTH_LAST_NAME)
    private String lastName = "";

    private Collection<Authority> authorities;

    public SocialUserDTO() {

    }

    @Override
    public String toString() {
        return "SocialUserDTO{" +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                "lastName='" + lastName + '\'' +
                "signInProvider='" + signInProvider + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
