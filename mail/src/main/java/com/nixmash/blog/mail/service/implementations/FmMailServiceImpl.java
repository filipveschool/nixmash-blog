package com.nixmash.blog.mail.service.implementations;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.mail.common.MailSettings;
import com.nixmash.blog.mail.components.MailSender;
import com.nixmash.blog.mail.components.MailUI;
import com.nixmash.blog.mail.dto.MailDTO;
import com.nixmash.blog.mail.service.interfaces.FmMailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
@SuppressWarnings("Duplicates")
@Service("fmMailService")
public class FmMailServiceImpl implements FmMailService {

    private static final String CONTACT_EMAIL_SUBJECT = "mail.contact.subject";
    private static final String CONTACT_EMAIL_GREETING = "mail.contact.greeting";
    private static final String EMAIL_SITE_USER_SERVICES = "mail.site.user.services";

    @Autowired
    private MailSender mailSender;

    @Autowired
    private MailSettings mailSettings;

    @Autowired
    private ApplicationSettings applicationSettings;

    @Autowired
    private Configuration fm;

    @Autowired
    private Environment environment;

    @Autowired
    private MailUI mailUI;

//    @Value("${mail.contact.body.type}")
//    private MailDTO.Type mailType;
    
    @Override
    public void sendResetPasswordMail(User user, String token) {
        try {
            mailSender.send(new MimeMessagePreparator() {
                static final String PASSWORD_RESET_EMAIL_SUBJECT = "mail.password.reset.subject";
                static final String PASSWORD_RESET_EMAIL_FROM = "mail.password.reset.from";
                static final String PASSWORD_RESET_EMAIL_GREETING = "mail.password.reset.greeting";

                public void prepare(MimeMessage mimeMessage)
                        throws MessagingException {

                    // region build mimeMessage

                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setFrom(mailUI.getMessage(PASSWORD_RESET_EMAIL_FROM));
                    String sendTo = user.getEmail();
                    if (applicationSettings.getBaseUrl().indexOf("localhost") > 0) {
                        sendTo = mailSettings.getDeveloperTo();
                    }
                    message.addTo(sendTo);

                    message.setSubject(mailUI.getMessage(PASSWORD_RESET_EMAIL_SUBJECT));

                    String greeting = mailUI.getMessage(PASSWORD_RESET_EMAIL_GREETING);
                    greeting = MessageFormat.format(greeting, String.format("%s %s", user.getFirstName(), user.getLastName()));

                    String applicationPropertyUrl = environment.getProperty("spring.application.url");
                    String siteName = mailUI.getMessage("mail.site.name");

                    String resetLink = applicationSettings.getBaseUrl() + "/users/resetpassword/" + token;

                    // endregion

                    Map<String, Object> model = new Hashtable<>();
                    model.put("greeting", greeting);
                    model.put("memberServices",  mailUI.getMessage(EMAIL_SITE_USER_SERVICES));
                    model.put("siteName", siteName);
                    model.put("applicationPropertyUrl", applicationPropertyUrl);
                    model.put("resetLink", resetLink);

                    String html;
                    try {
                        Template template = fm.getTemplate("mail/resetpassword.ftl");
                        html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
                        message.setText(html, true);
                    } catch (IOException | TemplateException e) {
                        log.error("Problem merging reset password mail template : " + e.getMessage());
                    }

                    log.info(String.format("Reset Password email sent to: %s", String.format("%s %s", user.getFirstName(), user.getLastName())));
                }

            });
        } catch (MailSendException e) {
            log.error("Reset Password Email Exception: " + e.getMessage());
        }

    }

    @Override
    public void sendContactMail(MailDTO mailDTO) {
        try {
            mailSender.send(mimeMessage -> {

                // region build mimeMessage

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setFrom(mailDTO.getFrom());
                message.addTo(mailSettings.getContactTo());

                if (mailSettings.getSendContactCC()) {
                    message.addCc(mailSettings.getContactCC());
                }

                String subject = mailUI.getMessage(CONTACT_EMAIL_SUBJECT);
                message.setSubject(MessageFormat.format(subject, mailDTO.getFromName()));

                String body = mailDTO.getBody();
                String greeting = mailUI.getMessage(CONTACT_EMAIL_GREETING);
                String applicationPropertyUrl = environment.getProperty("spring.application.url");
                String siteName = mailUI.getMessage("mail.site.name");

                // endregion

                MailDTO.Type mailType =
                        Enum.valueOf(MailDTO.Type.class, mailUI.getMessage("mail.contact.body.type"));

                switch (mailType) {
                    case HTML:
                        Map<String, Object> model = new Hashtable<>();
                        model.put("message", mailDTO);
                        model.put("greeting", greeting);
                        model.put("siteName", siteName);
                        model.put("applicationPropertyUrl", applicationPropertyUrl);

                        String html;
                        try {
                            Template template = fm.getTemplate("mail/contact.ftl");
                            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
                            message.setText(html, true);
                        } catch (IOException | TemplateException e) {
                            log.error("Problem merging contact mail template : " + e.getMessage());
                        }

                        break;
                    default:
                        message.setText(body);
                        break;
                }
                log.info(String.format("Contact Email sent from: %s", mailDTO.getFrom()));
            });
        } catch (MailSendException e) {
            log.error("Contact Email Exception: " + e.getMessage());
        }

    }

    @Override
    public void sendUserVerificationMail(User user) {
        try {
            mailSender.send(new MimeMessagePreparator() {
                static final String USER_VERIFICATION_EMAIL_SUBJECT = "mail.user.verification.subject";
                static final String USER_VERIFICATION_EMAIL_FROM = "mail.user.verification.from";
                static final String USER_VERIFICATION_EMAIL_GREETING = "mail.user.verification.greeting";

                public void prepare(MimeMessage mimeMessage)
                        throws MessagingException {

                    // region build mimeMessage

                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setFrom(mailUI.getMessage(USER_VERIFICATION_EMAIL_FROM));
                    String sendTo = user.getEmail();
                    if (applicationSettings.getBaseUrl().indexOf("localhost") > 0) {
                        sendTo = mailSettings.getDeveloperTo();
                    }
                    message.addTo(sendTo);

                    message.setSubject(mailUI.getMessage(USER_VERIFICATION_EMAIL_SUBJECT));

                    String greeting = mailUI.getMessage(USER_VERIFICATION_EMAIL_GREETING);
                    greeting = MessageFormat.format(greeting, String.format("%s %s", user.getFirstName(), user.getLastName()));

                    String applicationPropertyUrl = environment.getProperty("spring.application.url");
                    String siteName = mailUI.getMessage("mail.site.name");

                    String verifyLink = applicationSettings.getBaseUrl() + "/users/verify/" + user.getUserKey();

                    // endregion

                    Map<String, Object> model = new Hashtable<>();
                    model.put("greeting", greeting);
                    model.put("memberServices",  mailUI.getMessage(EMAIL_SITE_USER_SERVICES));
                    model.put("siteName", siteName);
                    model.put("applicationPropertyUrl", applicationPropertyUrl);
                    model.put("verifyLink", verifyLink);

                    String html;
                    try {
                        Template template = fm.getTemplate("mail/userverification.ftl");
                        html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
                        message.setText(html, true);
                    } catch (IOException | TemplateException e) {
                        log.error("Problem merging user verification mail template : " + e.getMessage());
                    }

                    log.info(String.format("User Verification email sent to: %s", String.format("%s %s", user.getFirstName(), user.getLastName())));
                }

            });
        } catch (MailSendException e) {
            log.error("User Verification Email Exception: " + e.getMessage());
        }

    }

}
