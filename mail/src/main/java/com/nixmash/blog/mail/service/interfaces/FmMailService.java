package com.nixmash.blog.mail.service.interfaces;

import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.mail.dto.MailDTO;

public interface FmMailService {

    void sendResetPasswordMail(User user, String token);

    void sendContactMail(MailDTO mailDTO);

    void sendUserVerificationMail(User user);
}
