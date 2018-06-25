package com.nixmash.blog.mvc.containers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pager implements java.io.Serializable {

    private static final long serialVersionUID = 7634436759919152932L;

    public Pager() {
    }

    private int beginIndex;
    private int endIndex;
    private int currentIndex;
    private int totalPageCount;
    private String baseUrl;
    private int totalItems;


}