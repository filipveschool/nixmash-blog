package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "tags")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getTagCloud",
                query = "select count(*) as `tagCount`, t.tag_value, t.tag_id from tags t " +
                        " inner join post_tag_ids pt on t.tag_id = pt.tag_id " +
                        " inner join posts p on pt.post_id = p.post_id " +
                        " where p.is_published = true " +
                        "group by t.tag_value order by tagCount DESC;",
                resultClass = Tag.class)
})
public class Tag implements Serializable {

    private static final long serialVersionUID = -5531381747015731447L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    @Basic
    @Column(name = "tag_value", nullable = false, length = 50)
    private String tagValue;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;

    @Transient
    private int tagCount = 0;

    public Tag() {
    }

    public Tag(String tagValue) {
        this.tagValue = tagValue;
    }

    public Tag(Long tagId, String tagValue) {
        this.tagId = tagId;
        this.tagValue = tagValue;
    }

    @Override
    public String toString() {
        return getTagValue();
    }

    public void update(final String tagValue) {
        this.tagValue = tagValue;
    }

    public static Builder getBuilder(Long tagId, String tagValue) {
        return new Builder(tagId, tagValue);
    }

    public static class Builder {

        private Tag built;

        public Builder(Long tagId, String tagValue) {
            built = new Tag();
            built.tagId = tagId;
            built.tagValue = tagValue;
        }

        public Tag build() {
            return built;
        }
    }

}
