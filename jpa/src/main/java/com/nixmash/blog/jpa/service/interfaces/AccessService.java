package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.dto.AccessDTO;

public interface AccessService {

    boolean isEmailApproved(String email);

    AccessDTO createAccessDTO(String email);
}
