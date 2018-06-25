package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_tokens")
public class UserToken {

    @Id
    @Column(name = "token_id", nullable = false)
    private long tokenId;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Basic
    @Column(name = "token", nullable = true, length = 255)
    private String token;

    @Basic
    @Column(name = "token_expiration", nullable = false)
    private Timestamp tokenExpiration;

    private static final int EXPIRATION = 60 * 24;

    public UserToken() {
        super();
    }

    public UserToken(final String token) {
        super();
        this.token = token;
        this.tokenExpiration = calculateExpiryDate(EXPIRATION);
    }

    public UserToken(final String token, final User user) {
        super();

        this.token = token;
        this.user = user;
        this.tokenExpiration = calculateExpiryDate(EXPIRATION);
    }

    private Timestamp calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Timestamp(cal.getTime().getTime());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.tokenExpiration = calculateExpiryDate(EXPIRATION);
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "tokenId=" + tokenId +
                ", user=" + user.getUsername() +
                ", userToken='" + token + '\'' +
                ", tokenExpiration=" + tokenExpiration +
                '}';
    }
}
