package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.PostMeta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostMetaRepository extends CrudRepository<PostMeta, Long> {

    PostMeta findByPostId(Long postId);

    List<PostMeta> findAll();
}

