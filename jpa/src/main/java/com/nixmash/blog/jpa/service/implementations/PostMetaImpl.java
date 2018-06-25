package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.repository.PostMetaRepository;
import com.nixmash.blog.jpa.service.interfaces.PostMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

            PostMeta pp = new PostMeta();
            pp.setTwitterCardType(postMeta.getTwitterCardType());
            pp.setTwitterTitle(post.getPostTitle());
            pp.setTwitterSite(twitterSite);
            pp.setTwitterCreator(postMeta.getTwitterCreator());
            pp.setTwitterDescription(postMeta.getTwitterDescription());
            pp.setTwitterImage(twitterImage);
            pp.setTwitterUrl(twitterUrl);
            return pp;

        } else
            return null;
    }

    // endregion
}
