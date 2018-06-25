
package com.nixmash.blog.jpa.exceptions;

public class DuplicatePostNameException extends RuntimeException {

    private static final long serialVersionUID = -4658463190108406055L;
    private String msg;

    public DuplicatePostNameException() {
        super();
    }

    public DuplicatePostNameException(String msg) {
        this.msg = System.currentTimeMillis()
                + ": " + msg;
    }

    public String getMsg() {
        return msg;
    }


}


