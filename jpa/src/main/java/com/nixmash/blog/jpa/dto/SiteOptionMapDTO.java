package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.enums.UserRegistration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

@Getter
@Setter
public class SiteOptionMapDTO implements Serializable {

    private static final long serialVersionUID = -5465065342755162883L;

    @NotEmpty
    private String siteName;

    @NotEmpty
    private String siteDescription;

    @NotEmpty
    private Boolean addGoogleAnalytics;

    private String googleAnalyticsTrackingId;

    private UserRegistration userRegistration;

    public SiteOptionMapDTO() {
    }

    public static Builder withGeneralSettings(
            String siteName,
            String siteDescription,
            Boolean addGoogleAnalytics,
            String googleAnalyticsTrackingId,
            UserRegistration userRegistration) {

        return new Builder(siteName, siteDescription, addGoogleAnalytics, googleAnalyticsTrackingId, userRegistration);
    }

    public static class Builder {

        private SiteOptionMapDTO built;

        public Builder(String siteName, String siteDescription, Boolean addGoogleAnalytics, String googleAnalyticsTrackingId, UserRegistration userRegistration) {
            built = new SiteOptionMapDTO();
            built.siteName = siteName;
            built.siteDescription = siteDescription;
            built.addGoogleAnalytics = addGoogleAnalytics;
            built.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
            built.userRegistration = userRegistration;
        }

        public SiteOptionMapDTO build() {
            return built;
        }
    }
}
