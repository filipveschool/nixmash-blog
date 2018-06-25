package com.nixmash.blog.mvc.components;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.dto.ProfileImageDTO;
import com.nixmash.blog.jpa.service.interfaces.StatService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

@Slf4j
@Component
public class WebUI {


    public static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";

    @Resource
    private MessageSource messageSource;

    private final Environment environment;
    private final ApplicationSettings applicationSettings;
    private final StatService statService;

    @Autowired
    public WebUI(Environment environment, ApplicationSettings applicationSettings, StatService statService) {
        this.environment = environment;
        this.applicationSettings = applicationSettings;
        this.statService = statService;
    }

    // region Message Functions

    public String getMessage(String code, Object... params) {
        Locale current = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, params, current);
    }

    public void addFeedbackMessage(RedirectAttributes model, String code, Object... params) {
        String localizedFeedbackMessage = getMessage(code, params);
        model.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK, localizedFeedbackMessage);
    }

    public String parameterizedMessage(String code, Object... params) {
        String localizedMessage = getMessage(code, params);
        return localizedMessage;
    }

    // endregion

    // region User Profile MultiFile Upload Functions

    public void processProfileImage(ProfileImageDTO profileImageDTO, String userKey)
            throws IOException {

        // Reduce original image size. Thumbnailator will not modify
        // image if less than 600x600

        BufferedImage bufferedProfileImage =
                Thumbnails.of(profileImageDTO.getFile().getInputStream())
                        .forceSize(600, 600)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedProfileImage, userKey, false);

        // Create profile image icon. Saved to separate directory

        BufferedImage bufferedIconImage =
                Thumbnails.of(profileImageDTO.getFile().getInputStream())
                        .forceSize(32, 32)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedIconImage, userKey, true);
    }


    public void processProfileImage(String providerImageUrl, String userKey)
            throws IOException {

        // Reduce original image size. Thumbnailator will not modify
        // image if less than 600x600

        BufferedImage bufferedProfileImage =
                Thumbnails.of(new URL(providerImageUrl))
                        .forceSize(600, 600)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedProfileImage, userKey, false);

        // Create profile image icon. Saved to separate directory

        BufferedImage bufferedIconImage =
                Thumbnails.of(new URL(providerImageUrl))
                        .forceSize(32, 32)
                        .allowOverwrite(true)
                        .outputFormat("png")
                        .asBufferedImage();

        saveProfileImage(bufferedIconImage, userKey, true);
    }

    private void saveProfileImage(BufferedImage bufferedImage, String userKey, boolean isIcon) throws IOException {

        String destination = isIcon ? applicationSettings.getProfileIconPath() : applicationSettings.getProfileImagePath();
        File imageDestination = new File(destination + userKey);
        ImageIO.write(bufferedImage, "png", imageDestination);

    }

    // endregion

}

