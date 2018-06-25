package com.nixmash.blog.mail;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.mail.common.MailSettings;
import com.nixmash.blog.mail.components.MailSender;
import com.nixmash.blog.mail.components.MailUI;
import com.nixmash.blog.mail.dto.MailDTO;
import com.nixmash.blog.mail.service.interfaces.FmMailService;
import com.nixmash.blog.mail.service.implementations.FmMailServiceImpl;
import freemarker.template.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class FmMailTests extends MailContext {

    private MailSender mockMailSender;
    private MailDTO mailDTO;

    private FmMailService mockFmMailService;
    private MailSettings mailSettings;
    private ApplicationSettings applicationSettings;
    private Configuration fm;
    private Environment environment;
    private MailUI mailUI;

    @Autowired
    UserService userService;

    @Before
    public void setUp() {
        mockMailSender = mock(MailSender.class);
        mockFmMailService =
                new FmMailServiceImpl(mockMailSender, mailSettings,applicationSettings, fm, environment, mailUI);
        mailDTO = MailTestUtils.testMailDTO();
    }

    @Test
    public void contactSendsMimeMessage() throws MessagingException {
        mockFmMailService.sendContactMail(mailDTO);
        verify(mockMailSender, Mockito.times(1)).send(any(MimeMessagePreparator.class));
    }

    @Test
    public void passwordResetSendsMimeMessage() throws MessagingException {
        Optional<User> user = userService.getUserById(2L);
        String token = UUID.randomUUID().toString();
        mockFmMailService.sendResetPasswordMail(user.get(), token);
        verify(mockMailSender, Mockito.times(1)).send(any(MimeMessagePreparator.class));
    }

    @Test
    public void userVerificationSendsMimeMessage() throws MessagingException {
        Optional<User> user = userService.getUserById(2L);
        mockFmMailService.sendUserVerificationMail(user.get());
        verify(mockMailSender, Mockito.times(1)).send(any(MimeMessagePreparator.class));
    }

}
