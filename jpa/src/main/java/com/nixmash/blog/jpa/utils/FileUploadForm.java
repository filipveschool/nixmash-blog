package com.nixmash.blog.jpa.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
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