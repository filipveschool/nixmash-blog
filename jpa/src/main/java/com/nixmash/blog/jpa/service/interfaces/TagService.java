package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.exceptions.TagNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface TagService {

    Tag getTag(String tagValue);

    @Transactional
    Tag createTag(TagDTO tagDTO);

    @Transactional
    Tag updateTag(TagDTO tagDTO);

    @Transactional
    void deleteTag(TagDTO tagDTO, List<Post> posts);

    @Transactional(readOnly = true)
    Set<TagDTO> getTagDTOs();

    @Transactional(readOnly = true)
    List<TagDTO> getTagCloud(int tagCount);

    @Transactional(readOnly = true)
    List<String> getTagValues();

    Set<TagDTO> getTagDTOs(Long postId);
}
