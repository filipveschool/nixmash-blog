package com.nixmash.blog.mvc.containers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategory implements java.io.Serializable {

    private static final long serialVersionUID = 5580569251842603417L;

    public ProductCategory() {
    }

    public ProductCategory(String category, int productCount) {
        super();
        this.category = category;
        this.productCount = productCount;
    }

    private String category;
    private int productCount;

}

