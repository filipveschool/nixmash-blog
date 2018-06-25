package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import org.springframework.security.core.Authentication;

public interface PermaPostService {


    Post getPostById(Long postId);

    Post getPost(String postName);

    boolean canUpdatePost(Authentication authentication, Long postId);


}
