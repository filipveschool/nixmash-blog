package com.nixmash.blog.jpa.components;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.common.ISiteOption;
import com.nixmash.blog.jpa.common.SiteOptions;
import com.nixmash.blog.jpa.dto.AlphabetDTO;
import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.SiteOptionDTO;
import com.nixmash.blog.jpa.enums.BatchJobName;
import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.SiteOptionNotFoundException;
import com.nixmash.blog.jpa.model.BatchJob;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.SiteImage;
import com.nixmash.blog.jpa.service.interfaces.CategoryService;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.service.interfaces.SiteService;
import com.nixmash.blog.jpa.service.interfaces.StatService;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static com.nixmash.blog.jpa.utils.SharedUtils.timeMark;
import static com.nixmash.blog.jpa.utils.SharedUtils.totalTime;

@Slf4j
@Component
public class JpaUI {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ApplicationSettings applicationSettings;
    @Autowired
    DefaultListableBeanFactory beanfactory;

    @Autowired
    private SiteOptions siteOptions;

    @Autowired
    private StatService statService;

    @Autowired
    private Environment environment;

    // endregion

    public void init() {
        String activeProfile = environment.getActiveProfiles()[0];
        log.info(String.format("Current JPA Active Profile: %s", activeProfile));

        displaySiteImageInfo();
    }

    // region SiteImages
    private void displaySiteImageInfo() {
        SiteImage siteImage = siteService.getHomeBanner();
        System.out.println(String.format("SiteImageId: %s | Filename: %s", siteImage.getSiteImageId(), siteImage.getImageFilename()));
    }

    // endregion

    // region Categories

    private void displayCategoryCounts() {
        List<CategoryDTO> categoryDTOS = categoryService.getCategoryCounts();
        for (CategoryDTO categoryDTO: categoryDTOS) {
            System.out.println(MessageFormat.format("{0} | {1} | {2}",
                    categoryDTO.getCategoryId(), categoryDTO.getCategoryValue(), categoryDTO.getCategoryCount()));
        }
    }
    // endregion

    // region BatchJob Reports and GitHub Stats

    private void getBatchJobs() {
        List<BatchJob> batchJobs = statService.getBatchJobsByJob(BatchJobName.DEMOJOB);
        for (BatchJob batchJob: batchJobs) {
            System.out.println(batchJob);
        }
    }

    // endregion

    // region Posts

    private void displayPosts() {
        List<Post> posts = postService.getAllPosts();
        for (Post post: posts) {
            System.out.println(post.getPostId() + " : " + post.getPostTitle() + " : " + post.getCategory().getCategoryValue());
        }
    }

    // endregion

    // region cache play

    public void allPublishedPostsCache() {
        List<Post> posts;
        long start;
        long end;

        System.out.println();
        start = timeMark();
        postService.getPublishedPosts(0, 25);
        end = timeMark();
        System.out.println("Retrieval time getPublishedPosts(0, 25): " + totalTime(start, end));

        start = timeMark();
        postService.getPublishedPosts(0, 25);
        end = timeMark();
        System.out.println("Repeat retrieval time getPublishedPosts(0, 25): " + totalTime(start, end));

        System.out.println();
        start = timeMark();
        postService.getPublishedPosts(1, 25);
        end = timeMark();
        System.out.println("Retrieval time getPublishedPosts(1, 25): " + totalTime(start, end));

        start = timeMark();
        postService.getPublishedPosts(1, 25);
        end = timeMark();
        System.out.println("Repeat retrieval time getPublishedPosts(1, 25): " + totalTime(start, end));

    }

    // endregion

    private void generateAlphabet() {

        List<AlphabetDTO> alphaLinks = postService.getAlphaLInks();
        for (AlphabetDTO alphaLink: alphaLinks) {
            System.out.println(alphaLink.getAlphaCharacter() + " " + alphaLink.getIsActive());
        }
    }

    private void displayRandomUserIdString() {
        System.out.println(RandomStringUtils.randomAlphanumeric(16));
    }

    private void addPostDemo() throws DuplicatePostNameException {
        String title = "Best way to create SEO friendly URI string";
        PostDTO postDTO = new PostDTO();
        postDTO.setUserId(1L);
        postDTO.setPostTitle(title);
        postDTO.setPostName(PostUtils.createSlug(title));
        postDTO.setPostLink("http://nixmash.com/java/variations-on-json-key-value-pairs-in-spring-mvc/");
        postDTO.setPostContent("This is the post content");
        postDTO.setPostType(PostType.LINK);
        postDTO.setDisplayType(PostDisplayType.LINK_FEATURE);
        postDTO.setCategoryId(1L);
        postDTO.setTwitterCardType(TwitterCardType.SUMMARY);
        postService.add(postDTO);
    }

    private void siteOptionsDemo() {
        System.out.println("Initialized SiteOptions Bean Property: " +
                siteOptions.getGoogleAnalyticsTrackingId());

        Boolean reset = true;
        String siteName = reset ? "My Site" : "My Updated Site Name";
        String userRegistration = "EMAIL_VERIFICATION";

        try {
            siteService.update(new SiteOptionDTO(ISiteOption.SITE_NAME, siteName));
            siteService.update(new SiteOptionDTO(ISiteOption.USER_REGISTRATION, userRegistration));
        } catch (SiteOptionNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("New SiteOptions values: " + siteOptions.getSiteName());
        System.out.println("GoogleAnalyticsId: " + siteOptions.getGoogleAnalyticsTrackingId());
        System.out.println("UserRegistration: " + siteOptions.getUserRegistration());
    }

}
