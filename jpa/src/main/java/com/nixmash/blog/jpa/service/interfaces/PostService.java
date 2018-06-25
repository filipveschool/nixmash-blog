package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.dto.AlphabetDTO;
import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.exceptions.CategoryNotFoundException;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.exceptions.TagNotFoundException;
import com.nixmash.blog.jpa.model.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by daveburke on 6/1/16.
 */
public interface PostService {

    Post add(PostDTO postDTO) throws DuplicatePostNameException;

    Page<Post> getPosts(Integer pageNumber, Integer pageSize);

    @Transactional
    Post update(PostDTO postDTO) throws PostNotFoundException;

    @Transactional(readOnly = true)
    Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPosts();

    @Transactional(readOnly = true)
    List<Post> getAdminRecentPosts();

    @Transactional(readOnly = true)
    List<Post> getAllPublishedPostsByPostType(PostType postType);

    @Transactional(readOnly = true)
    Page<Post> getPagedPostsByPostType(PostType postType, int pageNumber, int pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPublishedPosts();

    Optional<Post> getOneMostRecent();

    @Transactional(readOnly = true)
    List<Post> getPostsWithDetail();


    @Transactional(readOnly = true)
    List<AlphabetDTO> getAlphaLInks();

    @Transactional(readOnly = true)
    List<PostDTO> getAlphaPosts();


    List<Post> getAllPostsByTagId(long tagId);

    Page<Post> getPublishedPostsByTagId(long tagId, int pageNumber, int pageSize);

    @Transactional(readOnly = true)
    List<Post> getAllPostsByCategoryId(long categoryId);

    List<Post> getPublishedPostsByTagId(long tagId);


}
