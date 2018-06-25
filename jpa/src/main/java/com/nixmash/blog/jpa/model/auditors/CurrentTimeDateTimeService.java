package com.nixmash.blog.jpa.model.auditors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

/**
 * This class returns the current time.
 *
 * From Petri Kainulainen's JPA Examples Project on GitHub
 *
 * spring-data-jpa-examples/query-methods/
 * https://goo.gl/lY7sT5
 *
 */
@Slf4j
public class CurrentTimeDateTimeService implements DateTimeService {

    @Override
    public ZonedDateTime getCurrentDateAndTime() {
        ZonedDateTime currentDateAndTime =  ZonedDateTime.now();
        return currentDateAndTime;
    }
}
