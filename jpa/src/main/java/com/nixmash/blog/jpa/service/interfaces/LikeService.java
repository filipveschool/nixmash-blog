package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikeService {

    @Transactional(readOnly = true)
    List<Post> getPostsByUserLikes(Long userId);

    @Transactional(readOnly = true)
    List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize);

    @Transactional
    int addPostLike(long userId, long postId);
}
