package com.nixmash.blog.jpa.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Component
// @PropertySource("file:${blog.properties.file.path}${blog.properties.file.basename}.properties")
@PropertySource("classpath:/blog.properties")
@ConfigurationProperties(prefix = "blog")
public class ApplicationSettings implements Serializable {

    private static final long serialVersionUID = 7939595017750704755L;

    private String baseUrl;
    private String siteName;
    private Boolean loginEnabled;
    private Boolean solrEnabled;

    // endregion

    //region Social Properties

    private String twitterAppId;
    private String twitterAppSecret;
    private String facebookAppId;
    private String facebookAppSecret;
    private String googleAppId;
    private String googleAppSecret;
    private String googleMapKey;

    //endregion

    // region User Profile Image Properties

    private String profileImagePath;
    private String profileImageUrlRoot;

    private String profileIconPath;
    private String profileIconUrlRoot;

    // endregion

    //region RSS Properties

    private String rssChannelTitle;
    private String rssChannelDescription;

    //endregion

    // region Posts Stream and Display

    private Boolean titleStreamDisplay;
    private Boolean titleSearchResultsDisplay;

    private String postImagePath;
    private String postDemoImagePath;
    private String postImageUrlRoot;
    private String postDemoImageUrlRoot;
    private String postAtoZFilePath;

    private int sidebarTagCloudCount;
    private int postStreamPageCount;
    private int postTitleStreamPageCount;

    // endregion

    // region PermaPost Display and MetaData

    private Boolean moreLikeThisDisplay;
    private int moreLikeThisNum;
    private String twitterSite;
    private String twitterCreator;
    private String twitterImage;

    // endregion


}
