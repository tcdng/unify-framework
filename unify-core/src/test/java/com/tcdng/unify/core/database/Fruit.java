/*
 * Copyright 2018-2024 The Code Department.
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
 * Test fruit entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "FRUIT", uniqueConstraints = { @UniqueConstraint({ "name" }) })
public class Fruit extends AbstractTestVersionedTableEntity {

    @Column
    private String name;

    @Column
    private String color;

    @Column(nullable = true)
    private Double price;

    @Column(nullable = true)
    private Integer quantity;

    public Fruit(String name, String color, Double price) {
        this.name = name;
        this.color = color;
        this.price = price;
        this.quantity = Integer.valueOf(0);
    }

    public Fruit(String name, String color, Double price, Integer quantity) {
        this.name = name;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    public Fruit() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
