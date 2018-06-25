package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import org.springframework.transaction.annotation.Transactional;

public interface PostMetaService {

    @Transactional(readOnly = true)
    PostMeta buildTwitterMetaTagsForDisplay(Post post);

    @Transactional(readOnly = true)
    PostMeta getPostMetaById(Long postId);
}
