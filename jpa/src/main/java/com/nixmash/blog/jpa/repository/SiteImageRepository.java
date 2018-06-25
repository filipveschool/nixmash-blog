package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.SiteImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SiteImageRepository extends CrudRepository<SiteImage, Long> {

    SiteImage findBySiteImageId(Long id);

    SiteImage findByIsCurrentTrueAndDayOfYear(Integer dayOfYear);

    Collection<SiteImage> findAll();

    Collection<SiteImage> findByBannerImageTrueAndIsActiveTrue();

    Collection<SiteImage> findByBannerImageTrue();

}
