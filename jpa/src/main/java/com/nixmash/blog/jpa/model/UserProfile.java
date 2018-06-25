package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
/**
 * A profile for a system user.
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = -2264925400662063658L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;

    @Column
    private String address;

    @Column
    private String address2;

    @Column
    private String city;

    @Column
    @Size(min = 2)
    private String state;

    @Column
    @Size(min = 5, max = 10)
    private String zip;

    @Column
    private String phone;

    public UserProfile() {
    }

    public UserProfile(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserProfile(" + user.getUsername() + ")";
    }
}
