package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.utils.MultipartPostImage;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.substring;

@Getter
@Setter
public class PostDTO implements Serializable {

    private static final long serialVersionUID = 3533657789336113957L;

    public static final String ALPHACODE_09 = "09";

    @NotEmpty
    private Set<TagDTO> tags = new HashSet<TagDTO>();

    @NotEmpty
    @Length(max = Post.MAX_POST_TITLE_LENGTH)
    private String postTitle;

    @NotEmpty
    @Length(min = Post.MIN_POST_CONTENT_LENGTH)
    private String postContent;

    @NotNull
    private PostDisplayType displayType;

    @NotNull
    private TwitterCardType twitterCardType;

    // region Private Properties

    private Long postId;

    private Long userId;

    private String postName;

    private String postLink;

    private String postImage;

    private ZonedDateTime postDate;

    private ZonedDateTime postModified;

    private PostType postType;

    private Boolean isPublished = true;

    private String postSource = "NA";

    private int clickCount = 0;

    private int likesCount = 0;

    private int valueRating = 0;

    private int version = 0;

    private Boolean hasImages = false;

    private int imageIndex = 1;

    private String alphaKey;

    private Long temporaryPostId = 1L;

    private Long categoryId = 1L;

    private List<MultipartPostImage> postImages;

    private User author;

    // endregion

    // region methods

    public String authorFullname() {
        return this.author.getFirstName() + " " + this.author.getLastName();
    }

    public boolean isLink() {
        return this.getPostType().equals(PostType.LINK);
    }

    public boolean isNew() {
        return this.postId == null;
    }

    // endregion

    // region ToString()

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", postTitle='" + postTitle + '\'' +
                ", postName='" + postName + '\'' +
                ", postLink='" + postLink + '\'' +
                ", postDate=" + postDate +
                ", postModified=" + postModified +
                ", postType='" + postType + '\'' +
                ", displayType='" + displayType + '\'' +
                ", isPublished=" + isPublished +
                ", postContent='" + postContent + '\'' +
                ", postSource='" + postSource + '\'' +
                ", clickCount=" + clickCount +
                ", likesCount=" + likesCount +
                ", valueRating=" + valueRating +
                ", categoryId =" + categoryId +
                ", version=" + version +
                '}';
    }

    // endregion

    // region Builders

    public static Builder getBuilder(Long userId, String postTitle, String postName, String postLink, String postContent, PostType postType, PostDisplayType displayType, Long categoryId, TwitterCardType twitterCardType) {
        return new PostDTO.Builder(userId, postTitle, postName, postLink, postContent, postType, displayType, categoryId, twitterCardType);
    }

    public static Builder getUpdateFields(Long postId,
                                          String postTitle,
                                          String postContent,
                                          Boolean isPublished,
                                          PostDisplayType displayType,
                                          Long categoryId,
                                          TwitterCardType twitterCardType) {
        return new PostDTO.Builder(postId, postTitle, postContent, isPublished, displayType, categoryId, twitterCardType);
    }


    public static PostDTO buildAlphaTitles(Post post) {
        return populateAlphas(post, true);

    }

    public static PostDTO buildAlphaNumericTitles(Post post) {
        return populateAlphas(post, false);
    }

    private static PostDTO populateAlphas(Post post, Boolean isAlphabetic) {
        PostDTO built = new PostDTO();
        String postTitle = post.getPostTitle();
        String alphaKey = StringUtils.upperCase(substring(postTitle, 0, 1));
        if (!isAlphabetic) {
            alphaKey = ALPHACODE_09;
        }

        built.postTitle = postTitle;
        built.postName = post.getPostName();
        built.alphaKey = alphaKey;
        return built;
    }

    public static class Builder {

        private PostDTO built;

        public Builder(Long userId, String postTitle, String postName, String postLink, String postContent, PostType postType, PostDisplayType displayType, Long categoryId, TwitterCardType twitterCardType) {
            built = new PostDTO();
            built.userId = userId;
            built.postTitle = postTitle;
            built.postName = postName;
            built.postLink = postLink;
            built.postContent = postContent;
            built.postType = postType;
            built.displayType = displayType;
            built.categoryId = categoryId;
            built.twitterCardType = twitterCardType;
            built.postSource = PostUtils.createPostSource(postLink);
        }

        public Builder(Long postId,
                       String postTitle,
                       String postContent,
                       Boolean isPublished,
                       PostDisplayType displayType,
                       Long categoryId, TwitterCardType twitterCardType) {
            built = new PostDTO();
            built.postId = postId;
            built.postTitle = postTitle;
            built.postContent = postContent;
            built.isPublished = isPublished;
            built.displayType = displayType;
            built.categoryId = categoryId;
            built.twitterCardType = twitterCardType;
        }

        public Builder postImage(String postImage) {
            built.postImage = postImage;
            return this;
        }

        public Builder hasImages(Boolean hasImages) {
            built.hasImages = hasImages;
            return this;
        }

        public Builder isPublished(Boolean isPublished) {
            built.setIsPublished(isPublished);
            return this;
        }

        public Builder postSource(String postSource) {
            built.postSource = postSource;
            return this;
        }

        public Builder postId(Long postId) {
            built.postId = postId;
            return this;
        }

        public Builder tags(Set<TagDTO> tagDTOs) {
            built.tags = tagDTOs;
            return this;
        }

        public PostDTO build() {
            return built;
        }
    }

    // endregion

}
