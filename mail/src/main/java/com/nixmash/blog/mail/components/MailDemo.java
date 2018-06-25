package com.nixmash.blog.mail.components;

import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import com.nixmash.blog.mail.dto.MailDTO;
import com.nixmash.blog.mail.service.interfaces.FmMailService;
import com.nixmash.blog.mail.service.interfaces.FmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class MailDemo {

    @Autowired
    private FmMailService fmMailService;

    @Autowired
    private UserService userService;

    @Autowired
    private FmService fmService;

    public void init() {
        displayUserTemplate();
    }

    private void displayUserTemplate() {
        Optional<User> user = userService.getUserById(1L);
        if (user.isPresent()) {
                System.out.println(fmService.displayTestTemplate(user.get()));
        }
    }

    private void userVerificationDemo() {
        Optional<User> user = userService.getUserById(8L);
        if (user.isPresent()) {
            fmMailService.sendUserVerificationMail(user.get());
        }
    }

    private void passwordResetDemo() {
        Optional<User> user = userService.getUserById(8L);
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            user.get().setEmail("daveburke@localhost");
            fmMailService.sendResetPasswordMail(user.get(), token);
        }
    }

    private void contactDemo() {
        fmMailService.sendContactMail(createContactMailDTO());
    }

    private MailDTO createContactMailDTO() {
        MailDTO mailDTO = new MailDTO();
        mailDTO.setFrom("contact@aol.com");
        mailDTO.setFromName("Contact Dude");
        mailDTO.setBody("This is a message from a contact");
        return  mailDTO;
    }

}
