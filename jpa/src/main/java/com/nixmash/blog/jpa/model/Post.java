package com.nixmash.blog.jpa.model;

import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@Access(value = FIELD)
@NamedQueries({
        @NamedQuery(name = "Post.getByPostIds",
                query = "SELECT p FROM Post p " +
                        "WHERE p.postId IN :postIds ORDER BY p.postDate DESC"),
})
public class Post implements Serializable {

    private static final long serialVersionUID = 3533657789336113957L;

    public static final int MAX_POST_TITLE_LENGTH = 200;
    public static final int MAX_POST_NAME_LENGTH = 200;
    public static final int MIN_POST_CONTENT_LENGTH = 20;

    // region properties

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_title", nullable = false, length = MAX_POST_TITLE_LENGTH)
    private String postTitle;

    @Column(name = "post_name", nullable = false, length = MAX_POST_NAME_LENGTH)
    private String postName;

    @Column(name = "post_link")
    private String postLink = "NA";

    @Column(name = "post_image", length = MAX_POST_NAME_LENGTH)
    private String postImage;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @CreatedDate
    @Column(name = "post_date", nullable = false)
    private ZonedDateTime postDate;


    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @LastModifiedDate
    @Column(name = "post_modified", nullable = false)
    private ZonedDateTime postModified;

    @Column(name = "post_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(name = "display_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostDisplayType displayType;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;

    @Column(name = "post_source", length = 50)
    private String postSource = "NA";

    @Column(name = "click_count", nullable = false)
    private int clickCount = 0;

    @Column(name = "likes_count", nullable = false)
    private int likesCount = 0;

    @Column(name = "value_rating", nullable = false)
    private int valueRating = 0;

    @Version
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    private int version = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_tag_ids",
            joinColumns = @JoinColumn(name = "post_id",
                    referencedColumnName = "post_id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id",
                    referencedColumnName = "tag_id",
                    nullable = false))
    public Set<Tag> tags;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "post_category_ids",
            joinColumns = @JoinColumn(name = "post_id",
                    referencedColumnName = "post_id",
                    nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id",
                    referencedColumnName = "category_id",
                    nullable = false))
    public Category category;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id",
            referencedColumnName = "post_id", insertable = false, updatable = false)
    public PostMeta postMeta;

    @Transient
    public List<PostImage> postImages;

    @Transient
    public PostImage singleImage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    public User author;

    // region Transient properties

    @Transient
    public boolean isOwner = false;

    // endregion

    public Post(){

    }

    // region method properties

    public boolean isNew() {
        return (this.postId == null);
    }

    public boolean isMultiPhotoPost() {
        return (this.displayType == PostDisplayType.MULTIPHOTO_POST);
    }

    public boolean isSinglePhotoPost() {
        return (this.displayType == PostDisplayType.SINGLEPHOTO_POST);
    }

    public String getAuthorFullname() {
        return author.getFirstName() + " " + author.getLastName();
    }


    // endregion

    public void update(String postTitle, String postContent, Boolean isPublished, PostDisplayType displayType) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.isPublished = isPublished;
        this.displayType = displayType;
    }

    public void updateLikes(int likeIncrement) {
        this.likesCount = this.likesCount + likeIncrement;
    }

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
                ", version=" + version +
                '}';
    }

    public static Builder getBuilder(Long userId, String postTitle, String postName, String postLink, String postContent, PostType postType, PostDisplayType displayType) {
        return new Post.Builder(userId, postTitle, postName, postLink, postContent, postType, displayType);
    }

    public static class Builder {

        private Post built;

        public Builder(Long userId, String postTitle, String postName, String postLink, String postContent, PostType postType, PostDisplayType displayType) {
            built = new Post();
            built.userId = userId;
            built.postTitle = postTitle;
            built.postName = postName;
            built.postLink = postLink;
            built.postContent = postContent;
            built.postType = postType;
            built.displayType = displayType;
            built.postSource = PostUtils.createPostSource(postLink);
        }

        public Builder isPublished(Boolean isPublished) {
            built.setIsPublished(isPublished);
            return this;
        }

        public Builder postSource(String postSource) {
            built.postSource = postSource;
            return this;
        }

        public Builder postId(long postId) {
            built.postId = postId;
            return this;
        }

        public Builder postImage(String postImage) {
            if (StringUtils.isEmpty(postImage))
                postImage = null;
            built.postImage = postImage;
            return this;
        }

        public Builder tags(Set<Tag> tags) {
            built.tags = tags;
            return this;
        }

        public Builder category(Category category) {
            built.category = category;
            return this;
        }

        public Post build() {
            return built;
        }
    }
}
