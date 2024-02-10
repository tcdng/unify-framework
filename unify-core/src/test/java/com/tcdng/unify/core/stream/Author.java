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
package com.tcdng.unify.core.stream;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Data object for tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
@XmlRootElement
@XmlType(propOrder = { "fullName", "books", "picture", "account" })
public class Author {

    private String fullName;

    private List<Book> books;

    private byte[] picture;

    private List<String> account;

    public Author(String fullName, List<Book> books, byte[] picture) {
        this.fullName = fullName;
        this.books = books;
        this.picture = picture;
    }

    public Author() {

    }

    public String getFullName() {
        return fullName;
    }

    @XmlElement
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Book> getBooks() {
        return books;
    }

    @XmlElement
    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public byte[] getPicture() {
        return picture;
    }

    @XmlElement
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public List<String> getAccount() {
        return account;
    }

    @XmlElement
    public void setAccount(List<String> account) {
        this.account = account;
    }
}
