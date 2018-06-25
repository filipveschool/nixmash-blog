package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FileUploadDTO {

    private String filename;

    private Long parentId;

    private List<MultipartFile> files;


    public FileUploadDTO() {

    }

}
