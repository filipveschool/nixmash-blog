package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.model.Tag;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TagDTO implements Serializable {

    private static final long serialVersionUID = -4809849404139121173L;

    private long tagId = -1;

    private String tagValue;

    private int tagCount = 0;

    public TagDTO() {
    }

    public TagDTO(String tagValue) {
        this.tagValue = tagValue;
    }

    public TagDTO(long tagId, String tagValue) {
        this.tagId = tagId;
        this.tagValue = tagValue;
    }

    public TagDTO(Tag tag) {
        this.tagId = tag.getTagId();
        this.tagValue = tag.getTagValue();
        this.tagCount = tag.getPosts().size();
    }
}

