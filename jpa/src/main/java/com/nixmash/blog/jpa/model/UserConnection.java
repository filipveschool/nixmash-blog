package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.social.connect.ConnectionData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "userconnection")
public class UserConnection implements Serializable {

    private static final long serialVersionUID = -6513324326814304874L;

    public UserConnection() {
    }

    @Id
    @Column(name = "userid", unique = true, nullable = false)
    private String userId;

    @Column(name = "providerid")
    @NotEmpty
    private String providerId;

    @Column(name = "provideruserid")
    @NotEmpty
    private String providerUserId;

    @Column(name = "rank")
    private int rank;

    @Column(name = "displayname")
    private String displayName;

    @Column(name = "profileurl")
    private String profileUrl;

    @Column(name = "imageurl")
    private String imageUrl;

    @Column(name = "accesstoken")
    private String accessToken;

    @Column(name = "secret")
    private String secret;

    @Column(name = "refreshtoken")
    private String refreshToken;

    @Column(name = "expiretime")
    private Long expireTime;

    public UserConnection(String userId, String providerId, String providerUserId, int rank, String displayName,
                          String profileUrl, String imageUrl, String accessToken, String secret, String refreshToken,
                          Long expireTime) {
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.rank = rank;
        this.displayName = displayName;
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.accessToken = accessToken;
        this.secret = secret;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
    }

    public UserConnection(ConnectionData connectionData, String userId) {
        this.userId = userId;
        this.providerId = connectionData.getProviderId();
        this.providerUserId = connectionData.getProviderUserId();
        this.rank = 1;
        this.displayName = connectionData.getDisplayName();
        this.profileUrl = connectionData.getProfileUrl();
        this.imageUrl = connectionData.getImageUrl();
        this.accessToken = connectionData.getAccessToken();
        this.secret = connectionData.getSecret();
        this.refreshToken = connectionData.getRefreshToken();
        this.expireTime = connectionData.getExpireTime();
    }

    public String toString() {
        return "userId = " + userId + ", providerId = " + providerId + ", providerUserId = " + providerUserId
                + ", rank = " + rank + ", displayName = " + displayName + ", profileUrl = " + profileUrl
                + ", imageUrl = " + imageUrl + ", accessToken = " + accessToken + ", secret = " + secret
                + ", refreshToken = " + refreshToken + ", expireTime = " + expireTime;
    }


}