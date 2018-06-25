package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.PostImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends CrudRepository<PostImage, Long> {

    List<PostImage> findAll();

    List<PostImage> findByPostId(long postId);
}
