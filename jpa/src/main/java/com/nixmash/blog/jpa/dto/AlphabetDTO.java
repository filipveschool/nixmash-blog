package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AlphabetDTO implements Serializable {

    private static final long serialVersionUID = -8261176350405087033L;

    private String alphaCharacter;

    private Boolean isActive;

    public AlphabetDTO() {
    }

    public AlphabetDTO(String alphaCharacter, Boolean isActive) {
        this.alphaCharacter = alphaCharacter;
        this.isActive = isActive;
    }
}
