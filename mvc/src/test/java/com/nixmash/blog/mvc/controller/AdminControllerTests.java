package com.nixmash.blog.mvc.controller;

import com.nixmash.blog.jpa.common.ISiteOption;
import com.nixmash.blog.jpa.common.SiteOptions;
import com.nixmash.blog.jpa.dto.SiteOptionMapDTO;
import com.nixmash.blog.jpa.enums.UserRegistration;
import com.nixmash.blog.jpa.model.validators.UserCreateFormValidator;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.service.interfaces.SiteService;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.mvc.AbstractContext;
import com.nixmash.blog.mvc.annotations.WithAdminUser;
import com.nixmash.blog.mvc.annotations.WithPostUser;
import com.nixmash.blog.mvc.components.WebUI;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;

import static com.nixmash.blog.jpa.model.SiteOptionTests.*;
import static com.nixmash.blog.mvc.controller.AdminController.*;
import static com.nixmash.blog.mvc.security.SecurityRequestPostProcessors.csrf;
import static com.nixmash.blog.mvc.security.SecurityTests.loginPage;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
public class AdminControllerTests extends AbstractContext {

    private AdminController adminController;
    private UserService mockUserService;

    private static final String NEW_SITE_NAME = "New Site Name";
    private static final Integer NEW_INTEGER_PROPERTY = 8;
    private static final UserRegistration NEW_USER_REGISTRATION = UserRegistration.CLOSED;

    @Autowired
    private WebUI webUI;

    @Autowired
    private UserService userService;

    @Autowired
    private SiteOptions siteOptions;

    @Autowired
    private SiteService siteService;

    @Autowired
    private UserCreateFormValidator userCreateFormValidator;

    @Autowired
    private PostService postService;

    private MockMvc mvc;
    private SiteOptionMapDTO siteOptionMapDTO;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() throws ServletException {


        mvc = webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        siteOptionMapDTO = SiteOptionMapDTO.withGeneralSettings(
                siteOptions.getSiteName(),
                siteOptions.getSiteDescription(),
                siteOptions.getAddGoogleAnalytics(),
                siteOptions.getGoogleAnalyticsTrackingId(),
                siteOptions.getUserRegistration())
                .build();

        mockUserService = mock(UserService.class);
        adminController = new AdminController(userService, webUI, siteOptions, siteService, userCreateFormValidator, postService);
    }

    @After
    public void tearDown() {
        siteOptions.setSiteName(DEFAULT_SITE_NAME);
        siteOptions.setSiteDescription(DEFAULT_SITE_DESCRIPTION);
        siteOptions.setAddGoogleAnalytics(false);
        siteOptions.setGoogleAnalyticsTrackingId(DEFAULT_TRACKING_ID);
        siteOptions.setUserRegistration(DEFAULT_USER_REGISTRATION);
    }


    @Test
    @WithAdminUser
    public void adminUserCanAccessAdmin() throws Exception {
        RequestBuilder request = get("/admin").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_HOME_VIEW));
    }


    @Test
    @WithPostUser
    public void postUserCanAccessAdmin() throws Exception {
        RequestBuilder request = get("/admin").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_HOME_VIEW));
    }


    @Test
    @WithUserDetails(value = "erwin",
            userDetailsServiceBeanName = "currentUserDetailsService")
    public void registeredUserCannotAccessAdmin() throws Exception {

        // Erwin a registered user but not in ROLE_POSTS
        RequestBuilder request = get("/admin").with(csrf());
        mvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/403"));
    }

    @Test
    @WithAnonymousUser
    public void anonymousCannotAccessAdmin() throws Exception {

        // Whereas Erwin is forbidden, anonymous users redirected to login page
        RequestBuilder request = get("/admin").with(csrf());
        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(loginPage());
    }

    @Test
    @WithAdminUser
    public void adminUserCanAccessAdminUsersList() throws Exception {
        RequestBuilder request = get("/admin/users").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_USERS_VIEW));
    }

    @Test
    @WithAdminUser
    public void retrieveSiteOptionsForSiteGeneralSettingsPage() throws Exception {
        RequestBuilder request = get("/admin/site/settings").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("siteOptionMapDTO"))
                .andExpect(view().name(AdminController.ADMIN_SITESETTINGS_VIEW));
    }

    @Test
    @WithAdminUser
    public void siteSettingsWithEmptySiteName_ErrorResult() throws Exception {

        RequestBuilder request = post("/admin/site/settings")
                .param(ISiteOption.SITE_NAME, StringUtils.EMPTY)
                .param(ISiteOption.SITE_DESCRIPTION, siteOptionMapDTO.getSiteDescription())
                .param(ISiteOption.ADD_GOOGLE_ANALYTICS, String.valueOf(siteOptionMapDTO.getAddGoogleAnalytics()))
                .param(ISiteOption.GOOGLE_ANALYTICS_TRACKING_ID, siteOptionMapDTO.getGoogleAnalyticsTrackingId()).with(csrf());

        mvc.perform(request)
                .andExpect(model().attributeHasFieldErrors("siteOptionMapDTO", "siteName"))
                .andExpect(view().name(AdminController.ADMIN_SITESETTINGS_VIEW));
    }

    @Test
    public void updateGeneralSiteSettingsMethodTest() throws Exception {
        siteOptionMapDTO.setSiteName(NEW_SITE_NAME);
        siteOptionMapDTO.setUserRegistration(NEW_USER_REGISTRATION);

        adminController.updateGeneralSiteSettings(siteOptionMapDTO);

        assertEquals(siteOptions.getSiteName(), NEW_SITE_NAME);

    }

    @Test
    @WithAdminUser
    public void siteSettingsUpdated_UpdatesSiteOptions() throws Exception {

        RequestBuilder request = post("/admin/site/settings")
                .param(ISiteOption.SITE_NAME, siteOptionMapDTO.getSiteName())
                .param(ISiteOption.SITE_DESCRIPTION, siteOptionMapDTO.getSiteDescription())
                .param(ISiteOption.ADD_GOOGLE_ANALYTICS, String.valueOf(siteOptionMapDTO.getAddGoogleAnalytics()))
                .param(ISiteOption.GOOGLE_ANALYTICS_TRACKING_ID, siteOptionMapDTO.getGoogleAnalyticsTrackingId())
                .param(ISiteOption.USER_REGISTRATION, String.valueOf(siteOptionMapDTO.getUserRegistration()))
                .with(csrf());

        mvc.perform(request)
                .andExpect(model().attributeHasNoErrors())
                .andExpect(MockMvcResultMatchers.flash().attributeExists("feedbackMessage"))
                .andExpect(redirectedUrl("/admin/site/settings"));
    }

    @Test
    @WithAdminUser
    public void getSetUserPasswordPage() throws Exception {
        RequestBuilder request = get("/admin/users/password/3").with(csrf());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPasswordDTO"))
                .andExpect(model().attributeExists("userDescription"))
                .andExpect(view().name(ADMIN_USERPASSWORD_VIEW));
    }

    @Test
    @WithAdminUser
    public void resetPasswordMatchingPasswords_RedirectsToUserList() throws Exception {
        RequestBuilder request = post("/admin/users/password")
                .param("userId", "4").param("password", "password").param("repeatedPassword", "password").with(csrf());

        mvc.perform(request)
                .andExpect(redirectedUrl("/admin/users"));

    }

    @Test
    @WithAdminUser
    public void resetPassword3CharPasswords_ReturnsToView() throws Exception {
        RequestBuilder request = post("/admin/users/password")
                .param("userId", "4").param("password", "one").param("repeatedPassword", "one").with(csrf());

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPasswordDTO"))
                .andExpect(model().attributeExists("userDescription"))
                .andExpect(view().name(ADMIN_USERPASSWORD_VIEW));

    }


    @Test
    @WithAdminUser
    public void resetNonMatchingPasswords_ReturnsToView() throws Exception {
        RequestBuilder request = post("/admin/users/password")
                .param("userId", "4").param("password", "firstpassword").param("repeatedPassword", "secondpassword").with(csrf());

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPasswordDTO"))
                .andExpect(model().attributeExists("userDescription"))
                .andExpect(view().name(ADMIN_USERPASSWORD_VIEW));

    }
}
