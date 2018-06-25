package com.nixmash.blog.jpa.utils;

import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.model.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by daveburke on 6/1/16.
 */
public class PostTestUtils {

    public static final Long USER_ID = 1L;
    public static final Long CATEGORY_ID = 1L;
    public static final String POST_TITLE = "Post title";
    public static final String POST_NAME = "post-title";
    public static final String POST_LINK = "http://test.link";
    public static final String POST_CONTENT = "Post content.";
    public static final PostType POST_TYPE = PostType.POST;
    public static final PostDisplayType DISPLAY_TYPE = PostDisplayType.LINK;
    public static final TwitterCardType TWITTER_CARD_SUMMARY = TwitterCardType.SUMMARY;
    public static final String JAVA_CATEGORY_VALUE = "Java";


    public static PostDTO createPostDTO(int i) {
        return PostDTO.getBuilder(USER_ID,
                fieldit(POST_TITLE, i), fieldit(POST_NAME, i), POST_LINK, POST_CONTENT, POST_TYPE, DISPLAY_TYPE, CATEGORY_ID, TWITTER_CARD_SUMMARY)
                .tags(getTestTagDTOs(2))
                .build();
    }

    private static String fieldit(String field, int i) {
        return String.format("%s-%d", field, i);
    }


    public static PostDTO createPostDTO(String appender) {
        return PostDTO.getBuilder(USER_ID,
                fieldit(POST_TITLE, appender), fieldit(POST_NAME, appender), POST_LINK, POST_CONTENT, POST_TYPE, DISPLAY_TYPE, CATEGORY_ID, TWITTER_CARD_SUMMARY)
                .tags(getTestTagDTOs(2))
                .build();
    }

    public static List<Post> createPostList(int n) {
        return IntStream.range(1, n + 1).mapToObj(i -> createPostDTO(n)).map(PostUtils::postDtoToPost).collect(Collectors.toCollection(() -> new ArrayList<>(n)));
    }

    public static Set<TagDTO> getTestTagDTOs(int i) {
        return IntStream.range(1000, i).mapToObj(j -> new TagDTO(i, "tag-" + i)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<Tag> getTestTags(int i) {
        return IntStream.range(1, i).mapToObj(j -> new Tag((long) i, "tag-" + i)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static CategoryDTO getTestCategoryDTO() {
        return new CategoryDTO(2L, "Java", 0, true, true);
    }

    public static Category getTestCategory() {
        return new Category(2L, JAVA_CATEGORY_VALUE, true, true);
    }

    public static Category getUncategorizedCategory() {
        return new Category(1L, "Uncategorized", true, true);
    }

    private static String fieldit(String field, String appender) {
        return String.format("%s-%s", field, appender);
    }

    public static PostMeta createPostMeta() {
        return PostMeta.getBuilder(TwitterCardType.SUMMARY,
                "Test Title", "@testsite", "@testblogger")
                .twitterDescription("This is a test card")
                .postId(1L)
                .twitterImage("http://testsite.com/x/test/myimage.png")
                .twitterUrl("http://testsite.com/post/test-post")
                .build();
    }
}
