package com.nixmash.blog.mvc.controller;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.exceptions.PostCategoryNotSupportedException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.model.CurrentUser;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostImage;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.service.interfaces.PermaPostService;
import com.nixmash.blog.jpa.service.interfaces.PostImageService;
import com.nixmash.blog.jpa.service.interfaces.PostMetaService;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.utils.PostUtils;
import com.nixmash.blog.mail.service.interfaces.FmService;
import com.nixmash.blog.mvc.components.WebUI;
import com.nixmash.blog.solr.service.PostDocService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by daveburke on 2/22/17.
 */
@Controller
public class PermaPostsController {

    private static final Logger logger = LoggerFactory.getLogger(PermaPostsController.class);

    public static final String POSTS_PERMALINK_VIEW = "posts/post";
    private static final String MORELIKETHIS_HEADING = "post.morelikethis.heading";

    private final PostService postService;

    @Autowired
    private PermaPostService permaPostService;

    @Autowired
    private PostMetaService postMetaService;

    @Autowired
    private PostImageService postImageService;


    private final PostDocService postDocService;
    private final FmService fmService;
    private final ApplicationSettings applicationSettings;
    private final WebUI webUI;

    public PermaPostsController(PostService postService, PostDocService postDocService, FmService fmService, ApplicationSettings applicationSettings, WebUI webUI) {
        this.postService = postService;
        this.postDocService = postDocService;
        this.fmService = fmService;
        this.applicationSettings = applicationSettings;
        this.webUI = webUI;
    }

    // region Handling NixMash Post {category}/{postname} Permalink  to /post/{postname}

    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    @GetMapping(value = "/{category:java|linux|postgresql|mysql|wordpress|android|codejohnny}/{postName}")
    public String categoryPost() {
        return "redirect:/post/{postName}";
    }

    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    @GetMapping(value = "/{category:ruby-on-rails|nixmashup|php|best-of-everyman-links|drupal}/{postName}")
    public String oldCategoryPost(HttpServletRequest request,
                                  @PathVariable("category") String category,
                                  @PathVariable("postName") String postName) {
        request.setAttribute("category", category);
        request.setAttribute("postName", postName);
        throw new PostCategoryNotSupportedException();
    }

    // endregion


    @GetMapping(value = "/post/{postName}")
    public String post(@PathVariable("postName") String postName, @RequestParam(value = "preview", required = false, defaultValue = "false") Boolean inPreviewMode, Model model, CurrentUser currentUser)
            throws PostNotFoundException {

        Post post = permaPostService.getPost(postName);
        Date postCreated = Date.from(post.getPostDate().toInstant());
        post.setOwner(PostUtils.isPostOwner(currentUser, post.getUserId()));
        post.setPostContent(PostUtils.formatPostContent(post));

        if (!inPreviewMode) {

            if (applicationSettings.getMoreLikeThisDisplay()) {

                // Solr must be active and number of postDocs retrieved must not be null
                if (postDocService.getMoreLikeThis(post.getPostId()) != null) {
                    model.addAttribute("moreLikeThisDisplay", true);
                    model.addAttribute("postId", post.getPostId());
                    model.addAttribute("moreLikeThisHeading",
                            webUI.getMessage(MORELIKETHIS_HEADING));
                }
            }

            PostMeta postMeta = postMetaService.buildTwitterMetaTagsForDisplay(post);
            if (postMeta != null) {
                model.addAttribute("twitterMetatags", fmService.getTwitterTemplate(postMeta));
            }
        }

        model.addAttribute("post", post);
        model.addAttribute("postCreated", postCreated);
        model.addAttribute("shareSiteName",
                StringUtils.deleteWhitespace(applicationSettings.getSiteName()));
        model.addAttribute("shareUrl",
                String.format("%s/post/%s", applicationSettings.getBaseUrl(), post.getPostName()));
        return POSTS_PERMALINK_VIEW;
    }


}
