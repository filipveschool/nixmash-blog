/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nixmash.blog.solr.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.geo.Point;
import org.springframework.data.solr.core.geo.GeoConverters;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@SolrDocument(solrCoreName = "nixmash")
public class Product implements Serializable, IProduct {

    private static final long serialVersionUID = -1636370516058761621L;

    @Field(ID_FIELD)
    private String id;

    @Field(NAME_FIELD)
    private String name;

    @Field(FEATURE_FIELD)
    private List<String> features;

    @Field(CATEGORY_FIELD)
    private List<String> categories;

    @Field(WEIGHT_FIELD)
    private Float weight;

    @Field(PRICE_FIELD)
    private Float price;

    @Field(POPULARITY_FIELD)
    private Integer popularity;

    @Field(AVAILABLE_FIELD)
    private boolean available;

    @Field(DOCTYPE_FIELD)
    private String doctype;

    @Field(LOCATION_FIELD)
    private String location;

    private Point point;

    public Product() {
    }

    public Product(String name) {
        setName(name);
    }

    public Product(String id, String name) {
        setId(id);
        setName(name);
    }

    public Point getPoint() {
        String _location = this.getLocation();
        if (this.getLocation() == null)
            _location = "-1,-1";
        return GeoConverters.StringToPointConverter.INSTANCE.convert(_location);
    }

    public boolean hasCategories() {
        return (this.categories != null);
    }

    public boolean hasFeatures() {
        return (this.features != null);
    }

    public boolean hasLocation() {
        return (this.getLocation() != null);
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", categories=" + categories + ", features=" + features + ", weight=" + weight + ", price="
                + price + ", popularity=" + popularity + ", available=" + available + ", doctype=" + doctype + "]";
    }

}
