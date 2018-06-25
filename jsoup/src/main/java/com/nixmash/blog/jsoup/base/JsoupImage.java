package com.nixmash.blog.jsoup.base;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@SuppressWarnings("WeakerAccess")
public class JsoupImage implements Serializable{

    private static final long serialVersionUID = 1772136151299547608L;

    public String src;
    public String alt;
    public Integer height;
    public Integer width;

    @Override
    public String toString() {
        return "JsoupImage{" +
                "src='" + src + '\'' +
                ", alt='" + alt + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
