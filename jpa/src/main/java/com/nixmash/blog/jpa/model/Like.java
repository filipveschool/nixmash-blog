package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "user_likes")
public class Like {

    @Id
    @Column(name = "like_id", nullable = false)
    private long likeId;

    @Basic
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Basic
    @Column(name = "item_id", nullable = false)
    private long itemId;

    @Basic
    @Column(name = "content_type_id", nullable = false)
    private int contentTypeId;

    public Like() {
    }

    public Like(long userId, long itemId, int contentTypeId) {
        this.userId = userId;
        this.itemId = itemId;
        this.contentTypeId = contentTypeId;
    }

    @Override
    public String toString() {
        return "Like{" +
                "likeId=" + likeId +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", contentTypeId=" + contentTypeId +
                '}';
    }

}
