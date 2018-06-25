package com.nixmash.blog.jpa.exceptions;


public class CategoryNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -9070542392426394574L;
    private String msg;

    public CategoryNotFoundException() {
        super();
    }

    public CategoryNotFoundException(String msg) {
        this.msg = System.currentTimeMillis()
                + ": " + msg;
    }

    public String getMsg() {
        return msg;
    }


}
