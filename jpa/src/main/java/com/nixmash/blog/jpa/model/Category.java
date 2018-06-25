package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "categories")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getCategoryCounts",
                query = "select count(*) as `categoryCount`, c.category_value, c.category_id, " +
                        "c.is_active, c.is_default from categories c " +
                        "inner join post_category_ids pc on c.category_id = pc.category_id " +
                        "inner join posts p on pc.post_id = p.post_id " +
                        " where p.is_published = true " +
                        "group by c.category_value order by categoryCount DESC;",
                resultClass = Category.class)
})
public class Category implements Serializable {

    private static final Long serialVersionUID = -5531381747015731447L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Basic
    @Column(name = "category_value", nullable = false, length = 50)
    private String categoryValue;

    @OneToMany
    @JoinTable(name = "post_category_ids",
            joinColumns = {@JoinColumn(name = "category_id",
                    referencedColumnName = "category_id")},
            inverseJoinColumns = {@JoinColumn(name = "post_id",
                    referencedColumnName = "post_id")})
    private Set<Post> posts;

    @Transient
    private int categoryCount = 0;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public Category() {
    }

    public Category(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public Category(Long categoryId, String categoryValue, Boolean isActive, Boolean isDefault) {
        this.categoryId = categoryId;
        this.categoryValue = categoryValue;
        this.isActive = isActive;
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return getCategoryValue();
    }

    public void update(String categoryValue, Boolean isActive, Boolean isDefault) {
        this.categoryValue = categoryValue;
        this.isActive = isActive;
        this.isDefault = isDefault;
    }

    public void clearDefault() {
        this.isDefault = false;
    }

    public static Builder getBuilder(Long categoryId, String categoryValue) {
        return new Builder(categoryId, categoryValue);
    }

    public static class Builder {

        private Category built;

        public Builder(Long categoryId, String categoryValue) {
            built = new Category();
            built.categoryId = categoryId;
            built.categoryValue = categoryValue;
        }

        public Category build() {
            return built;
        }
    }

}
