package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.enums.ContentType;
import com.nixmash.blog.jpa.model.Like;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.repository.LikeRepository;
import com.nixmash.blog.jpa.repository.PostRepository;
import com.nixmash.blog.jpa.service.interfaces.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
public class LikeImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CacheManager cacheManager;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PostRepository postRepository;


    // region Likes

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPostsByUserLikes(Long userId) {
        List<Post> posts;
        if (likeRepository.findLikedPostIds(userId).size() == 0)
            return null;
        else
            posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                    .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                    .getResultList();
        return posts;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize) {
        List<Post> posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return posts;
    }

    @Transactional
    @Override
    public int addPostLike(long userId, long postId) {
        int incrementValue = 1;
        Post post = postRepository.findByPostId(postId);
        Optional<Long> likeId = likeRepository.findPostLikeIdByUserId(userId, postId);
        if (likeId.isPresent()) {
            incrementValue = -1;
            likeRepository.delete(likeId.get());
        } else {
            Like like = new Like(userId, postId, ContentType.POST);
            likeRepository.save(like);
        }
        post.updateLikes(incrementValue);
        clearPostCaches();
        return incrementValue;
    }

    // endregion

    public void clearPostCaches() {
        cacheManager.getCache("posts").clear();
        cacheManager.getCache("pagedPosts").clear();
    }

}
