package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "site_options")
public class SiteOption implements Serializable {

    private static final long serialVersionUID = 6690621866489266673L;

    public static final int MAX_LENGTH_PROPERTYNAME = 50;

    @Id
    @Column(name = "option_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(name = "option_name")
    @NotNull
    private String name;

    @Column(name = "option_value", columnDefinition = "TEXT")
    private String value;

    public SiteOption(){

    }

    public void update(final String optionName, final String optionValue) {
        this.name = optionName;
        this.value = optionValue;
    }
}
