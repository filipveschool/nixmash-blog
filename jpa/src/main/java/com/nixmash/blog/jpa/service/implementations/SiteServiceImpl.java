package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.common.SiteOptions;
import com.nixmash.blog.jpa.dto.SiteOptionDTO;
import com.nixmash.blog.jpa.exceptions.SiteOptionNotFoundException;
import com.nixmash.blog.jpa.model.SiteImage;
import com.nixmash.blog.jpa.model.SiteOption;
import com.nixmash.blog.jpa.repository.SiteImageRepository;
import com.nixmash.blog.jpa.repository.SiteOptionRepository;
import com.nixmash.blog.jpa.service.interfaces.SiteService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service("siteService")
@Transactional
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteOptionRepository siteOptionRepository;

    @Autowired
    private SiteOptions siteOptions;

    @Autowired
    private SiteImageRepository siteImageRepository;

    // region SiteImages

    /**
     * <p>Retrieves Home Page Banner based on random selection from Active Banners
     * in table <strong>site_images</strong>.</p>
     *
     * <p>Used when number of active banners is greater than the number of days in the month.</p>
     *
     * @return SiteImage object
     */
    @Transactional
    @Override
    public SiteImage getHomeBanner() {
        int dayOfYear = DateTime.now().dayOfYear().get();

        SiteImage siteImage = siteImageRepository.findByIsCurrentTrueAndDayOfYear(dayOfYear);
        if (siteImage == null) {
            resetCurrentSiteImage(dayOfYear);
            siteImage = getNewCurrentSiteImage();
        }
        return siteImage;
    }

    private SiteImage getNewCurrentSiteImage() {
        Collection<SiteImage> siteImages = siteImageRepository.findByBannerImageTrueAndIsActiveTrue();
        int activeBannerCount = siteImages.size();
        int randomNum = ThreadLocalRandom.current().nextInt(0, activeBannerCount);
        SiteImage siteImage = (SiteImage) siteImages.toArray()[randomNum];
        siteImage.setIsCurrent(true);
        siteImageRepository.save(siteImage);
        return siteImage;
    }

    private void resetCurrentSiteImage(int dayOfYear) {
        Collection<SiteImage> all = siteImageRepository.findAll();
        all.forEach(a -> {
            a.setDayOfYear(dayOfYear);
            a.setIsCurrent(false);
        });
        siteImageRepository.save(all);
    }

    /**
     * Used for viewing specific banners in development and client review
     * Mapped to: /dev/banner?id=siteImageId
     *
     * @param siteImageId site_image_id of SiteImage record
     * @return SiteImage
     */
    @Transactional
    @Override
    public SiteImage getHomeBanner(long siteImageId) {
        return siteImageRepository.findBySiteImageId(siteImageId);
    }

    // endregion


    // region SiteOptions

    @Override
    public SiteOption update(SiteOptionDTO siteOptionDTO) throws SiteOptionNotFoundException {
        log.debug("Updating siteOption property {} with value: {}",
                siteOptionDTO.getName(), siteOptionDTO.getValue());

        SiteOption found = findOptionByName(siteOptionDTO.getName());
        found.update(siteOptionDTO.getName(), siteOptionDTO.getValue());

        try {
            siteOptions.setSiteOptionProperty(siteOptionDTO.getName(), siteOptionDTO.getValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error updating SiteOption Properties " + e.getMessage());
        }
        return found;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteOption findOptionByName(String name) throws SiteOptionNotFoundException {

        log.debug("Finding siteOption property with name: {}", name);
        SiteOption found = siteOptionRepository.findByNameIgnoreCase(name);

        if (found == null) {
            log.debug("No siteOption property with name: {}", name);
            throw new SiteOptionNotFoundException("No siteOption with property name: " + name);
        }

        return found;
    }

    // endregion

    // region Utility Methods and Sorts

    public Sort sortBySiteImageIdAsc() {
        return new Sort(Sort.Direction.ASC, "siteImageId");
    }

    // endregion
}



