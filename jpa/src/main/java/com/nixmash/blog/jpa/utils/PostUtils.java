package com.nixmash.blog.jpa.utils;

import com.github.slugify.Slugify;
import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.CurrentUser;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class PostUtils {

    private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

    public static Post postDtoToPost(PostDTO dto) {

        Post post = new Post();
        post.setUserId(dto.getUserId());
        post.setPostTitle(dto.getPostTitle());
        post.setPostName(dto.getPostName());
        post.setPostLink(dto.getPostLink());
        post.setPostContent(dto.getPostContent());
        post.setPostType(dto.getPostType());
        post.setDisplayType(dto.getDisplayType());
        post.setIsPublished(dto.getIsPublished());
        post.setPostSource(dto.getPostSource());
        post.setPostImage(dto.getPostImage());
        return post;
    }

//    public static Post postDtoToSolrPost(PostDTO dto) {
//        Post post = postDtoToPost(dto);
//        post.setTags(tagsDTOsToTags(dto.getTags()));
//        return post;
//    }


    public static PostDTO postToPostDTO(Post post) {

        PostDTO postDTO = new PostDTO();
        postDTO.setUserId(post.getUserId());
        postDTO.setPostTitle(post.getPostTitle());
        postDTO.setPostName(post.getPostName());
        postDTO.setPostLink(post.getPostLink());
        postDTO.setPostContent(post.getPostContent());
        postDTO.setPostType(post.getPostType());
        postDTO.setDisplayType(post.getDisplayType());
        postDTO.setCategoryId(post.getCategory().getCategoryId());
        postDTO.setTwitterCardType(post.getPostMeta().getTwitterCardType());
        postDTO.setIsPublished(post.getIsPublished());
        postDTO.setPostSource(post.getPostSource());
        postDTO.setPostId(post.getPostId());
        postDTO.setTags(tagsToTagDTOs(post.getTags()));
        postDTO.setPostImage(post.getPostImage());
        return postDTO;
    }

    public static String createPostSource(String url) {
        String domain = null;
        if (StringUtils.isEmpty(url) || url.equals("NA"))
            return null;
        else {
            try {
                URL linkURL = new URL(url);
                domain = linkURL.getHost();
            } catch (MalformedURLException e) {
                log.error("Malformed Url: " + e.getMessage());
            }
        }
        return domain;
    }

    public static String createSlug(String title) {
        Slugify slugify;
        String slug = null;
        try {
            slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            log.error(String.format("IOException for title: %s -- Exception: %s", title, e.getMessage()));
        }
        return slug;
    }

    public static String removeTags(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }

    public static Boolean isPostOwner(CurrentUser currentUser, Long userId) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getId().equals(userId);
    }

    public static String formatPostContent(Post post) {
        String content = post.getPostContent();
        String imageHtml = "<img alt=\"\" src=\"%s\"  class=\"%s-image\"/>\n";
        String thumbnail = String.format(imageHtml, post.getPostImage(), "thumbnail");
        String feature = String.format(imageHtml, post.getPostImage(), "feature");

        switch (post.getDisplayType()) {
            case LINK_SUMMARY:
                content = StringUtils.prependIfMissing(content, thumbnail);
                break;
            case LINK_FEATURE:
                content = StringUtils.appendIfMissing(content, feature);
                break;
            case LINK:
                break;
        }
        return content;
    }

    public static Set<TagDTO> tagsToTagDTOs(Set<Tag> tags) {
        return tags.stream().map(tag -> new TagDTO(tag.getTagId(), tag.getTagValue())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static CategoryDTO categoryToCategoryDTO(Category category) {
        return new CategoryDTO(category.getCategoryId(), category.getCategoryValue(), category.getCategoryCount(),
                category.getIsActive(), category.getIsDefault());
    }

    public static List<CategoryDTO> categoriesToCategoryDTOs(List<Category> categories) {
        return categories.stream().map(category -> new CategoryDTO(
                category.getCategoryId(), category.getCategoryValue(), category.getCategoryCount(),
                category.getIsActive(), category.getIsDefault())).collect(Collectors.toList());
    }

    public static Set<Tag> tagsDTOsToTags(Set<TagDTO> tagDTOs) {
        return tagDTOs.stream().map(tagDTO -> new Tag(tagDTO.getTagId(), tagDTO.getTagValue())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static List<String> tagsToTagValues(Set<Tag> tags) {
        //List<String> tagValues = tags.stream().map(Tag::getTagValue).collect(Collectors.toList());
        return tags.stream().map(Tag::getTagValue).collect(Collectors.toList());
    }


    // region display content

    public static void printPosts(List<Post> posts) {
        for (Post post:
                posts) {
            System.out.println(post.getPostTitle()
                    + "\n" + post.getPostContent() + " : " + post.getPostType() + "\n------------------------");
        }
    }

    // endregion

}
