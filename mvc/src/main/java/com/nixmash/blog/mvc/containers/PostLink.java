package com.nixmash.blog.mvc.containers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLink implements java.io.Serializable {

    private static final long serialVersionUID = 9063998456473593040L;

    public PostLink() {
    }

    private String link;


}

