package com.nixmash.blog.jsoup.dto;

import com.nixmash.blog.jsoup.annotations.ImageSelector;
import com.nixmash.blog.jsoup.annotations.MetaName;
import com.nixmash.blog.jsoup.annotations.Selector;
import com.nixmash.blog.jsoup.annotations.TwitterSelector;
import com.nixmash.blog.jsoup.base.JsoupImage;
import com.nixmash.blog.jsoup.base.JsoupTwitter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//@SuppressWarnings("WeakerAccess")
public class PagePreviewDTO implements Serializable {

    private static final long serialVersionUID = -3568730305139406803L;

    @Selector("title")
    public String title;

    @MetaName("keywords")
    public String keywords;

    @MetaName("description")
    public String description;

    @TwitterSelector
    public JsoupTwitter twitterDTO;

    @ImageSelector
    public List<JsoupImage> images;

}
