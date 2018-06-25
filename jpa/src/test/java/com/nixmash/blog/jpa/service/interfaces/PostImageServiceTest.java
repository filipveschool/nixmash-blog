package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.SpringDataTests;
import com.nixmash.blog.jpa.model.PostImage;
import com.nixmash.blog.jpa.repository.LikeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@Transactional
public class PostImageServiceTest extends SpringDataTests {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final String WOW_CATEGORY_NAME = "Wowwa Category";
    private static final String NONEXISTENT_CATEGORY_NAME = "MAMMA LAMMA";

    @Autowired
    private PostImageService postImageService;

    @Autowired
    private PermaPostService permaPostService;

    @Autowired
    LikeRepository likeRepository;


    @Test
    public void postImagesLoad() {
        List<PostImage> postImages = postImageService.getPostImages(1L);
        assertNotNull(postImages);
    }

    @Test
    public void allPostImagesLoad() {
        List<PostImage> postImages = postImageService.getAllPostImages();
        assertNotNull(postImages);
    }

}
