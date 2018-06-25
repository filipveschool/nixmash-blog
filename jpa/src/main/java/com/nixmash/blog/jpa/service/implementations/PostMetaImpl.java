package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.repository.PostMetaRepository;
import com.nixmash.blog.jpa.service.interfaces.PostMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class PostMetaImpl implements PostMetaService {

    @Autowired
    private PostMetaRepository postMetaRepository;

    @Autowired
    private ApplicationSettings applicationSettings;

    // region PostMeta Services

    @Override
    @Transactional(readOnly = true)
    public PostMeta getPostMetaById(Long postId) {
        return postMetaRepository.findByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public PostMeta buildTwitterMetaTagsForDisplay(Post post) {
        PostMeta postMeta = post.getPostMeta();
        if (!postMeta.getTwitterCardType().equals(TwitterCardType.NONE)) {
            String twitterSite = applicationSettings.getTwitterSite();
            String twitterUrl = String.format("%s/post/%s", applicationSettings.getBaseUrl(), post.getPostName());
            String twitterImage = String.format("%s%s", applicationSettings.getBaseUrl(), postMeta.getTwitterImage());
            return PostMeta.getBuilder(
                    postMeta.getTwitterCardType(),
                    post.getPostTitle(),
                    twitterSite,
                    postMeta.getTwitterCreator())
                    .twitterDescription(postMeta.getTwitterDescription())
                    .twitterImage(twitterImage)
                    .twitterUrl(twitterUrl)
                    .build();
        } else
            return null;
    }

    // endregion
}
