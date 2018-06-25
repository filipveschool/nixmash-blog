package com.nixmash.blog.jpa.model.validators;


import com.nixmash.blog.jpa.dto.UserPasswordDTO;
import com.nixmash.blog.jpa.model.User;
import com.nixmash.blog.jpa.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Slf4j
@Component
public class UserPasswordValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserPasswordDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        UserPasswordDTO form = (UserPasswordDTO) target;
        validatePasswords(errors, form);
        preventDemoUserUpdate(errors, form);
    }

    private void preventDemoUserUpdate(Errors errors, UserPasswordDTO form) {
        Optional<User> user = userService.getUserById(form.getUserId());
        if (user.isPresent()) {
            if (user.get().getUsername().toLowerCase().equals("user")) {
                errors.reject("global.error.password.demo.user");
            }
        }
    }

    private void validatePasswords(Errors errors, UserPasswordDTO form) {
        if (!form.getPassword().equals(form.getRepeatedPassword())) {
            errors.reject("password.no_match", "Passwords do not match");
        }
    }
}
