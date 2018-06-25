package com.nixmash.blog.jpa.dto;

import com.nixmash.blog.jpa.model.SiteOption;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class SiteOptionDTO {

    private Long optionId;

    @NotEmpty
    @Length(max = SiteOption.MAX_LENGTH_PROPERTYNAME)
    private String name;

    private String value;

    public SiteOptionDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public SiteOptionDTO() {
    }

    // region toString()

    @Override
    public String toString() {
        return "SiteOptionDTO{" +
                "optionId=" + optionId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    // endregion

    public static Builder with(String name, Object value) {
        return new SiteOptionDTO.Builder(name, value);
    }

    public static class Builder {

        private SiteOptionDTO built;

        public Builder(String name, Object value) {
            built = new SiteOptionDTO();
            built.setName(name);
            if (value != null)
                built.setValue(String.valueOf(value));
        }

        public SiteOptionDTO build() {
            return built;
        }
    }
}
