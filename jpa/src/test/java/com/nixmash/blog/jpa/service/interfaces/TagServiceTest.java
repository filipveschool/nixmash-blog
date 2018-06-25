package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.SpringDataTests;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.LikeRepository;
import com.nixmash.blog.jpa.utils.PostTestUtils;
import com.nixmash.blog.jpa.utils.PostUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Transactional
public class TagServiceTest extends SpringDataTests {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final String WOW_CATEGORY_NAME = "Wowwa Category";
    private static final String NONEXISTENT_CATEGORY_NAME = "MAMMA LAMMA";

    @Autowired
    private PostService postService;

    @Autowired
    private PermaPostService permaPostService;

    @Autowired
    private TagService tagService;

    // region Tags

    @Test
    public void addPostWithTags() throws DuplicatePostNameException, PostNotFoundException {
        PostDTO postDTO = PostTestUtils.createPostDTO(3);
        postDTO.getTags().add(new TagDTO("addPostWithTags1"));
        postDTO.getTags().add(new TagDTO("addPostWithTags2"));
        Post post = postService.add(postDTO);
        assertEquals(post.getTags().size(), 2);

        Post retrieved = permaPostService.getPostById(post.getPostId());
        assertEquals(retrieved.getTags().size(), 2);
    }

    @Test
    public void updatePostWithTags() throws DuplicatePostNameException, PostNotFoundException {

        // Post(5L) is loaded with 2 tags in H2
        Post post = permaPostService.getPostById(5L);
        PostDTO postDTO = PostUtils.postToPostDTO(post);
        postDTO.getTags().add(new TagDTO("updatePostWithTags1"));
        Post updated = postService.update(postDTO);
        assertEquals(updated.getTags().size(), 3);

        Post retrieved = permaPostService.getPostById(5L);
        assertEquals(retrieved.getTags().size(), 3);
    }

    @Test
    public void getTagCloud_TagListNotNull() throws Exception {
        List<TagDTO> tagcloud = tagService.getTagCloud(50);
        assertThat(tagcloud.get(0).getTagCount(), greaterThan(0));
        assertNotNull(tagcloud);
    }

    // endregion


}
