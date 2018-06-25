package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.nixmash.blog.jpa.model.User.MIN_LENGTH_PASSWORD;

@Getter
@Setter
public class UserPasswordDTO implements Serializable {

    private static final long serialVersionUID = -2221852531645649922L;

    @Length(min = MIN_LENGTH_PASSWORD)
    private String password;

    @Length(min = MIN_LENGTH_PASSWORD)
    private String repeatedPassword;

    private String verificationToken;

    private long userId;

    // region Constructors

    public UserPasswordDTO() {
    }

    public UserPasswordDTO(long userId, String verificationToken) {
        this.verificationToken = verificationToken;
        this.userId = userId;
    }

    public UserPasswordDTO(long userId, String verificationToken, String password, String repeatedPassword) {
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.verificationToken = verificationToken;
        this.userId = userId;
    }


    // endregion

}
