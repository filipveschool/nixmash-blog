package com.nixmash.blog.solr.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
// @PropertySource("file:${solr.properties.file.path}${solr.properties.file.basename}.properties")
@PropertySource("classpath:/solr.properties")
@ConfigurationProperties(prefix = "solr")
public class SolrSettings {

    private String solrServerUrl;
    private String solrEmbeddedPath;
    private String solrCoreName;


}
