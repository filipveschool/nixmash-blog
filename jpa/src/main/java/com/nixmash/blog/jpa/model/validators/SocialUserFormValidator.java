package com.nixmash.blog.jpa.model.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.nixmash.blog.jpa.dto.SocialUserDTO;
import com.nixmash.blog.jpa.service.interfaces.UserService;

@Slf4j
@Component
public class SocialUserFormValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SocialUserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        SocialUserDTO form = (SocialUserDTO) target;
        validateEmail(errors, form);
        validateUsername(errors, form);
    }

    private void validateEmail(Errors errors, SocialUserDTO form) {
        if (userService.getByEmail(form.getEmail()).isPresent()) {
            errors.reject("email.exists", "User with this email already exists");
        }
    }

    private void validateUsername(Errors errors, SocialUserDTO form) {
        if (userService.getUserByUsername(form.getUsername()) != null) {
            errors.reject("user.exists", "User with this username already exists");
        }
    }
}
