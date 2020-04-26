/*
 * Copyright 2018-2020 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tcdng.unify.core.filter;

/**
 * Test product class;
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Product {

    private String name;

    private String description;

    private Double costPrice;

    private Double salesPrice;


    public Product(String name, String description, Double costPrice, Double salesPrice) {
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.salesPrice = salesPrice;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public Double getCostPrice() {
        return costPrice;
    }


    public Double getSalesPrice() {
        return salesPrice;
    }

}
