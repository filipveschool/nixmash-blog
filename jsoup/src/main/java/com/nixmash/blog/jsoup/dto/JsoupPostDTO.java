package com.nixmash.blog.jsoup.dto;

import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jsoup.annotations.DocText;
import com.nixmash.blog.jsoup.annotations.ImageSelector;
import com.nixmash.blog.jsoup.base.JsoupImage;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("WeakerAccess")
public class JsoupPostDTO implements Serializable {

    private static final long serialVersionUID = -2690151588053584076L;

    @DocText
    public String bodyText;

    @ImageSelector
    public List<JsoupImage> imagesInContent;

    public boolean hasImages() {
        {
            return (!this.imagesInContent.isEmpty());
        }
    }

    public String twitterImagePath;
    public String twitterDescription;
    public PostDisplayType postDisplayType;


}
