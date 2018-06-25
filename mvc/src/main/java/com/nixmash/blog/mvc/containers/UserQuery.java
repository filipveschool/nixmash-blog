package com.nixmash.blog.mvc.containers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuery implements java.io.Serializable {

    private static final long serialVersionUID = -3747394871627673122L;

    public UserQuery() {
    }

    private String query;


}

