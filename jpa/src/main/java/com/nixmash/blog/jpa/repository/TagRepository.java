package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByTagValueIgnoreCase(String tagValue);

    Set<Tag> findAll();
}
