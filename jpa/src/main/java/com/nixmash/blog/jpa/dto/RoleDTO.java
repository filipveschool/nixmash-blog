package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.model.Authority;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class RoleDTO {

    private Long id;

    private boolean isLocked = false;

    @NotEmpty
    @Length(min = Authority.MIN_LENGTH_AUTHORITY, max = Authority.MAX_LENGTH_AUTHORITY)
    private String authority;

    public RoleDTO() {
    }


    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", isLocked=" + isLocked +
                ", authority='" + authority + '\'' +
                '}';
    }
}
