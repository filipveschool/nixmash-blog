package com.nixmash.blog.jpa.model;

import com.nixmash.blog.jpa.enums.TwitterCardType;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "post_meta")
public class PostMeta implements Serializable {

    private static final long serialVersionUID = 7743331633690910405L;

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Basic
    @Column(name = "twitter_creator")
    private String twitterCreator;

    @Basic
    @Column(name = "twitter_image")
    private String twitterImage;

    @Basic
    @Column(name = "twitter_description")
    private String twitterDescription;

    @Basic
    @Column(name = "twitter_card")
    @Enumerated(EnumType.STRING)
    private TwitterCardType twitterCardType;

    @Transient
    private String twitterTitle;

    @Transient
    private String twitterUrl;

    @Transient
    private String twitterSite;

    public PostMeta(){

    }

    public void update(String twitterImage, String twitterCreator, String twitterDescription, TwitterCardType twitterCardType) {
        this.twitterImage = twitterImage;
        this.twitterCreator = twitterCreator;
        this.twitterDescription = twitterDescription;
        this.twitterCardType = twitterCardType;
    }

    public static Builder getBuilder(TwitterCardType twitterCardType, String twitterTitle, String twitterSite, String twitterCreator) {
        return new PostMeta.Builder(twitterCardType, twitterTitle, twitterSite, twitterCreator);
    }

    public static Builder getUpdated(TwitterCardType twitterCardType, String twitterImage, String twitterDescription) {
        return new PostMeta.Builder(twitterCardType, twitterImage, twitterDescription);
    }

    public static Builder getEmpty(Long postId, TwitterCardType twitterCardType) {
        return new PostMeta.Builder(postId, twitterCardType);
    }

    public static class Builder {

        private PostMeta built;

        public Builder(TwitterCardType twitterCardType, String twitterTitle, String twitterSite, String twitterCreator) {
            built = new PostMeta();
            built.twitterCardType = twitterCardType;
            built.twitterTitle = twitterTitle;
            built.twitterSite = twitterSite;
            built.twitterCreator = twitterCreator;
        }

        public Builder(TwitterCardType twitterCardType, String twitterImage, String twitterDescription) {
            built = new PostMeta();
            built.twitterCardType = twitterCardType;
            built.twitterImage = twitterImage;
            built.twitterDescription = twitterDescription;
        }

        public Builder(Long postId, TwitterCardType twitterCardType) {
            built = new PostMeta();
            built.postId = postId;
            built.twitterCardType = twitterCardType;
        }

        public Builder twitterCreator(String creator)
        {
            built.twitterCreator= creator;
            return this;
        }

        public Builder postId(Long postId)
        {
            built.postId= postId;
            return this;
        }

        public Builder twitterDescription(String description)
        {
            built.twitterDescription = description;
            return this;
        }


        public Builder twitterUrl(String postUrl)
        {
            built.twitterUrl = postUrl;
            return this;
        }

        public Builder twitterImage(String twitterImage) {
            built.twitterImage = twitterImage;
            return this;
        }

        public PostMeta build() {
            return built;
        }

    }
}
