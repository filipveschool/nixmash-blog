package com.nixmash.blog.jpa.service.implementations;

import com.google.common.collect.Lists;
import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.model.PostImage;
import com.nixmash.blog.jpa.repository.PostImageRepository;
import com.nixmash.blog.jpa.service.interfaces.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostImageImpl implements PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private ApplicationSettings applicationSettings;


    @Transactional(readOnly = true)
    @Override
    public List<PostImage> getAllPostImages() {
        return Lists.newArrayList(postImageRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostImage> getPostImages(long postId) {
        List<PostImage> images = Lists.newArrayList(postImageRepository.findByPostId(postId));
        for (PostImage image: images) {
            image.setUrl(applicationSettings.getPostImageUrlRoot());
        }
        return images;
    }


    @Transactional
    @Override
    public PostImage addImage(PostImage image) {
        return postImageRepository.save(image);
    }

    @Transactional(readOnly = true)
    @Override
    public PostImage getPostImage(long imageId) {
        return postImageRepository.findOne(imageId);
    }

    @Transactional
    @Override
    public void deleteImage(PostImage image) {
        postImageRepository.delete(image);
    }

}
