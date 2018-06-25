package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.exceptions.CategoryNotFoundException;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    @Transactional(readOnly = true)
    Category getCategory(String categoryValue) throws CategoryNotFoundException;

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    List<CategoryDTO> getCategoryCounts();

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    List<CategoryDTO> getCategoryCounts(int categoryCount);

    @Transactional(readOnly = true)
    List<CategoryDTO> getAdminSelectionCategories();

    @Transactional(readOnly = true)
    List<CategoryDTO> getAdminCategories();

    @Transactional(readOnly = true)
    List<Category> getAllCategories();

    @Transactional
    Category createCategory(CategoryDTO categoryDTO);

    @Transactional
    Category updateCategory(CategoryDTO categoryDTO);

    @Transactional
    void deleteCategory(CategoryDTO categoryDTO, List<Post> posts);

    @Transactional(readOnly = true)
    Category getCategoryById(long categoryId);
}
