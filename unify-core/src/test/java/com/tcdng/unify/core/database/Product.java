/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.database;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.Column;

/**
 * Test product entity.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Table(name = "TESTPRODUCT", uniqueConstraints = { @UniqueConstraint({ "name" }) })
public class Product extends AbstractTestVersionedTableEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Double costPrice;

    @Column(nullable = true)
    private Double salesPrice;


    public Product(String name, String description, Double costPrice, Double salesPrice) {
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.salesPrice = salesPrice;
    }

    public Product() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", description=" + description + ", costPrice=" + costPrice + ", salesPrice="
                + salesPrice + "]";
    }

}
