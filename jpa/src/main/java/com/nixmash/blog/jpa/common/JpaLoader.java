package com.nixmash.blog.jpa.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiringInspection")
public class JpaLoader implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void run(String... args) throws Exception {

        String activeProfile = environment.getActiveProfiles()[0];
        log.info(String.format("Current JPA Active Profile: %s", activeProfile));

        String applicationVersion = environment.getProperty("nixmash.blog.jpa.version");
        log.info(String.format("NixMash Spring JPA Application Version: %s", applicationVersion));

        log.debug("Using Cache Manager: " + this.cacheManager.getClass().getName());
    }
}
