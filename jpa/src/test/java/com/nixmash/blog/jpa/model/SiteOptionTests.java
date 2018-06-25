package com.nixmash.blog.jpa.model;

import com.nixmash.blog.jpa.common.ISiteOption;
import com.nixmash.blog.jpa.common.SiteOptions;
import com.nixmash.blog.jpa.config.ApplicationConfig;
import com.nixmash.blog.jpa.dto.SiteOptionDTO;
import com.nixmash.blog.jpa.dto.SiteOptionMapDTO;
import com.nixmash.blog.jpa.enums.DataConfigProfile;
import com.nixmash.blog.jpa.enums.UserRegistration;
import com.nixmash.blog.jpa.exceptions.SiteOptionNotFoundException;
import com.nixmash.blog.jpa.repository.SiteOptionRepository;
import com.nixmash.blog.jpa.service.interfaces.SiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@ActiveProfiles(DataConfigProfile.H2)
public class SiteOptionTests {

    // region Constants

    public static final String DEFAULT_SITE_NAME = "My Site";
    public static final String DEFAULT_SITE_DESCRIPTION = "My Site Description";
    public static final String DEFAULT_TRACKING_ID = "UA-XXXXXX-7";
    public static final Integer DEFAULT_INTEGER_PROPERTY = 1;
    public static final UserRegistration DEFAULT_USER_REGISTRATION=  UserRegistration.ADMINISTRATIVE_APPROVAL;

    private static final String MY_UPDATED_SITE_NAME = "My Updated Site Name";
    private static final Integer UPDATED_INTEGER_PROPERTY = 8;
    public static final UserRegistration UPDATED_USER_REGISTRATION=  UserRegistration.CLOSED;

    // endregion

    // region Beans

    @Autowired
    protected ApplicationContext context;

    @Autowired
    SiteOptionRepository siteOptionRepository;

    @Autowired
    SiteOptions siteOptions;

    @Autowired
    SiteService siteService;

    // endregion

    // region Private Properties

    private SiteOptionMapDTO siteOptionMapDTO;
    // endregion

    @Test
    public void siteOptionsIsPopulatedFromContext() throws Exception {
        assertEquals(siteOptions.getAddGoogleAnalytics(), false);
        assertEquals(siteOptions.getGoogleAnalyticsTrackingId(), "UA-XXXXXX-7");
        assertEquals(siteOptions.getSiteName(), "My Site");
        assertEquals(siteOptions.getSiteDescription(), "My Site Description");
        assertEquals(siteOptions.getUserRegistration(), UserRegistration.ADMINISTRATIVE_APPROVAL);
    }

    @Test
    public void siteOptionsPropertyIsUpdatedAtRuntime() throws SiteOptionNotFoundException {

        assertEquals(siteOptions.getSiteName(), DEFAULT_SITE_NAME);
        assertEquals(siteOptions.getUserRegistration(), DEFAULT_USER_REGISTRATION);

        siteService.update(new SiteOptionDTO(ISiteOption.SITE_NAME, MY_UPDATED_SITE_NAME));
        siteService.update(new SiteOptionDTO(ISiteOption.INTEGER_PROPERTY,
                UPDATED_INTEGER_PROPERTY.toString()));
        siteService.update(new SiteOptionDTO(ISiteOption.USER_REGISTRATION, String.valueOf(UPDATED_USER_REGISTRATION)));

        assertEquals(siteOptions.getSiteName(), MY_UPDATED_SITE_NAME);
        assertEquals(siteOptions.getUserRegistration(), UserRegistration.CLOSED);

        siteService.update(new SiteOptionDTO(ISiteOption.SITE_NAME, DEFAULT_SITE_NAME));
    }

    @Test
    public void siteOptionDtoCreatedFromSiteOptionMapDTO() {
        SiteOptionDTO siteOptionDTO = SiteOptionDTO.with(ISiteOption.SITE_NAME, "My Fabulous Site").build();
        assertEquals(siteOptionDTO.getName(), ISiteOption.SITE_NAME);
        assertEquals(siteOptionDTO.getValue(), "My Fabulous Site");
    }

    @Test
    public void userRegistrationEnumRetrievedFromSiteOptionDTO() {
        SiteOptionDTO siteOptionDTO = SiteOptionDTO.with(ISiteOption.USER_REGISTRATION, UserRegistration.INVITE_ONLY).build();
        assertEquals(siteOptionDTO.getName(), ISiteOption.USER_REGISTRATION);
        assertEquals(siteOptionDTO.getValue(), "INVITE_ONLY");
    }

    @Test
    public void SiteOptionMapDtoValidationTests() {

        SiteOptionMapDTO siteOptionMapDTO = SiteOptionMapDTO.withGeneralSettings(
                null,
                siteOptions.getSiteDescription(),
                siteOptions.getAddGoogleAnalytics(),
                siteOptions.getGoogleAnalyticsTrackingId(),
                siteOptions.getUserRegistration())
                .build();

        Errors errors = new BeanPropertyBindingResult(siteOptionMapDTO, "siteOptionMapDTO");
        ValidationUtils.invokeValidator(new SiteOptionMapDtoValidator(), siteOptionMapDTO, errors);
        assertTrue(errors.hasFieldErrors("siteName"));
        assertEquals("EMPTY", errors.getFieldError("siteName").getCode());

    }

    private static class SiteOptionMapDtoValidator implements Validator {

        public boolean supports(Class clazz) {
            return SiteOptionMapDTO.class.isAssignableFrom(clazz);
        }

        public void validate(Object obj, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "siteName", "EMPTY", "You must enter a site name!");
        }
    }

}
