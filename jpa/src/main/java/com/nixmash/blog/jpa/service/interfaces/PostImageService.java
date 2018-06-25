package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.model.PostImage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostImageService {


    @Transactional(readOnly = true)
    List<PostImage> getAllPostImages();

    @Transactional(readOnly = true)
    List<PostImage> getPostImages(long postId);

    @Transactional
    PostImage addImage(PostImage image);

    @Transactional(readOnly = true)
    PostImage getPostImage(long imageId);

    void deleteImage(PostImage image);

}
