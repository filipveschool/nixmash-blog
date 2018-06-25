package com.nixmash.blog.jpa;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.config.ApplicationConfig;
import com.nixmash.blog.jpa.enums.DataConfigProfile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
@ActiveProfiles(DataConfigProfile.H2)
public class SpringDataTests {

	@Autowired
	ApplicationSettings applicationSettings;

}
