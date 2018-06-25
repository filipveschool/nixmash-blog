package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileImageDTO {

    private MultipartFile file;

    public ProfileImageDTO() {

    }
}

