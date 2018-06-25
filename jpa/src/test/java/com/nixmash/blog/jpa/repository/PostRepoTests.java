package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.config.ApplicationConfig;
import com.nixmash.blog.jpa.enums.DataConfigProfile;
import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.nixmash.blog.jpa.utils.PostTestUtils.getTestCategory;
import static com.nixmash.blog.jpa.utils.PostTestUtils.getTestTags;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by daveburke on 5/31/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@ActiveProfiles(DataConfigProfile.H2)
public class PostRepoTests {

    @Autowired
    PostRepository postRepository;

    @Autowired
    TagRepository tagRepository;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
    }

    @Test
    public void nonextistentPostFromRepository() {
        Post post = postRepository.findByPostId(-100L);
        assertNull(post);
    }

    @Test
    public void retrievePostFromRepository() {
        Post post = postRepository.findByPostId(1L);
        assertNotNull(post);
    }

    @Test
    public void newLinkHasPostSourceDomain() {
        Post post = new Post();
        post.setUserId(1L);
        post.setPostType(PostType.LINK);
        post.setDisplayType(PostDisplayType.LINK_SUMMARY);
        post.setPostContent("New post content!");
        post.setPostLink("http://linksource.com");
        post.setPostTitle("New Link");
        post.setPostName("new-link");
        post.setTags(getTestTags(2));
        post.setCategory(getTestCategory());

        Post saved = postRepository.save(post);
        assertNotNull(saved);
        assertEquals(saved.getPostType(), PostType.LINK);

        // postSource is domain of url passed to builder
        assertEquals(saved.getPostSource(), "linksource.com");
    }

    @Test
    public void newCategoryAdded() {
        Post post = new Post();
        post.setUserId(1L);
        post.setPostType(PostType.POST);
        post.setDisplayType(PostDisplayType.POST);
        post.setPostContent("New post content!");
        post.setPostLink(null);
        post.setPostTitle("New Title");
        post.setPostName("new-title");
        post.setTags(getTestTags(2));
        post.setCategory(getTestCategory());

        Post saved = postRepository.save(post);
        assertNotNull(saved);
        assertEquals(saved.getPostType(), PostType.POST);

        // postSource is domain of url passed to builder
        assertEquals(saved.getPostSource(), null);
    }

    @Test
    public void nullPostLinkEnteredAndResultsInPostSourceAsNull() {
        Post post = new Post();
        post.setUserId(1L);
        post.setPostTitle("Nuther New Title");
        post.setPostName("nuther-new-title");
        post.setPostLink(null);
        post.setPostContent("New post content!");
        post.setPostType(PostType.POST);
        post.setDisplayType(PostDisplayType.POST);

        Post saved = postRepository.save(post);
        assertNotNull(saved);
        assertNull(saved.getPostLink());
        assertEquals(saved.getPostSource(), null);
    }

    @Test
    public void savePostWithTags() {
        Post post = new Post();
        post.setUserId(1L);
        post.setPostTitle("Post With Tags");
        post.setPostName("post-with-tags");
        post.setPostLink(null);
        post.setPostContent("New post with tags!");
        post.setPostType(PostType.POST);
        post.setDisplayType(PostDisplayType.POST);

        Tag tag1 = new Tag();
        tag1.setTagValue("third tag");
        tag1 = tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setTagValue("fourth tag");
        tag2 = tagRepository.save(tag2);

        Post saved = postRepository.save(post);

        saved.setTags(new HashSet<>());
        saved.getTags().add(tag1);
        saved.getTags().add(tag2);
        assertEquals(saved.getTags().size(), 2);

        postRepository.save(saved);

        List<Post> posts = postRepository.findAllWithDetail();
        Optional<Post> found = posts.stream()
                .filter(p -> p.getPostId().equals(saved.getPostId())).findFirst();

        if (found.isPresent()) {
            assertEquals(found.get().getTags().size(), 2);
        }
    }

    @Test
    public void addTags() {

        int startTagCount = tagRepository.findAll().size();

        Tag tag = new Tag("tag one");
        tagRepository.save(tag);

        tag = new Tag("tag two ");
        tagRepository.save(tag);

        Set<Tag> found = tagRepository.findAll();
        assertEquals(found.size(), startTagCount + 2);
    }

    @Test
    public void alphaLinksStringShouldBeAString() {
        assertThat(postRepository.getAlphaLinkString(), isA(String.class));
    }
}
