package com.nixmash.blog.mail.service.implementations;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.mail.components.MailUI;
import com.nixmash.blog.mail.service.interfaces.FmService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
@Service("fmService")
public class FmServiceImpl implements FmService {


    @Autowired
    private ApplicationSettings applicationSettings;

    @Autowired
    private Configuration fm;

    @Autowired
    private Environment environment;

    @Autowired
    private PostService postService;

    @Autowired
    private MailUI mailUI;

    // region Test Template

    @Override
    public String displayTestTemplate(User user) {

        String applicationPropertyUrl = environment.getProperty("spring.application.url");
        String siteName = mailUI.getMessage("mail.site.name");
        String greeting = "YOUSA!";

        Map<String, Object> model = new Hashtable<>();
        model.put("siteName", siteName);
        model.put("greeting", greeting);
        model.put("user", user);
        model.put("applicationPropertyUrl", applicationPropertyUrl);

        String result = null;

        try {
            Template template = fm.getTemplate("tests/test.ftl");
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging test template : " + e.getMessage());
        }
        return result;
    }

    // endregion

    // region PostMeta Tags

    @Override
    public String getTwitterTemplate(PostMeta postMeta) {
        String result = null;
        Map<String, Object> model = new Hashtable<>();

        model.put("postMeta", postMeta);
        String twitterCard = postMeta.getTwitterCardType().name().toLowerCase();
        model.put("twitterCardType", twitterCard);
        try {
            result = FreeMarkerTemplateUtils
                    .processTemplateIntoString(fm.getTemplate("posts/twitter.ftl"), model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging Twitter MetaTag template : " + e.getMessage());
        }
        return result;
    }

    // endregion

    // region Posts

    @Override
    public String getNoLinksMessage() {
        String result = null;
        Map<String, Object> model = new Hashtable<>();

        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("posts/nolinks.ftl"), model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging No Links template : " + e.getMessage());
        }
        return result;
    }


    @Override
    public String getNoResultsMessage(String search) {
        String result = null;
        Map<String, Object> model = new Hashtable<>();

        model.put("search", search);
        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("posts/noresults.ftl"), model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging Quick Search template : " + e.getMessage());
        }
        return result;
    }

    @Override
    public String getNoLikesMessage() {
        String result = null;
        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("posts/nolikes.ftl"), null);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging NoLikes template : " + e.getMessage());
        }
        return result;
    }

    @Override
    public String createRssPostContent(Post post) {
        String html = null;

        Map<String, Object> model = new Hashtable<>();

        model.put("post", post);
        model.put("baseurl", applicationSettings.getBaseUrl());

        try {
            Template template = fm.getTemplate("posts/rss_post.ftl");
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging post template : " + e.getMessage());
        }
        return html;
    }

    @Override
    public String createPostHtml(Post post, String templateName) {
        String html = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String postCreated = post.getPostDate().format(formatter);

        Map<String, Object> model = new Hashtable<>();

        model.put("post", post);
        model.put("postCreated", postCreated);
        model.put("shareSiteName",
                StringUtils.deleteWhitespace(applicationSettings.getSiteName()));
        model.put("shareUrl",
                String.format("%s/post/%s", applicationSettings.getBaseUrl(), post.getPostName()));

        String displayType = templateName == null ? post.getDisplayType().name().toLowerCase() : templateName;
        String ftl = String.format("posts/%s.ftl", displayType);

        try {
            Template template = fm.getTemplate(ftl);
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging post template : " + e.getMessage());
        }
        return html;
    }

    @Override
    public String createPostHtml(Post post) {
        return createPostHtml(post, null);
    }

    @Override
    public String createPostAtoZs() {
        String html = null;

        String backToTop = mailUI.getMessage("posts.az.page.backtotop");
        String azFileName = environment.getProperty("posts.az.file.name");
        String azFilePath = applicationSettings.getPostAtoZFilePath();

        Map<String, Object> model = new Hashtable<>();
        model.put("alphaLinks", postService.getAlphaLInks());
        model.put("alphaPosts", postService.getAlphaPosts());
        model.put("backToTop", backToTop);

        try {
            Template template = fm.getTemplate("posts/az.ftl");
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            InputStream in = IOUtils.toInputStream(html, "UTF-8");
            FileUtils.copyInputStreamToFile(in, new File(azFilePath + azFileName));
        } catch (IOException | TemplateException e) {
            log.error("Problem creating A-to-Z template or HTML file: " + e.getMessage());
        }
        return html;
    }

    @Override
    public String getNoMoreLikeThisMessage() {
        String result = null;
        Map<String, Object> model = new Hashtable<>();

        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("posts/nomlts.ftl"), model);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging No MoreLikeThis template : " + e.getMessage());
        }
        return result;
    }

    // endregion

    // region Utility Templates

    @Override
    public String getRobotsTxt() {
        String result = null;
        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("utils/robots.ftl"), null);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging Robots.txt template : " + e.getMessage());
        }
        return result;
    }

    @Override
    public String getFileUploadingScript() {
        String result = null;
        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("utils/fileuploading.ftl"), null);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging fileuploading template : " + e.getMessage());
        }
        return result;
    }

    @Override
    public String getFileUploadedScript() {
        String result = null;
        try {
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fm.getTemplate("utils/fileuploaded.ftl"), null);
        } catch (IOException | TemplateException e) {
            log.error("Problem merging fileuploaded template : " + e.getMessage());
        }
        return result;
    }

    // endregion

}
