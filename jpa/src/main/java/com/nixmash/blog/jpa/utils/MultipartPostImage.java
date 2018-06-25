package com.nixmash.blog.jpa.utils;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class MultipartPostImage {

    private MultipartFile file;
}

