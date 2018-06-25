package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.exceptions.CategoryNotFoundException;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.CategoryRepository;
import com.nixmash.blog.jpa.repository.PostRepository;
import com.nixmash.blog.jpa.service.interfaces.CategoryService;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional(readOnly = true)
    @Override
    public Category getCategory(String categoryValue) throws CategoryNotFoundException {
        Category found = categoryRepository.findByCategoryValueIgnoreCase(categoryValue);
        if (found == null) {
            log.info("No category found with id: {}", categoryValue);
            throw new CategoryNotFoundException("No category found with id: " + categoryValue);
        }
        return found;
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getCategoryCounts() {
        return getCategoryCounts(-1);
    }


    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getCategoryCounts(int categoryCount) {
        List<Category> categories = em.createNamedQuery("getCategoryCounts", Category.class)
                .getResultList();
        List<CategoryDTO> categoryDTOS = categories
                .stream()
                .filter(c -> c.getPosts().size() > 0)
                .limit(categoryCount > 0 ? categoryCount : Long.MAX_VALUE)
                .map(CategoryDTO::new)
                .sorted(comparing(CategoryDTO::getCategoryValue))
                .collect(Collectors.toList());
        return categoryDTOS;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getAdminSelectionCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue(sortByCategoryAsc());
        return PostUtils.categoriesToCategoryDTOs(categories);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getAdminCategories() {
        List<Category> categories = categoryRepository.findAll(sortByCategoryAsc());
        for (Category category: categories) {
            category.setCategoryCount(postRepository.findAllByCategoryId(category.getCategoryId()).size());
        }
        return PostUtils.categoriesToCategoryDTOs(categories);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(sortByCategoryAsc());
    }

    @Transactional
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryValueIgnoreCase(categoryDTO.getCategoryValue());
        if (category == null) {
            category = new Category(null, categoryDTO.getCategoryValue(), true, false);
            categoryRepository.save(category);
        }
        return category;
    }

    @Transactional
    @Override
    public Category updateCategory(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByCategoryId(categoryDTO.getCategoryId());
        if (categoryDTO.getCategoryId() > 1) {
            if (categoryDTO.getIsDefault().equals(true))
                clearCategoryDefaults();
            category.update(categoryDTO.getCategoryValue(), categoryDTO.getIsActive(), categoryDTO.getIsDefault());
        }
        return category;
    }

    @Transactional
    private void clearCategoryDefaults() {
        Iterable<Category> cats = categoryRepository.findAll();
        for (Category cat: cats) {
            cat.clearDefault();
        }
    }

    @Transactional
    @Override
    public void deleteCategory(CategoryDTO categoryDTO, List<Post> posts) {
        if (categoryDTO.getCategoryId() > 1) {
            if (posts != null) {
                Category unCategorizedCategory = categoryRepository.findOne(1L);
                for (Post post: posts) {
                    post.setCategory(unCategorizedCategory);
                }
            }
            categoryRepository.delete(categoryDTO.getCategoryId());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(long categoryId) {
        return categoryRepository.findByCategoryId(categoryId);
    }

    public Sort sortByCategoryAsc() {
        return new Sort(Sort.Direction.ASC, "categoryValue");
    }
}
