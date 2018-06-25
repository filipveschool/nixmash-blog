/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nixmash.blog.mvc.controller;

import com.google.common.collect.Lists;
import com.nixmash.blog.jpa.dto.ProfileImageDTO;
import com.nixmash.blog.jpa.dto.SocialUserDTO;
import com.nixmash.blog.jpa.dto.UserDTO;
import com.nixmash.blog.jpa.enums.SignInProvider;
import com.nixmash.blog.jpa.model.Authority;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.model.UserConnection;
import com.nixmash.blog.jpa.model.validators.SocialUserFormValidator;
import com.nixmash.blog.jpa.model.validators.UserCreateFormValidator;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.mail.service.interfaces.FmMailService;
import com.nixmash.blog.mvc.components.WebUI;
import com.nixmash.blog.mvc.security.SignInUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.nixmash.blog.mvc.controller.GeneralController.HOME_VIEW;
import static com.nixmash.blog.mvc.controller.GeneralController.REDIRECT_HOME_VIEW;
import static com.nixmash.blog.mvc.controller.GlobalController.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class UserController {

    // region Constants

    public static final String MODEL_ATTRIBUTE_CURRENTUSER = "currentUser";
    private static final String MODEL_ATTRIBUTE_SOCIALUSER = "socialUserDTO";
    public static final String USER_PROFILE_VIEW = "users/profile";
    public static final String USER_REVERIFY_VIEW = "users/reverify";
    public static final String SIGNUP_VIEW = "signup";
    public static final String SIGNIN_VIEW = "signin";
    public static final String REGISTER_VIEW = "register";
    public static final String MESSAGE_KEY_SOCIAL_SIGNUP = "signup.page.subheader";

    public static final String USER_VERIFICATION_NOKEY_TITLE = "user.verification.nokey.title";
    private static final String USER_VERIFICATION_NOKEY_MESSAGE = "user.verification.nokey.message";
    public static final String USER_VERIFICATION_ERROR_TITLE = "user.verification.error.title";
    private static final String USER_VERIFICATION_ERROR_MESSAGE = "user.verification.error.message";
    private static final String USER_VERIFICATION_EMAIL_SENT = "user.verification.email.sent";

    // endregion

    // region Private Classes

    private final UserService userService;
    private final UserCreateFormValidator userCreateFormValidator;
    private final SocialUserFormValidator socialUserFormValidator;
    private final ProviderSignInUtils providerSignInUtils;
    private WebUI webUI;
    private final FmMailService fmMailService;

    // endregion

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    public UserController(UserService userService, UserCreateFormValidator userCreateFormValidator,
                          SocialUserFormValidator socialUserFormValidator, ProviderSignInUtils providerSignInUtils, WebUI webUI, FmMailService fmMailService) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
        this.socialUserFormValidator = socialUserFormValidator;
        this.providerSignInUtils = providerSignInUtils;
        this.webUI = webUI;
        this.fmMailService = fmMailService;
    }

    // region validators

    @InitBinder("userDTO")
    public void initUserBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @InitBinder("socialUserDTO")
    public void initSocialUserBinder(WebDataBinder binder) {
        binder.addValidators(socialUserFormValidator);
    }

    // endregion

    // region User Sign-in, Registration and Email Validation

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public void signin() {
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registrationForm(@ModelAttribute UserDTO userDTO, HttpServletRequest request) {

        if (request.getUserPrincipal() != null)
            return "redirect:/";
        else
            return HOME_VIEW;
    }

    @RequestMapping(value = "/register", method = POST)
    public String register(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, WebRequest request,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return REGISTER_VIEW;
        }
        userDTO.setSignInProvider(SignInProvider.SITE);
        userDTO.setAuthorities(Lists.newArrayList(new Authority("ROLE_USER")));

        // todo: Add SiteOptions.AuthenticationType for optional setEnabled(false)
        userDTO.setEnabled(false);

        User user = userService.create(userDTO);

        String redirectionUrl = "redirect:/";

        // todo: SiteOptions.AuthenticationType check
        if (!userDTO.isEnabled()) {
            // send validation email
            fmMailService.sendUserVerificationMail(user);
            redirectAttributes.addFlashAttribute("statusMessage", webUI.getMessage(USER_VERIFICATION_EMAIL_SENT, user.getEmail()));
            redirectionUrl += "register?message";
        } else {
            // non-email validation
            SignInUtils.authorizeUser(user);
        }
        return redirectionUrl;
    }

    @RequestMapping(value = "/users/reverify/{username}", method = RequestMethod.GET)
    public String resendUserVerification(@PathVariable String username, RedirectAttributes redirectAttributes) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found!");
        } else {
            redirectAttributes.addFlashAttribute("statusMessage", webUI.getMessage(USER_VERIFICATION_EMAIL_SENT, user.getEmail()));
            return "redirect:/signin?message";
        }
    }

    @RequestMapping(value = "/users/verify/{userkey}", method = RequestMethod.GET)
    public String verifyUser(@PathVariable("userkey") String userkey, Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.getByUserKey(userkey);
        String viewName = ERROR_CUSTOM_VIEW;
        if (!user.isPresent()) {
            model.addAttribute(ERROR_PAGE_TITLE_ATTRIBUTE,
                    webUI.getMessage(USER_VERIFICATION_ERROR_TITLE));
            model.addAttribute(ERROR_PAGE_MESSAGE_ATTRIBUTE,
                    webUI.getMessage(USER_VERIFICATION_ERROR_MESSAGE));
        } else {
            userService.enableAndApproveUser(user.get());
            redirectAttributes.addFlashAttribute("emailVerifiedWelcomeMessage", true);
            viewName = REDIRECT_HOME_VIEW;
        }
        return viewName;
    }

    // endregion

    // region Social User Signup

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(@ModelAttribute SocialUserDTO socialUserDTO, WebRequest request, Model model) {

        if (request.getUserPrincipal() != null)
            return "redirect:/";
        else {
            Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
            request.setAttribute("connectionSubheader",
                    webUI.parameterizedMessage(MESSAGE_KEY_SOCIAL_SIGNUP,
                            StringUtils.capitalize(connection.getKey().getProviderId())),
                    RequestAttributes.SCOPE_REQUEST);

            socialUserDTO = createSocialUserDTO(request, connection);

            ConnectionData connectionData = connection.createData();
            SignInUtils.setUserConnection(request, connectionData);

            model.addAttribute(MODEL_ATTRIBUTE_SOCIALUSER, socialUserDTO);
            return SIGNUP_VIEW;
        }
    }

    @RequestMapping(value = "/signup", method = POST)
    public String signup(@Valid @ModelAttribute("socialUserDTO") SocialUserDTO socialUserDTO, BindingResult result,
                         WebRequest request, RedirectAttributes redirectAttributes) {


        if (result.hasErrors()) {
            return SIGNUP_VIEW;
        }

        UserDTO userDTO = createUserDTO(socialUserDTO);
        User user = userService.create(userDTO);

        providerSignInUtils.doPostSignUp(userDTO.getUsername(), request);
        UserConnection userConnection =
                userService.getUserConnectionByUserId(userDTO.getUsername());
        if (userConnection.getImageUrl() != null) {
            try {
                webUI.processProfileImage(userConnection.getImageUrl(), user.getUserKey());
                userService.updateHasAvatar(user.getId(), true);
            } catch (IOException e) {
                logger.error("ImageUrl IOException for username: {0}", user.getUsername());
            }
        }
        SignInUtils.authorizeUser(user);

        redirectAttributes.addFlashAttribute("connectionWelcomeMessage", true);
        return "redirect:/";
    }

    private UserDTO createUserDTO(SocialUserDTO socialUserDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(socialUserDTO.getUsername().toLowerCase());
        userDTO.setFirstName(socialUserDTO.getFirstName());
        userDTO.setLastName(socialUserDTO.getLastName());
        userDTO.setEmail(socialUserDTO.getEmail());
        userDTO.setSignInProvider(socialUserDTO.getSignInProvider());
        userDTO.setPassword(UUID.randomUUID().toString());
        userDTO.setAuthorities(Lists.newArrayList(new Authority("ROLE_USER")));
        return userDTO;
    }

    private SocialUserDTO createSocialUserDTO(WebRequest request, Connection<?> connection) {
        SocialUserDTO dto = new SocialUserDTO();

        if (connection != null) {
            UserProfile socialMediaProfile = connection.fetchUserProfile();
            dto.setEmail(socialMediaProfile.getEmail());
            dto.setFirstName(socialMediaProfile.getFirstName());
            dto.setLastName(socialMediaProfile.getLastName());

            ConnectionKey providerKey = connection.getKey();
            dto.setSignInProvider(SignInProvider.valueOf(providerKey.getProviderId().toUpperCase()));

        }

        return dto;
    }

    // endregion

    // region User Profile and Account Services

//    @PreAuthorize("#username == authentication.name")
//    @RequestMapping(value = "/{username}", method = GET)
//    public String profilePage(@PathVariable("username") String username,
//                              Model model, WebRequest request)
//            throws UsernameNotFoundException {
//
//        logger.info("Showing user page for user: {}", username);
//        ProfileImageDTO profileImageDTO = new ProfileImageDTO();
//        model.addAttribute("profileImageDTO", profileImageDTO);
//
//        return USER_PROFILE_VIEW;
//    }

    @PreAuthorize("#username == authentication.name")
    @GetMapping(value = "/user/{username}")
    public String userProfilePage(@PathVariable("username") String username,
                                  Model model) throws UsernameNotFoundException {
        logger.info("Showing user page for user: {}", username);
        ProfileImageDTO profileImageDTO = new ProfileImageDTO();
        model.addAttribute("profileImageDTO", profileImageDTO);

        return USER_PROFILE_VIEW;
    }

    // endregion

}
