package com.nixmash.blog.jpa.service;

import com.nixmash.blog.jpa.SpringDataTests;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.LikeRepository;
import com.nixmash.blog.jpa.service.interfaces.PermaPostService;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.utils.PostTestUtils;
import com.nixmash.blog.jpa.utils.PostUtils;
import org.hamcrest.Matchers;
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
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@Transactional
public class PostServiceTests extends SpringDataTests {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final String WOW_CATEGORY_NAME = "Wowwa Category";
    private static final String NONEXISTENT_CATEGORY_NAME = "MAMMA LAMMA";

    @Autowired
    private PostService postService;

    @Autowired
    private PermaPostService permaPostService;

    @Autowired
    LikeRepository likeRepository;

    // region Posts

    @Test
    public void addPostDTO() throws DuplicatePostNameException {
        PostDTO postDTO = PostTestUtils.createPostDTO(1);
        Post post = postService.add(postDTO);
        assertNotNull(post);
    }

    @Test
    public void postContainsAuthorObject() throws Exception {
        Post post = permaPostService.getPostById(1L);
        assertNotNull(post.author);
    }

    @Test
    public void unpublishedPost_ShouldNotBeReturned_InFindAll() throws DuplicatePostNameException {
        PostDTO postDTO = PostTestUtils.createPostDTO(2);
        postDTO.setIsPublished(false);
        Post post = postService.add(postDTO);
        long postId = post.getPostId();
        assertThat(postId, greaterThan(1L));

        List<Post> posts = postService.getAllPublishedPosts();
        Optional<Post> unpublishedPost;
        unpublishedPost = posts.stream().filter(p -> p.getPostId().equals(post.getPostId())).findFirst();
        assertFalse(unpublishedPost.isPresent());

        List<Post> allposts = postService.getAllPosts();
        unpublishedPost = allposts.stream().filter(p -> p.getPostId().equals(post.getPostId())).findFirst();
        assertTrue(unpublishedPost.isPresent());
    }

    @Test
    public void updatePostDTO() throws PostNotFoundException {
        Post post = permaPostService.getPostById(1L);
        PostDTO postDTO = PostUtils.postToPostDTO(post);
        String newTitle = "New Title 897";
        postDTO.setPostTitle(newTitle);
        Post update = postService.update(postDTO);
        assertEquals(update.getPostTitle(), newTitle);

        // PostName does not change...yet
        assertEquals(update.getPostName(), PostUtils.createSlug(post.getPostName()));
    }

    @Test
    public void builderShouldReturn_Null_ForMalformedLink() {
        PostDTO postDTO1 = new PostDTO();
        postDTO1.setUserId(USER_ID);
        postDTO1.setPostTitle(POST_TITLE);
        postDTO1.setPostName(POST_NAME);
        postDTO1.setPostLink("malformed.link");
        postDTO1.setPostContent(POST_CONTENT);
        postDTO1.setPostType(POST_TYPE);
        postDTO1.setDisplayType(DISPLAY_TYPE);
        postDTO1.setCategoryId(CATEGORY_ID);
        postDTO1.setTwitterCardType(TWITTER_CARD_SUMMARY);

        assertEquals(postDTO1.getPostSource(), null);
    }

    @Test
    public void builderShouldReturnDomainAsPostSourceFromLink() {
        PostDTO postDTO1 = new PostDTO();
        postDTO1.setUserId(USER_ID);
        postDTO1.setPostTitle(POST_TITLE);
        postDTO1.setPostName(POST_NAME);
        postDTO1.setPostLink("http://wellformed.link");
        postDTO1.setPostContent(POST_CONTENT);
        postDTO1.setPostType(POST_TYPE);
        postDTO1.setDisplayType(DISPLAY_TYPE);
        postDTO1.setCategoryId(CATEGORY_ID);
        postDTO1.setTwitterCardType(TWITTER_CARD_SUMMARY);
        assertEquals(postDTO1.getPostSource(), "wellformed.link");
    }

    @Test
    public void builderShouldReturn_Null_ForNullLink() {
        PostDTO postDTO1 = new PostDTO();
        postDTO1.setUserId(USER_ID);
        postDTO1.setPostTitle(POST_TITLE);
        postDTO1.setPostName(POST_NAME);
        postDTO1.setPostLink(null);
        postDTO1.setPostContent(POST_CONTENT);
        postDTO1.setPostType(POST_TYPE);
        postDTO1.setDisplayType(DISPLAY_TYPE);
        postDTO1.setCategoryId(CATEGORY_ID);
        postDTO1.setTwitterCardType(TWITTER_CARD_SUMMARY);

        assertEquals(postDTO1.getPostSource(), null);
    }

    @Test
    public void postDtoToPostShouldRetainPostSource() {
        PostDTO postDTO1 = new PostDTO();
        postDTO1.setUserId(USER_ID);
        postDTO1.setPostTitle(POST_TITLE);
        postDTO1.setPostName(POST_NAME);
        postDTO1.setPostLink("http://wellformed.link");
        postDTO1.setPostContent(POST_CONTENT);
        postDTO1.setPostType(POST_TYPE);
        postDTO1.setDisplayType(DISPLAY_TYPE);
        postDTO1.setCategoryId(CATEGORY_ID);
        postDTO1.setTwitterCardType(TWITTER_CARD_SUMMARY);

        assertEquals(postDTO1.getPostSource(), "wellformed.link");
        Post post = postDtoToPost(postDTO1);
        assertEquals(post.getPostSource(), "wellformed.link");
    }

    @Test
    public void postDtoToPostShouldRetainPostSourceOf_NA_ForNullLink() {

        PostDTO postDTO1 = new PostDTO();
        postDTO1.setUserId(USER_ID);
        postDTO1.setPostTitle(POST_TITLE);
        postDTO1.setPostName(POST_NAME);
        postDTO1.setPostLink(null);
        postDTO1.setPostContent(POST_CONTENT);
        postDTO1.setPostType(POST_TYPE);
        postDTO1.setDisplayType(DISPLAY_TYPE);
        postDTO1.setCategoryId(CATEGORY_ID);
        postDTO1.setTwitterCardType(TWITTER_CARD_SUMMARY);

        assertEquals(postDTO1.getPostSource(), null);
        Post post = postDtoToPost(postDTO1);
        assertEquals(post.getPostSource(), null);
    }

    @Test
    public void findAllWithPaging() {
        Slice<Post> posts = postService.getPosts(0, 3);
        assertEquals(posts.getSize(), 3);
        ZonedDateTime firstPostDate = posts.getContent().get(0).getPostDate();
        ZonedDateTime secondPostDate = posts.getContent().get(1).getPostDate();

        // firstPostDate is higher (more recent) than secondPostDate with [sort: postDate: DESC]
        assertTrue(firstPostDate.compareTo(secondPostDate) > 0);
    }

    @Test
    public void getAllPostsIsGreaterThanPagedTotal() {
        List<Post> posts = postService.getAllPosts();
        assertThat(posts.size(), Matchers.greaterThan(3));
        ZonedDateTime firstPostDate = posts.get(0).getPostDate();
        ZonedDateTime secondPostDate = posts.get(1).getPostDate();

        // firstPostDate is higher (more recent) than secondPostDate with [sort: postDate: DESC]
        assertTrue(firstPostDate.compareTo(secondPostDate) > 0);
    }

    @Test
    public void findPostsByTagId() {
        Slice<Post> posts = postService.getPublishedPostsByTagId(1, 0, 3);

        // posts are retrieved for tagId #1 as all 5 H2 posts have tagId #1
        assertEquals(posts.getSize(), 3);
    }

    @Test
    public void findAllWithDetails() {
        List<Post> posts = postService.getPostsWithDetail();
        assertNotNull(posts);
    }

    @Test
    public void getPostsByPostType() throws Exception {
        List<Post> posts;
        posts = postService.getAllPublishedPostsByPostType(PostType.POST);
        assertNotNull(posts);
        posts = postService.getAllPublishedPostsByPostType(PostType.LINK);
        assertNotNull(posts);
    }

    @Test
    public void getPagedPostsByPostType() throws Exception {
        Page<Post> posts;
        posts = postService.getPagedPostsByPostType(PostType.POST, 0, 10);
        assertNotNull(posts);
        posts = postService.getPagedPostsByPostType(PostType.LINK, 0, 10);
        assertNotNull(posts);
    }

    // endregion
}
