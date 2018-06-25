package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.SiteOption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SiteOptionRepository extends CrudRepository<SiteOption, Long> {

    SiteOption findByNameIgnoreCase(String optionName);

    Collection<SiteOption> findAll();

    SiteOption save(SiteOption siteOption);

}