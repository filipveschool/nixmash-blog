package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    Category findByCategoryValueIgnoreCase(String categoryValue);

    List<Category> findAll(Sort sort);

    List<Category> findByIsActiveTrue(Sort sort);

    Category findByCategoryId(Long categoryId);

}
