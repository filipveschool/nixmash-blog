package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

@Getter
@Setter
public class ForgotEmailDTO implements Serializable {

    private static final long serialVersionUID = -934031990312257019L;

    @NotEmpty
    private String email;

    public ForgotEmailDTO() {

    }
}
