package com.nixmash.blog.jpa.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FileUploadForm {

    private List<MultipartFile> files;
    private Long parentId;

    public FileUploadForm() {
    }

    public FileUploadForm(Long parentId) {
        this.parentId = parentId;
    }
}