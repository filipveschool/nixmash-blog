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
public class PermaPostServiceTest extends SpringDataTests {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final String WOW_CATEGORY_NAME = "Wowwa Category";
    private static final String NONEXISTENT_CATEGORY_NAME = "MAMMA LAMMA";

    @Autowired
    private PermaPostService permaPostService;



    // region Misc tests

    @Test(expected = PostNotFoundException.class)
    public void negativePostIdStub_NotYetSelected() throws PostNotFoundException {
        Post post = permaPostService.getPostById(-1L);
        assertEquals(post.getPostName(), "not-yet-selected");

    }

    // endregion

}
