package com.nixmash.blog.jpa.model;


import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "post_images")
@JsonIgnoreProperties({"id", "postId", "thumbnailFilename", "newFilename", "contentType", "dateCreated", "lastUpdated"})
public class PostImage implements Serializable {

    private static final long serialVersionUID = 5143474574941155184L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "image_name")
    private String name;

    @Column(name = "thumbnail_filename")
    private String thumbnailFilename;

    @Column(name = "filename")
    private String newFilename;

    @Column(name = "content_type")
    private String contentType;
    private Long size;

    @Column(name = "thumbnail_size")
    private Long thumbnailSize;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datetime_created")
    private Date dateCreated;

    @Transient
    private String url;
    @Transient
    private String thumbnailUrl;
    @Transient
    private String deleteUrl;
    @Transient
    private String deleteType;

    public PostImage() {
    }


    @Override
    public String toString() {
        return "Image{" + "name=" + name + ", thumbnailFilename=" + thumbnailFilename + ", newFilename=" + newFilename + ", contentType=" + contentType + ", url=" + url + ", thumbnailUrl=" + thumbnailUrl + ", deleteUrl=" + deleteUrl + ", deleteType=" + deleteType + '}';
    }

    public static Builder getBuilder(Long postId, String name, String thumbnailFilename, String newFilename, String contentType, Long size, Long thumbnailSize, Date dateCreated, String url, String thumbnailUrl, String deleteUrl, String deleteType) {
        return new PostImage.Builder(postId, name, thumbnailFilename, newFilename, contentType, size, thumbnailSize, dateCreated, url, thumbnailUrl, deleteUrl, deleteType);
    }

    public static class Builder {
        private PostImage built;

        public Builder(Long postId, String name, String thumbnailFilename, String newFilename, String contentType, Long size, Long thumbnailSize, Date dateCreated, String url, String thumbnailUrl, String deleteUrl, String deleteType) {
            built.postId = postId;
            built.name = name;
            built.thumbnailFilename = thumbnailFilename;
            built.newFilename = newFilename;
            built.contentType = contentType;
            built.size = size;
            built.thumbnailSize = thumbnailSize;
            built.dateCreated = dateCreated;
            built.url = url;
            built.thumbnailUrl = thumbnailUrl;
            built.deleteUrl = deleteUrl;
            built.deleteType = deleteType;
        }

        public PostImage build() {
            return built;
        }
    }
}
