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
package com.nixmash.blog.solr.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = -2572547753224433591L;

    private String id;
    private String name;
    private List<String> categories;
    private List<String> features;
    private Float weight;
    private Float price;
    private Integer popularity;
    private boolean available;
    private String doctype;
    private Point point;
    private String location;


    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", categories=" + categories + ", features =" + features + ", weight=" + weight + ", price="
                + price + ", popularity=" + popularity + ", available=" + available + ", doctype=" + doctype + "]";
    }


}
