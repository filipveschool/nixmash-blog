package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.SpringDataTests;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.LikeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@Transactional
public class LikeServiceTest extends SpringDataTests {

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
    private LikeService likeService;

    // region Likes

    @Test
    public void getPostsByUserLikes() {
        // userId 3 "keith" has 3 likes, userId 2 "user" has 2 likes
        List<Post> posts = likeService.getPostsByUserLikes(3L);
        assertNotNull(posts);
    }

    @Test
    public void getLikedPostsForUserWithNoLikes_NotNull() {
        // no likes for userId = 6 in H2 data
        List<Post> posts = likeService.getPostsByUserLikes(6L);
        assertNull(posts);
    }


    @Test
    public void addLikedPost_UserWithNoLikes_ReturnsPlusOne()
            throws PostNotFoundException {

        //  H2DATA:  no likes for any posts for userId 4 ------------------------------------ */

        // get initial LikeCount for postId 3
        int likeCount = permaPostService.getPostById(3L).getLikesCount();

        // new Like for postId from userId 4. Should return increment value 1
        int increment = likeService.addPostLike(4L, 3L);
        assertEquals(increment, 1);

        // LikeCount for postId 3 should increment by 1
        int updatedLikeCount = permaPostService.getPostById(3L).getLikesCount();
        assertEquals(updatedLikeCount, likeCount + 1);

        // confirm like added to user_likes table
        Optional<Long> likeId = likeRepository.findPostLikeIdByUserId(4L, 3L);
        assert (likeId.isPresent());
    }

    @Test
    public void addLikedPost_UserPreviouslyLikedPost_ReturnsMinusOne()
            throws PostNotFoundException {

        //  H2DATA: userId  3 has pre-existing Like for postId 10------------------------------------ */

        // initial Like count for postId 3
        int likeCount = permaPostService.getPostById(10L).getLikesCount();

        // addPostLike(userId, postId) should return -1 which removes existing Post Like
        int increment = likeService.addPostLike(3L, 10L);
        assertEquals(increment, -1);

        // postId 3 should have one less LikeCount
        int updatedLikeCount = permaPostService.getPostById(10L).getLikesCount();
        assertEquals(updatedLikeCount, likeCount - 1);

        // pre-existing like removes record from user_likes table: should NOT be present
        Optional<Long> likeId = likeRepository.findPostLikeIdByUserId(3L, 10L);
        assert (!likeId.isPresent());
    }

    @Test
    public void pagedLikedPostsTest() {
        List<Post> posts = likeService.getPagedLikedPosts(3, 0, 2);
        // list contains 2 posts
        assertEquals(posts.size(), 2);

        ZonedDateTime firstPostDate = posts.get(0).getPostDate();
        ZonedDateTime secondPostDate = posts.get(1).getPostDate();

        // first PostDate is higher (more recent) than second PostDate [sort: postDate: DESC]
        assertTrue(firstPostDate.compareTo(secondPostDate) > 0);

    }

}
