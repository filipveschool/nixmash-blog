package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.SpringDataTests;
import com.nixmash.blog.jpa.dto.AlphabetDTO;
import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.exceptions.CategoryNotFoundException;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostImage;
import com.nixmash.blog.jpa.repository.LikeRepository;
import com.nixmash.blog.jpa.utils.PostTestUtils;
import com.nixmash.blog.jpa.utils.PostUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.nixmash.blog.jpa.dto.PostDTO.ALPHACODE_09;
import static com.nixmash.blog.jpa.utils.PostTestUtils.CATEGORY_ID;
import static com.nixmash.blog.jpa.utils.PostTestUtils.DISPLAY_TYPE;
import static com.nixmash.blog.jpa.utils.PostTestUtils.POST_CONTENT;
import static com.nixmash.blog.jpa.utils.PostTestUtils.POST_NAME;
import static com.nixmash.blog.jpa.utils.PostTestUtils.POST_TITLE;
import static com.nixmash.blog.jpa.utils.PostTestUtils.POST_TYPE;
import static com.nixmash.blog.jpa.utils.PostTestUtils.TWITTER_CARD_SUMMARY;
import static com.nixmash.blog.jpa.utils.PostTestUtils.USER_ID;
import static com.nixmash.blog.jpa.utils.PostUtils.postDtoToPost;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Transactional
public class CategoryServiceTest extends SpringDataTests {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final String WOW_CATEGORY_NAME = "Wowwa Category";
    private static final String NONEXISTENT_CATEGORY_NAME = "MAMMA LAMMA";

    @Autowired
    private PostService postService;

    @Autowired
    private PermaPostService permaPostService;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    private CategoryService categoryService;

    // region Category Tests

    @Test(expected = CategoryNotFoundException.class)
    public void nonExistingCategory() throws Exception {
        Category category = categoryService.getCategory(NONEXISTENT_CATEGORY_NAME);
    }

    @Test
    public void uncategorizedCategory() throws CategoryNotFoundException {
        Category category = categoryService.getCategory(UNCATEGORIZED);
        assertEquals(category.getCategoryValue(), UNCATEGORIZED);
    }

    @Test
    public void categoryCountsTest() {
        List<CategoryDTO> categoryDTOS = categoryService.getCategoryCounts(5);
        for (CategoryDTO categoryDTO : categoryDTOS) {
            assertThat(categoryDTO.getCategoryCount(), greaterThan(0));
        }
    }

    @Test
    public void createCategory() {
        CategoryDTO categoryDTO = new CategoryDTO(WOW_CATEGORY_NAME);
        long categoryId = categoryService.createCategory(categoryDTO).getCategoryId();
        Category category = categoryService.getCategoryById(categoryId);
        assertTrue(category.getCategoryValue().equals(WOW_CATEGORY_NAME));
    }

    @Test
    public void newPostContainsAssignedCategory() throws DuplicatePostNameException {
        PostDTO postDTO = PostTestUtils.createPostDTO(100);
        postDTO.setCategoryId(2L);
        Post post = postService.add(postDTO);
        assertNotNull(post);
        assertNotNull(post.getCategory());

        Category category = post.getCategory();
        assertEquals(category.getCategoryValue(), "Java");
    }

    @Test
    public void setDefaultCategoryResultsInSingleDefault() throws Exception {
        // H2Data Status: Java Category is Sole Default
        Category category = categoryService.getCategory("wannabe");
        assertEquals(category.getIsDefault(), false);
        assertEquals(defaultCategoryCount(), 1);

        // Sole Default will be Wannabe Category
        Category updated = categoryService.updateCategory(new CategoryDTO(5L, "Wannabe", 0, true, true));
        assertEquals(category.getIsDefault(), true);
        assertEquals(defaultCategoryCount(), 1);
    }

    @Test
    public void deletedCategoryIncreasesUncategorizedBySame() throws Exception {
        // H2 "ShortTimer" category removed, existing posts assigned "uncategorized"

        int startCount = postService.getAllPostsByCategoryId(1L).size();

        List<Post> posts = postService.getAllPostsByCategoryId(6L);
        int postCount = posts.size();

        categoryService.deleteCategory(new CategoryDTO(6L, "shorttimer", 1, true, false), posts);
        int endCount = postService.getAllPostsByCategoryId(1L).size();

        assertEquals(endCount, startCount + postCount);
    }

    @Test
    public void uncategorizedCategoryIsNotDeleted() throws Exception {
        // H2Data Status: Uncategorized is CategoryId 1
        assertTrue(startCount_isEqual_to_endCount(1L));

        // H2Data Status: PHP is CategoryId 4
        assertFalse(startCount_isEqual_to_endCount(4L));

    }

    @Test
    public void uncategorizedCategoryIsNotUpdated() throws Exception {
        // H2Data Status: Uncategorized is CategoryId 1
        Category uncategorized = categoryService.getCategoryById(1L);
        assertEquals(uncategorized.getCategoryValue(), "Uncategorized");
        Category updated = categoryService.updateCategory(new CategoryDTO(1L, "Categorized", 0, true, false));

        uncategorized = categoryService.getCategoryById(1L);
        assertEquals(uncategorized.getCategoryValue(), "Uncategorized");

    }

    @Test
    public void updatedPostContainsNewlyAssignedCategory() throws DuplicatePostNameException, PostNotFoundException {

        Post post = permaPostService.getPostById(1L);
        assertEquals(post.getCategory().getCategoryValue(), "Uncategorized");

        PostDTO postDTO = PostUtils.postToPostDTO(post);
        postDTO.setCategoryId(2L);
        post = postService.update(postDTO);
        assertEquals(post.getCategory().getCategoryValue(), "Java");

    }

    private int defaultCategoryCount() {
        List<Category> categories = categoryService.getAllCategories();
        return (int) categories.stream().filter(c -> c.getIsDefault().equals(true)).count();
    }

    private boolean startCount_isEqual_to_endCount(long categoryId) {
        int startCount = categoryService.getAllCategories().size();
        Category category = categoryService.getCategoryById(categoryId);
        categoryService.deleteCategory(new CategoryDTO(categoryId, "something", 0, true, false), null);
        int endCount = categoryService.getAllCategories().size();
        return startCount == endCount;
    }

    // endregion

}
