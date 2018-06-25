package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    List<Like> findByUserIdAndContentTypeId(long userId, int contentTypeId);

    List<Like> findByContentTypeId(int contentTypeId);

    List<Like> findAll();

    @Query("select l.itemId from Like l where l.contentTypeId = 1 and l.userId = ?1")
    List<Long> findLikedPostIds(long userId);

    @Query("select l.likeId from Like l where l.contentTypeId = 1 and l.userId = ?1 and l.itemId = ?2")
    Optional<Long> findPostLikeIdByUserId(long userId, long postId);

}
