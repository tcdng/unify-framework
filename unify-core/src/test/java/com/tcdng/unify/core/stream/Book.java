/*
 * Copyright 2014 The Code Department
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Data object for tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@XmlRootElement
@XmlType(propOrder = { "title", "price", "quantity" })
public class Book {

	private String title;

	private String genre;

	private BigDecimal price;

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

	@XmlElement
	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	@XmlAttribute
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public BigDecimal getPrice() {
		return price;
	}

	@XmlElement
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	@XmlElement
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
