package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AccessDTO implements Serializable {

    private static final long serialVersionUID = -5289711184924125135L;

    private boolean isValid;

    private boolean isApproved;

    private String email;

    private String domain;

    public AccessDTO() {
    }

    public AccessDTO(String email) {
        this.email = email;
        this.isValid = false;
        this.isApproved = false;
    }

    @Override
    public String toString() {
        return "AccessDTO{" +
                "isValid=" + isValid +
                ", isApproved='" + isApproved + '\'' +
                ", email='" + email + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }


}
