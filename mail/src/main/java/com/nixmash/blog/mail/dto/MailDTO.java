package com.nixmash.blog.mail.dto;

import com.nixmash.blog.jpa.model.validators.ExtendedEmailValidator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Basic;

@Getter
@Setter
public class MailDTO {

    private static final int MAX_LENGTH_EMAIL_ADDRESS = 65;
    private static final int MAX_LENGTH_FROM_NAME = 60;
    private static final int MAX_LENGTH_EMAIL_BODY = 1000;

    @Basic
    @ExtendedEmailValidator
    @Length(max = MAX_LENGTH_EMAIL_ADDRESS)
    private String from;

    @NotEmpty
    @Length(max = MAX_LENGTH_FROM_NAME)
    private String fromName;

    @NotEmpty
    @Length(max = MAX_LENGTH_EMAIL_BODY)
    private String body;

    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String templateName;

    public static enum Type {
        PLAIN,
        HTML
    };

    private Type type = Type.PLAIN;

    public MailDTO() {
    }

    private MailDTO(MailDTO m) {
        this.type = m.type;
    }

    @Override
    public String toString() {

        return "MailDTO{" +
                "from=" + from +
                ", fromName=" + fromName +
                ", to=" + to +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", templateName='" + templateName + '\'' +
                ", type=" + type +
                '}';
    }

}
