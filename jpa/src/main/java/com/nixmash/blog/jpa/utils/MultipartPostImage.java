package com.nixmash.blog.jpa.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MultipartPostImage {

    private MultipartFile file;

    public MultipartPostImage() {

    }
}

