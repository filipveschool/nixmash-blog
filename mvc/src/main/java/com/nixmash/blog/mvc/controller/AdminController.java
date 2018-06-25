package com.nixmash.blog.mvc.controller;

import com.nixmash.blog.jpa.common.ISiteOption;
import com.nixmash.blog.jpa.common.SiteOptions;
import com.nixmash.blog.jpa.dto.*;
import com.nixmash.blog.jpa.enums.SignInProvider;
import com.nixmash.blog.jpa.exceptions.SiteOptionNotFoundException;
import com.nixmash.blog.jpa.model.Authority;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.model.validators.UserCreateFormValidator;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.service.interfaces.SiteService;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.jpa.utils.UserUtils;
import com.nixmash.blog.mvc.components.WebUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    // region View Constants

    private static final String PARAMETER_USER_ID = "id";

    private static final String ADMIN_MOCKUP_VIEW = "admin/mockup";
    public static final String ADMIN_HOME_VIEW = "admin/dashboard";
    public static final String ADMIN_USERS_VIEW = "admin/security/users";
    private static final String ADMIN_ROLES_VIEW = "admin/security/roles";
    private static final String ADMIN_USERFORM_VIEW = "admin/security/userform";
    public static final String ADMIN_USERPASSWORD_VIEW = "admin/security/password";
    public static final String ADMIN_SITESETTINGS_VIEW = "admin/site/settings";

    // endregion

    // region Feedback Message Constants
    private static final String FEEDBACK_MESSAGE_KEY_USER_UPDATED = "feedback.message.user.updated";
    private static final String FEEDBACK_MESSAGE_KEY_USER_ADDED = "feedback.message.user.added";
    private static final String FEEDBACK_MESSAGE_KEY_ROLE_ADDED = "feedback.message.role.added";
    private static final String FEEDBACK_MESSAGE_KEY_ROLE_UPDATED = "feedback.message.role.updated";
    private static final String FEEDBACK_MESSAGE_KEY_ROLE_ERROR = "feedback.message.role.error";
    private static final String FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED = "feedback.message.role.islocked";
    private static final String FEEDBACK_MESSAGE_KEY_ROLE_DELETED = "feedback.message.role.deleted";
    private static final String FEEDBACK_SITE_SETTINGS_UPDATED = "feedback.message.sitesettings.updated";

    private static final String ADMIN_USER_SETPASSWORD_HEADING_KEY = "admin.users.password.heading";
    private static final String FEEDBACK_USER_PASSWORD_UPDATED_KEY = "feedback.user.password.updated";
    private static final String GLOBAL_ERROR_PASSWORDS_DONOT_MATCH_KEY = "global.error.passwords.donotmatch";


    // endregion

    private final SiteService siteService;
    private final UserService userService;
    private final WebUI webUI;
    private final SiteOptions siteOptions;
    private final UserCreateFormValidator userCreateFormValidator;
    private final PostService postService;

    @Autowired
    public AdminController(UserService userService, WebUI webUI, SiteOptions siteOptions,
                           SiteService siteService, UserCreateFormValidator userCreateFormValidator, PostService postService) {
        this.userService = userService;
        this.webUI = webUI;
        this.siteOptions = siteOptions;
        this.siteService = siteService;
        this.userCreateFormValidator = userCreateFormValidator;
        this.postService = postService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @InitBinder("userDTO")
    public void initUserBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    // region Main Pages

    @RequestMapping(value = "", method = GET)
    public String home(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return ADMIN_HOME_VIEW;
    }

    @RequestMapping(value = "/mockup", method = GET)
    public String mockup(Model model) {
        return ADMIN_MOCKUP_VIEW;
    }

    // endregion

    // region Users

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView userlist(Model model) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userService.getAllUsers());
        mav.setViewName(ADMIN_USERS_VIEW);
        return mav;
    }

    @RequestMapping(value = "/users/update/{userId}", method = GET)
    public ModelAndView updateUser(@PathVariable("userId") Long id) {
        return populateUserForm(id);
    }

    @RequestMapping(value = "/users/new", method = RequestMethod.GET)
    public ModelAndView initAddUserForm() {
        return populateUserForm((long) -1);
    }

    @RequestMapping(value = "/users/password/{userId}", method = RequestMethod.GET)
    public String setPasswordPage(@PathVariable("userId") Long userId, Model model) {
        UserPasswordDTO userPasswordDTO = new UserPasswordDTO(userId, UUID.randomUUID().toString());
        model.addAttribute("userPasswordDTO", userPasswordDTO);
        model.addAttribute("userDescription", getUserDescription(userId));
        return ADMIN_USERPASSWORD_VIEW;
    }

    private String getUserDescription(long userId) {
        String userDescription = null;
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            userDescription = String.format("%s %s (%s)", user.get().getFirstName(),
                    user.get().getLastName(), user.get().getUsername());
        }
        return webUI.getMessage(ADMIN_USER_SETPASSWORD_HEADING_KEY, userDescription);
    }

    @RequestMapping(value = "/users/password", method = RequestMethod.POST)
    public String setPasswordPage(@Valid UserPasswordDTO userPasswordDTO, BindingResult result, Model model,
                                  RedirectAttributes attributes) {

        long userId = userPasswordDTO.getUserId();
        if (result.hasErrors()) {
            model.addAttribute("userDescription", getUserDescription(userId));
            return ADMIN_USERPASSWORD_VIEW;
        } else {
            if (!userPasswordDTO.getPassword().equals(userPasswordDTO.getRepeatedPassword())) {
                result.reject(GLOBAL_ERROR_PASSWORDS_DONOT_MATCH_KEY);
                model.addAttribute("userDescription", getUserDescription(userId));
                return ADMIN_USERPASSWORD_VIEW;
            } else {
                userService.updatePassword(userPasswordDTO);
                Optional<User> user = userService.getUserById(userPasswordDTO.getUserId());
                if (user.isPresent()) {
                    webUI.addFeedbackMessage(attributes, FEEDBACK_USER_PASSWORD_UPDATED_KEY, user.get().getFirstName(),
                            user.get().getLastName());
                }
            }
        }
        return "redirect:/admin/users";
    }

    private ModelAndView populateUserForm(Long id) {

        ModelAndView mav = new ModelAndView();
        Optional<User> found = userService.getUserById(id);
        User user;
        if (found.isPresent()) {
            user = found.get();
            logger.info("Editing User with id and username: {} {}", id, user.getUsername());
            mav.addObject("userDTO", UserUtils.userToUserDTO(user));
        } else {
            mav.addObject("userDTO", new UserDTO());
        }
        mav.addObject("authorities", userService.getRoles());
        mav.setViewName(ADMIN_USERFORM_VIEW);
        return mav;
    }

    @RequestMapping(value = "/users/update/{userId}", method = RequestMethod.POST)
    public String updateUser(@Valid UserDTO userDTO, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("authorities", userService.getRoles());
            return ADMIN_USERFORM_VIEW;
        } else {

            userDTO.setUpdateChildren(true);
            userService.update(userDTO);

            attributes.addAttribute(PARAMETER_USER_ID, userDTO.getUserId());
            webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_UPDATED,
                    userDTO.getFirstName() + " " + userDTO.getLastName());

            return "redirect:/admin/users";
        }
    }

    @RequestMapping(value = "/users/new", method = RequestMethod.POST)
    public String addUser(@Valid UserDTO userDTO,
                          BindingResult result, SessionStatus status, Model model,
                          RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("authorities", userService.getRoles());
            return ADMIN_USERFORM_VIEW;
        } else {
            userDTO.setSignInProvider(SignInProvider.SITE);
            User added = userService.create(userDTO);
            logger.info("Added user with information: {}", added);
            status.setComplete();

            webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_ADDED, added.getFirstName(),
                    added.getLastName());

            return "redirect:/admin/users";
        }
    }

    // endregion

    // region Roles


    @RequestMapping(value = "/roles/update/{Id}", method = RequestMethod.POST)
    public String updateRole(@Valid @ModelAttribute(value = "authority") RoleDTO roleDTO, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ERROR);
            return "redirect:/admin/roles";
        } else {
            Authority authority = userService.getAuthorityById(roleDTO.getId());
            if (authority.isLocked()) {
                webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED, roleDTO.getAuthority().toUpperCase());
                return "redirect:/admin/roles";
            } else {
                userService.updateAuthority(roleDTO);
                webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_UPDATED, roleDTO.getAuthority());
                return "redirect:/admin/roles";
            }
        }
    }

    @RequestMapping(value = "/roles/update/{Id}", params = {"deleteRole"}, method = RequestMethod.POST)
    public String deleteRole(@Valid @ModelAttribute(value = "authority") RoleDTO roleDTO, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (result.hasErrors()) {
            webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ERROR);
            return "redirect:/admin/roles";
        } else {
            Authority authority = userService.getAuthorityById(roleDTO.getId());

            if (authority.isLocked()) {
                webUI.addFeedbackMessage(attributes,
                        FEEDBACK_MESSAGE_KEY_ROLE_IS_LOCKED, roleDTO.getAuthority());
            } else {
                Collection<User> users = userService.getUsersByAuthorityId(roleDTO.getId());
                userService.deleteAuthority(authority, users);
                webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_DELETED,
                        roleDTO.getAuthority(), users.size());
            }

            return "redirect:/admin/roles";
        }
    }

    @RequestMapping(value = "/roles/new", method = RequestMethod.POST)
    public String addUser(@Valid RoleDTO roleDTO,
                          BindingResult result,
                          SessionStatus status,
                          RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return ADMIN_ROLES_VIEW;
        } else {

            Authority authority = userService.createAuthority(roleDTO);
            logger.info("Role Added: {}", authority);
            status.setComplete();

            webUI.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ROLE_ADDED, authority.getAuthority());
            return "redirect:/admin/roles";
        }
    }

    @RequestMapping(value = "/roles", method = GET)
    public ModelAndView roleList(Model model) {

        ModelAndView mav = new ModelAndView();
        mav.addObject("roles", userService.getRoles());
        mav.addObject("newRole", new Authority());
        mav.setViewName(ADMIN_ROLES_VIEW);
        return mav;
    }

    // endregion

    // region Site Settings

    @RequestMapping(value = "/site/settings", method = GET)
    public ModelAndView siteSettings(Model model) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("siteOptionMapDTO", getGeneralSiteSettings());
        mav.setViewName(ADMIN_SITESETTINGS_VIEW);
        return mav;
    }

    @RequestMapping(value = "/site/settings", method = RequestMethod.POST)
    public String siteSettings(@Valid SiteOptionMapDTO siteOptionMapDTO,
                               BindingResult result,
                               RedirectAttributes attributes) throws SiteOptionNotFoundException {
        if (hasSiteSettingsErrors(result)) {
            return ADMIN_SITESETTINGS_VIEW;
        } else {
            updateGeneralSiteSettings(siteOptionMapDTO);
            webUI.addFeedbackMessage(attributes, FEEDBACK_SITE_SETTINGS_UPDATED);
            return "redirect:/admin/site/settings";
        }
    }

    // endregion

    // region Utility Methods

    SiteOptionMapDTO getGeneralSiteSettings() {
        return SiteOptionMapDTO.withGeneralSettings(
                siteOptions.getSiteName(),
                siteOptions.getSiteDescription(),
                siteOptions.getAddGoogleAnalytics(),
                siteOptions.getGoogleAnalyticsTrackingId(),
                siteOptions.getUserRegistration())
                .build();
    }

    void updateGeneralSiteSettings(SiteOptionMapDTO siteOptionMapDTO)
            throws SiteOptionNotFoundException {

        siteService.update(SiteOptionDTO.with(
                ISiteOption.SITE_NAME, siteOptionMapDTO.getSiteName())
                .build());
        siteService.update(SiteOptionDTO.with(
                ISiteOption.SITE_DESCRIPTION, siteOptionMapDTO.getSiteDescription())
                .build());
        siteService.update(SiteOptionDTO.with(
                ISiteOption.ADD_GOOGLE_ANALYTICS, siteOptionMapDTO.getAddGoogleAnalytics())
                .build());
        siteService.update(SiteOptionDTO.with(
                ISiteOption.GOOGLE_ANALYTICS_TRACKING_ID, siteOptionMapDTO.getGoogleAnalyticsTrackingId())
                .build());
        siteService.update(SiteOptionDTO.with(
                ISiteOption.USER_REGISTRATION, siteOptionMapDTO.getUserRegistration())
                .build());

    }


    private Boolean hasSiteSettingsErrors(BindingResult result) {
        for (FieldError error : result.getFieldErrors()) {
            if (!error.getField().equals("integerProperty"))
                return true;
        }
        return false;
    }

    // endregion

}
