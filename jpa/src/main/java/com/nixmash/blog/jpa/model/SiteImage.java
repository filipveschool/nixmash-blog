package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Getter
@Setter
@Entity
@Table(name = "site_images")
public class SiteImage {

    @Id
    @Column(name = "site_image_id")
    private Long siteImageId;

    @Basic
    @Column(name = "image_filename")
    private String imageFilename;

    @Basic
    @Column(name = "image_description")
    private String imageDescription;

    @Basic
    @Column(name = "image_author")
    private String imageAuthor;

    @Basic
    @Column(name = "source_url")
    private String sourceUrl;

    @Basic
    @Column(name = "common_license")
    private Boolean commonLicense;

    @Basic
    @Column(name = "banner_image")
    private Boolean bannerImage;

    @Basic
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Basic
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent;

    @Basic
    @Column(name = "day_of_year")
    private Integer dayOfYear;

    @Transient
    private String imageMessage;

    @Transient
    public Boolean isOwned() {
        return getImageAuthor().equals("SITE");
    }

    public SiteImage(){

    }

    @Override
    public String toString() {
        return "SiteImage{" +
                "siteImageId=" + siteImageId +
                ", imageFilename='" + imageFilename + '\'' +
                ", imageDescription='" + imageDescription + '\'' +
                ", imageAuthor='" + imageAuthor + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", commonLicense=" + commonLicense +
                ", bannerImage=" + bannerImage +
                ", isActive=" + isActive +
                ", isCurrent=" + isCurrent +
                ", dayOfYear=" + dayOfYear +
                ", imageMessage='" + imageMessage + '\'' +
                '}';
    }
}
