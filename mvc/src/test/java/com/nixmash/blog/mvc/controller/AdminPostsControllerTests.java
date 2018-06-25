package com.nixmash.blog.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.model.Tag;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.utils.PostTestUtils;
import com.nixmash.blog.mvc.AbstractContext;
import com.nixmash.blog.mvc.MvcTestUtil;
import com.nixmash.blog.mvc.annotations.WithAdminUser;
import com.nixmash.blog.mvc.dto.JsonPostDTO;
import com.nixmash.blog.solr.service.PostDocService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.nixmash.blog.mvc.controller.AdminPostsController.*;
import static com.nixmash.blog.mvc.security.SecurityRequestPostProcessors.csrf;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@WithAdminUser
public class AdminPostsControllerTests  extends AbstractContext{

    // region Logger and Constants

    private static final Logger logger = LoggerFactory.getLogger(AdminPostsControllerTests.class);

    private static final String POST_CONSTANT = "POST";
    private static final String TWITTER_SUMMARY = "SUMMARY";
    private static final String SUMMARY_LARGE_IMAGE = "SUMMARY_LARGE_IMAGE";
    private static final String LINK_CONSTANT = "LINK";
    private static final String GOOD_URL = "http://nixmash.com/some-post/";

    // endregion

    // region  Variables

    private JacksonTester<JsonPostDTO> json;

    private MockMvc mvc;
    private String azTestFileName;

    // endregion

    // region Beans

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationSettings applicationSettings;

    @Autowired
    private SolrOperations solrOperations;

    @Autowired
    private PostService postService;

    @Autowired
    private PostDocService postDocService;

    @Autowired
    protected WebApplicationContext wac;

    // endregion

    // region Before / After

    @Before
    public void setup() throws ServletException {

        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);

        mvc = webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        azTestFileName = applicationSettings.getPostAtoZFilePath() +
                environment.getProperty("posts.az.file.name");
    }

    @After
    public void tearDown() {
    }

    // endregion

    // region Pages Load

    @Test
    public void AZFileNameIsAZTest() throws Exception {
        assertThat(environment.getProperty("posts.az.file.name"), is("aztest.html"));
    }

    @Test
    public void postsList_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts").with(csrf());
        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_POSTS_LIST_VIEW)).andReturn();

        assertThat(result.getModelAndView().getModel().get("posts"),
                is(instanceOf(ArrayList.class)));
    }

    @Test
    public void postsList_allPosts_present() throws Exception {
        RequestBuilder request = get("/admin/posts?allposts=true")
                .with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("allposts"))
                .andExpect(view().name(ADMIN_POSTS_LIST_VIEW));
    }

    @Test
    public void postsList_allPosts_isNotPresent() throws Exception {
        RequestBuilder request = get("/admin/posts")
                .with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeDoesNotExist("allposts"))
                .andExpect(view().name(ADMIN_POSTS_LIST_VIEW));
    }

    // endregion

    // region Posts

    @Test
    public void addPost_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts/add/post").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postDTO"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(view().name(ADMIN_POST_ADD_VIEW));

    }

    @Test
    public void updatePostWithMissingTitle_ReturnsToPage() throws Exception {
        RequestBuilder request = post("/admin/posts/update")
                .param("postContent", "postContent").with(csrf());

        mvc.perform(request)
                .andExpect(model().attributeHasFieldErrors("postDTO", "postTitle"))
                .andExpect(view().name(ADMIN_POSTLINK_UPDATE_VIEW));
    }

    @Test
    public void updatePostWithValidData_RedirectsToPermalinkPage() throws Exception {

        String newTitle = "New Title for updatePostWithValidData_RedirectsToPermalinkPage Test";

        Post post = postService.getPostById(1L);
        RequestBuilder request = post("/admin/posts/update")
                .param("postId", "1")
                .param("displayType", String.valueOf(post.getDisplayType()))
                .param("postContent", post.getPostContent())
                .param("twitterCardType", post.getPostMeta().getTwitterCardType().name())
                .param("postTitle", newTitle)
                .param("tags", "updatePostWithValidData1, updatePostWithValidData2")
                .with(csrf());

        mvc.perform(request)
                .andExpect(model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.flash().attributeExists("feedbackMessage"))
                .andExpect(redirectedUrl("/admin/posts"));

        Post updatedPost = postService.getPostById(1L);
        assert (updatedPost.getPostTitle().equals(newTitle));
    }

    @Test
    public void updatedPostTitleInAzIncludeFile() throws Exception {

        String newTitle = "New Title for updatedPostTitleInAzIncludeFile Test";

        Post post = postService.getPostById(1L);
        RequestBuilder request = post("/admin/posts/update")
                .param("postId", "1")
                .param("displayType", String.valueOf(post.getDisplayType()))
                .param("postContent", post.getPostContent())
                .param("postTitle", newTitle)
                .param("twitterCardType", TWITTER_SUMMARY)
                .param("tags", "one, two")
                .with(csrf());

        mvc.perform(request);

        File azFile = new File(azTestFileName);
        String contents = FileUtils.readFileToString(azFile);
        assertTrue(contents.contains(newTitle));
    }

    @Test
    public void saveAndContinueWithValidData_UpdatesPost() throws Exception  {

        String newTitle = "saveAndContinueWithValidData_UpdatesPost";
        Post post = postService.getPostById(8L);
        JsonPostDTO jsonPostDTO = MvcTestUtil.createJsonPostDTO(post);
        jsonPostDTO.setPostTitle(newTitle);

        mvc.perform(post("/admin/posts/archive")
                .content(json.write(jsonPostDTO).getJson())
                .contentType(APPLICATION_JSON_UTF8)
                .with(csrf()))
                .andExpect(content().string(containsString("SUCCESS")));

        post = postService.getPostById(8L);
        assertEquals(post.getPostTitle(), newTitle);

        File azFile = new File(azTestFileName);
        String contents = FileUtils.readFileToString(azFile);
        assertFalse(contents.contains(newTitle));
    }

    @Test
    public void saveAndContinueWithBadData_PostUnchanged() throws Exception  {

        Post post = postService.getPostById(8L);
        String originalTitle = post.getPostTitle();

        JsonPostDTO jsonPostDTO = MvcTestUtil.createJsonPostDTO(post);
        jsonPostDTO.setPostTitle("saveAndContinueWithBadData_PostUnchanged");
        jsonPostDTO.setPostContent(StringUtils.EMPTY);

        logger.info(this.json.write(jsonPostDTO).getJson());

        mvc.perform(post("/admin/posts/archive")
                .content(json.write(jsonPostDTO).getJson())
                .contentType(APPLICATION_JSON_UTF8)
                .with(csrf()))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andExpect(content().string(containsString("ERROR")));

        post = postService.getPostById(8L);
        assertEquals(post.getPostTitle(), originalTitle);
    }

    @Test(expected = PostNotFoundException.class)
    public void badPostIdOnPostUpdate_ThrowsPostNotFoundException() throws Exception {

        when(postService.getPostById(-2L))
                .thenThrow(new PostNotFoundException());

        mvc.perform(get("/admin/posts/update/-2"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/custom"));
    }

    @Test
    public void addsTwoNewTags() throws Exception {
        int tagStartCount = postService.getTagDTOs().size();
        mvc.perform(addPostRequest("addsTwoTags"));
        int tagEndCount = postService.getTagDTOs().size();
        assertEquals(tagStartCount + 2, tagEndCount);
    }

    @Test
    public void addNewPostRecord() throws Exception {
        int postStartCount = postService.getAllPosts().size();
        mvc.perform(addPostRequest("addNewPostRecord"));
        int postEndCount = postService.getAllPosts().size();
        assertEquals(postStartCount + 1, postEndCount);
    }

    @Test
    public void newPublishedPostRedirectsToPostList() throws Exception {
        mvc.perform(addPostRequest("redirectsToPostList"))
                .andExpect(redirectedUrl("/admin/posts"));
    }

    @Test
    public void newUnpublishedPostReturnsPostAddView() throws Exception {
        mvc.perform(addPostRequest("returnsPostAddView", false, false, TwitterCardType.SUMMARY))
                .andExpect(view().name(ADMIN_POST_ADD_VIEW));
    }

    @Test
    public void removingTagFromPostDecreasesItsTagCount() throws Exception {
        Post post = postService.getPostById(1L);
        int postTagStartCount = post.getTags().size();

        // tag size of Post 1L is 3. Could be any value.
        // We are assigning a new tag, so the postTagEndCount
        // should be 2 less, or rather, always 1

        mvc.perform(post("/admin/posts/update")
                .param("postId", "1")
                .param("displayType", String.valueOf(post.getDisplayType()))
                .param("postContent", post.getPostContent())
                .param("postTitle", post.getPostTitle())
                .param("twitterCardType", TWITTER_SUMMARY)
                .param("tags", "removingTag1")
                .param("categoryId", "1")
                .with(csrf()));

        int postTagEndCount = post.getTags().size();
        assertEquals(postTagEndCount, 1);

        Post verifyPost = postService.getPostById(1L);
        assertEquals(verifyPost.getTags().size(), postTagEndCount);

    }

    // endregion

    // region PostMeta Updates


    @Test
    public void postMetaPageLoads() throws Exception {
        RequestBuilder request = get("/admin/posts/postmeta").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_POSTMETA_UPDATE_VIEW));
    }

    @Test
    public void updatePostMetas() throws Exception {
        RequestBuilder request = get("/admin/posts/postmeta")
                .param("update", "doit")
                .with(csrf());

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hasPostMetaCount"))
                .andExpect(view().name(ADMIN_POSTMETA_UPDATE_VIEW));

        List<Post> posts = postService.getAllPublishedPosts();

    }
    // endregion

    // region Post Categories

    @Test
    public void newPostRecordContainsUpdatedCategory() throws Exception {
        mvc.perform(solrCategoryRequest("solrCategory"));
        Post post = postService.getPost("my-title-solrcategory");
        assertEquals(post.getCategory().getCategoryValue().toLowerCase(), "solr");
    }

    @Test
    public void updatedPostContainsNewCategory() throws Exception {

        Post post = postService.getPostById(1L);
        post.setCategory(PostTestUtils.getUncategorizedCategory());
        assert(post.getCategory().getCategoryId().equals(1L));

        RequestBuilder request = post("/admin/posts/update")
                .param("postId", "1")
                .param("displayType", String.valueOf(post.getDisplayType()))
                .param("postContent", post.getPostContent())
                .param("postTitle", post.getPostTitle())
                .param("twitterCardType", post.getPostMeta().getTwitterCardType().name())
                .param("tags", "one, two")
                .param("categoryId", "3")
                .with(csrf());

        mvc.perform(request);

        post = postService.getPostById(1L);
        assert(post.getCategory().getCategoryId().equals(3L));

    }

    // endregion

    // region Post MetaData

    @Test
    public void newPostContainsTwitterCardInfo() throws Exception {

        mvc.perform(addTwitterCardPostRequest("bigtwitter",
                TwitterCardType.SUMMARY_LARGE_IMAGE, PostDisplayType.POST));
        Post post = postService.getPost("my-title-bigtwitter");

        assertEquals(post.getPostMeta().getTwitterCardType(),
                TwitterCardType.SUMMARY_LARGE_IMAGE);

        PostMeta postMeta = postService.getPostMetaById(post.getPostId());
        assertNotNull(postMeta);
    }

    @Test
    public void newPostWithTwitterCardTypeNoneIsSaved() throws Exception {
        mvc.perform(addTwitterCardPostRequest("notwitter", TwitterCardType.NONE, PostDisplayType.POST));
        Post post = postService.getPost("my-title-notwitter");
        assertEquals(post.getPostMeta().getTwitterCardType(), TwitterCardType.NONE);

        PostMeta postMeta = postService.getPostMetaById(post.getPostId());
        assertNotNull(postMeta);
    }

    @Test
    public void multiPhotoPostTwitterCardIsSaved() throws Exception {
        mvc.perform(addTwitterCardPostRequest("multiphoto twitter post", TwitterCardType.SUMMARY, PostDisplayType.MULTIPHOTO_POST));
        Post post = postService.getPost("my-title-multiphoto-twitter-post");
        assertEquals(post.getPostMeta().getTwitterCardType(), TwitterCardType.SUMMARY);

        PostMeta postMeta = postService.getPostMetaById(post.getPostId());
        assertNotNull(postMeta);
    }

    @Test
    public void updatedPostUpdatesTwitterCardInfo() throws Exception {
        Post post = postService.getPostById(1L);
        assert(post.getPostMeta().getTwitterCardType().equals(TwitterCardType.SUMMARY));

        RequestBuilder request = post("/admin/posts/update")
                .param("postId", "1")
                .param("displayType", String.valueOf(post.getDisplayType()))
                .param("postContent", post.getPostContent())
                .param("postTitle", post.getPostTitle())
                .param("twitterCardType", TwitterCardType.SUMMARY_LARGE_IMAGE.name())
                .param("tags", "one, two")
                .param("categoryId", "3")
                .with(csrf());

        mvc.perform(request);

        post = postService.getPostById(1L);
        assert(post.getPostMeta().getTwitterCardType().equals(TwitterCardType.SUMMARY_LARGE_IMAGE));
    }

    // endregion

    // region Links

    @Test
    public void addNewPublishedLink_IncreasesPostAndPostDocSize() throws Exception {

        // Adding a Published Link increases the PostCount and PostDocCount by 1

        String TITLE = "addNewPublishedLink";
        String SOLR_TITLE = "title:addNewPublishedLink";

        int postStartCount = postService.getAllPosts().size();
        int postDocStartCount = postDocService.getAllPostDocuments().size();

        mvc.perform(addLinkRequest(TITLE));

        int postEndCount = postService.getAllPosts().size();
        assertEquals(postStartCount + 1, postEndCount);

        int postDocEndCount = postDocService.getAllPostDocuments().size();
        assertEquals(postDocStartCount + 1, postDocEndCount);
        postDocService.removeFromIndex(SOLR_TITLE);

        File azFile = new File(azTestFileName);
        String contents = FileUtils.readFileToString(azFile);
        assertTrue(contents.contains("addNewPublishedLink"));
    }

    @Test
    public void addLink_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts/add/link").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postDTO"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(view().name(ADMIN_LINK_ADD_VIEW));
    }

    @Test
    public void addLink_page_loads_with_Url() throws Exception {
        mvc.perform(get("/admin/posts/add/link")
                .param("isLink", "true")
                .param("link", GOOD_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hasLink"))
                .andExpect(view().name(ADMIN_LINK_ADD_VIEW));
    }

    @Test
    public void addLink_page_returns_error_on_missing_Url() throws Exception {
        mvc.perform(get("/admin/posts/add/link")
                .param("isLink", "true")
                .param("link", "").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeHasFieldErrorCode("postLink", "link", "post.link.is.empty"))
                .andExpect(view().name(ADMIN_LINK_ADD_VIEW));
    }

    @Test
    public void throwErrorOnEmptyPostLink() throws Exception {

        mvc.perform(get("/admin/posts/add/link")
                .param("isLink", "true")
                .param("link", ""))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeHasFieldErrorCode("postLink", "link", "post.link.is.empty"))
                .andExpect(view().name(ADMIN_LINK_ADD_VIEW));
    }

    @Test
    public void updatePostLink_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts/update/1").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postDTO"))
                .andExpect(view().name(ADMIN_POSTLINK_UPDATE_VIEW));
    }

    // endregion

    // region Categories

    @Test
    public void categoriesList_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts/categories").with(csrf());
        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_CATEGORIES_VIEW)).andReturn();

        assertThat(result.getModelAndView().getModel().get("categories"),
                is(instanceOf(ArrayList.class)));
    }

    @Test
    public void newCategoryIncreasesCategorySize() throws Exception {
        int preCategoryCount = postService.getAllCategories().size();
        mvc.perform(addCategoryRequest("categoryIncreasesCount"))
                .andExpect(redirectedUrl("/admin/posts/categories"));

        int postCategoryCount = postService.getAllCategories().size();
        assertThat(postCategoryCount, is(greaterThan(preCategoryCount)));
    }

    @Test
    public void updateCategoryTests() throws Exception {
        // H2Data VALUES (4, 'PHP', 0, 0);
        Category category = postService.getCategory("PHP");

        mvc.perform(updateCategoryRequest(category.getCategoryId(), "NOPE", true, true))
                .andExpect(redirectedUrl("/admin/posts/categories"));

        Category updated = postService.getCategory("nope");
        assertEquals(category.getCategoryId(), updated.getCategoryId());
        assertEquals(category.getIsDefault(), true);
    }

    // endregion

    // region Tags

    @Test
    public void tagCloudCacheCountsTest() throws Exception {

        List<TagDTO> tagCloud = postService.getTagCloud(2);
        assertEquals(tagCloud.size(), 2);

        tagCloud = postService.getTagCloud(-1);
        assertThat(tagCloud.size(), greaterThan(2));
    }

    @Test
    public void tagsList_page_loads() throws Exception {
        RequestBuilder request = get("/admin/posts/tags").with(csrf());
        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(view().name(ADMIN_TAGS_VIEW)).andReturn();

        assertThat(result.getModelAndView().getModel().get("tags"),
                is(instanceOf(ArrayList.class)));
    }

    @Test
    public void addTag_increases_tag_count() throws Exception {
        int preTagCount = postService.getTagDTOs().size();
        mvc.perform(addTagRequest("tagIncreasesTagCount"))
                .andExpect(redirectedUrl("/admin/posts/tags"));

        int postTagCount = postService.getTagDTOs().size();
        assertThat(postTagCount, is(greaterThan(preTagCount)));
    }

    @Test
    public void updateTag_changes_tag_value() throws Exception {
        Tag preTag = postService.getTag("h2tagfour");

        mvc.perform(updateTagRequest(preTag.getTagId(), "updateChangesTagName"))
                .andExpect(redirectedUrl("/admin/posts/tags"));

        Tag postTag = postService.getTag("updateChangesTagName");
        assertEquals(preTag.getTagId(), postTag.getTagId());
    }

    @Test
    public void deleteTag_decreases_tag_count() throws Exception {
        int preTagCount = postService.getTagDTOs().size();
        Tag preTag = postService.getTag("h2tagsix");

        mvc.perform(deleteTagRequest(preTag.getTagId()))
                .andExpect(redirectedUrl("/admin/posts/tags"));

        int postTagCount = postService.getTagDTOs().size();
        assertThat(postTagCount, is(lessThan(preTagCount)));
    }

    // endregion

    // region Utility Methods

    private RequestBuilder addPostRequest(String s) {
        return addPostRequest(s, true, false, TwitterCardType.SUMMARY);
    }

    private RequestBuilder solrCategoryRequest(String s) {
        return addPostRequest(s, true, true, TwitterCardType.SUMMARY);
    }

    private RequestBuilder addUnpublishedPostRequest(String s) {
        return addPostRequest(s, false, false, TwitterCardType.SUMMARY);
    }

    private RequestBuilder addTwitterCardPostRequest(String s, TwitterCardType twitterCardType, PostDisplayType postDisplayType) {
        return post("/admin/posts/add/post")
                .param("post", POST_PUBLISH )
                .param("postTitle", "my title " + s)
                .param("hasPost", "true")
                .param("postLink", StringUtils.EMPTY)
                .param("postType", POST_CONSTANT)
                .param("twitterCardType", twitterCardType.name())
                .param("displayType", postDisplayType.name())
                .param("postContent", "My Post Content must be longer so I don't trigger a new contraint addition!")
                .param("isPublished", "true")
                .param("tags", String.format("req%s, req%s%s", s, s, 1))
                .param("categoryId", "1")
                .with(csrf());
    }

    private RequestBuilder addPostRequest(String s, Boolean isPublished, Boolean addSolrCategory, TwitterCardType twitterCardType) {
        return post("/admin/posts/add/post")
                .param("post", isPublished ? POST_PUBLISH : POST_DRAFT)
                .param("postTitle", "my title " + s)
                .param("hasPost", "true")
                .param("postLink", StringUtils.EMPTY)
                .param("postType", POST_CONSTANT)
                .param("twitterCardType", twitterCardType.name())
                .param("displayType", POST_CONSTANT)
                .param("postContent", "My Post Content must be longer so I don't trigger a new contraint addition!")
                .param("isPublished", isPublished.toString())
                .param("tags", String.format("req%s, req%s%s", s, s, 1))
                .param("categoryId", addSolrCategory ? "3" : "1")
                .with(csrf());
    }

    private RequestBuilder addLinkRequest(String s) {
        return post("/admin/posts/add/link")
                .param("post", POST_PUBLISH )
                .param("postTitle", "my title " + s)
                .param("hasPost", "true")
                .param("postLink", "http://some.link/some/path")
                .param("postDescription", "my description")
                .param("postType", LINK_CONSTANT)
                .param("displayType", LINK_CONSTANT)
                .param("twitterCardType", TWITTER_SUMMARY)
                .param("postContent", "My Post Content must be longer so I don't trigger a new contraint addition!")
                .param("isPublished", "true")
                .param("tags", String.format("req%s, req%s%s", s, s, 1))
                .with(csrf());
    }

    private RequestBuilder addTagRequest(String s) {
        return post("/admin/posts/tags/new")
                .param("tagValue", s)
                .with(csrf());
    }

    private RequestBuilder addCategoryRequest(String s) {
        return post("/admin/posts/categories/new")
                .param("categoryValue", s)
                .param("isActive", "1")
                .param("isDefault", "0")
                .with(csrf());
    }

    private RequestBuilder updateTagRequest(long tagId, String s) {
        return post("/admin/posts/tags/update")
                .param("tagValue", s)
                .param("tagId", String.valueOf(tagId))
                .with(csrf());
    }

    private RequestBuilder updateCategoryRequest(long categoryId, String s, Boolean isActive, Boolean isDefault) {
        return post("/admin/posts/categories/update")
                .param("categoryValue", s)
                .param("categoryId", String.valueOf(categoryId))
                .param("isActive", String.valueOf(isActive))
                .param("isActive", String.valueOf(categoryId))
                .with(csrf());
    }

    private RequestBuilder deleteTagRequest(long tagId) {
        return post("/admin/posts/tags/update")
                .param("deleteTag", "true")
                .param("tagId", String.valueOf(tagId))
                .with(csrf());
    }
    // endregion

}
