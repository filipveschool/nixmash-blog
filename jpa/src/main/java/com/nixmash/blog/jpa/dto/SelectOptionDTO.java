package com.nixmash.blog.jpa.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SelectOptionDTO implements Serializable {

    private static final long serialVersionUID = -8003039691215392613L;

    private String label;

    private String value;

    private Boolean selected;

    public SelectOptionDTO(){

    }

    public SelectOptionDTO(String label, String value, Boolean selected) {
        this.label = label;
        this.value = value;
        this.selected = selected;
    }


}
