package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.CurrentUser;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.PostRepository;
import com.nixmash.blog.jpa.service.interfaces.PermaPostService;
import com.nixmash.blog.jpa.service.interfaces.PostImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PermaPostImpl implements PermaPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageService postImageService;

    // region PermaPost

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "#postId")
    public Post getPostById(Long postId) {
        Post found = postRepository.findByPostId(postId);
        if (found == null) {
            log.debug("No post found with id: {}", postId);
            throw new PostNotFoundException("No post found with id: " + postId);
        }
        populatePostImages(found);
        return found;
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "#postName")
    public Post getPost(String postName) {
        Post found = postRepository.findByPostNameIgnoreCase(postName);
        if (found == null) {
            log.debug("No post found with name: {}", postName);
            throw new PostNotFoundException("No post found with name: " + postName);
        } else {
            populatePostImages(found);
        }

        return found;
    }

    private void populatePostImages(Post post) {
        try {
            if (post.getDisplayType().equals(PostDisplayType.MULTIPHOTO_POST))
                post.setPostImages(postImageService.getPostImages(post.getPostId()));
            if (post.getDisplayType().equals(PostDisplayType.SINGLEPHOTO_POST))
                post.setSingleImage(postImageService.getPostImages(post.getPostId()).get(0));
        } catch (Exception e) {
            log.info(String.format("Image Retrieval Error for Post ID:%s Title: %s", String.valueOf(post.getPostId()), post.getPostTitle()));
        }
    }

    // region Security Support


    @Override
    public boolean canUpdatePost(Authentication authentication, Long postId) {

        if (authentication instanceof AnonymousAuthenticationToken)
            return false;

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Post post = null;
        try {
            post = getPostById(postId);
        } catch (PostNotFoundException e) {
            log.error("Post not found for PostId {} ", postId);
            return false;
        }

        Long postUserId = post.getUserId();
        return currentUser.getId().equals(postUserId);
    }

    // endregion

}
