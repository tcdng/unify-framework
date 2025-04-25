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
package com.tcdng.unify.core.stream;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Data object for tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
@JacksonXmlRootElement(localName = "book")
@JsonPropertyOrder({ "title", "price", "quantity" })
public class Book {

	@JacksonXmlProperty
    private String title;

	@JacksonXmlProperty(isAttribute = true)
    private String genre;

	@JacksonXmlProperty
    private BigDecimal price;

	@JacksonXmlProperty
    private int quantity;

    public Book(String title, String genre, BigDecimal price, int quantity) {
        this.title = title;
        this.genre = genre;
        this.price = price;
        this.quantity = quantity;
    }

    public Book() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
