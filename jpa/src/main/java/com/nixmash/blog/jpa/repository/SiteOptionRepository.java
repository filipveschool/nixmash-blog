package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.SiteOption;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SiteOptionRepository extends CrudRepository<SiteOption, Long> {

    SiteOption findByNameIgnoreCase(String optionName) throws DataAccessException;

    Collection<SiteOption> findAll() throws DataAccessException;

    SiteOption save(SiteOption siteOption) throws DataAccessException;

}