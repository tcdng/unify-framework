/*
 * Copyright 2018 The Code Department
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Jackson JSON object streaming manager implementation test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class JSONObjectStreamerTest extends AbstractUnifyComponentTest {

    private static final String BOOK_JSON = "{" + "\"title\":\"C++ for Engineers\"," + "\"genre\":\"Science\","
            + "\"price\":25.2," + "\"quantity\":10" + "}";

    @Test
    public void testReadObjectFromInputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Book book = josm.unmarshal(Book.class, new ByteArrayInputStream(BOOK_JSON.getBytes()), null);
        assertNotNull(book);
        assertEquals("Science", book.getGenre());
        assertEquals("C++ for Engineers", book.getTitle());
        assertEquals(BigDecimal.valueOf(25.20), book.getPrice());
        assertEquals(10, book.getQuantity());
    }

    @Test
    public void testReadObjectFromReader() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Book book = josm.unmarshal(Book.class, new StringReader(BOOK_JSON));
        assertNotNull(book);
        assertEquals("Science", book.getGenre());
        assertEquals("C++ for Engineers", book.getTitle());
        assertEquals(BigDecimal.valueOf(25.20), book.getPrice());
        assertEquals(10, book.getQuantity());
    }

    @Test
    public void testWriteObjectToOutputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Book book = new Book("C++ for Engineers", "Science", BigDecimal.valueOf(25.20), 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        josm.marshal(book, baos, null);
        Book jsonBook = josm.unmarshal(Book.class, new String(baos.toByteArray()));
        assertEquals(book.getGenre(), jsonBook.getGenre());
        assertEquals(book.getPrice(), jsonBook.getPrice());
        assertEquals(book.getQuantity(), jsonBook.getQuantity());
        assertEquals(book.getTitle(), jsonBook.getTitle());
    }

    @Test
    public void testWriteObjectToWriter() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Book book = new Book("C++ for Engineers", "Science", BigDecimal.valueOf(25.20), 10);
        StringWriter writer = new StringWriter();
        josm.marshal(book, writer);
        Book jsonBook = josm.unmarshal(Book.class, writer.toString());
        assertEquals(book.getGenre(), jsonBook.getGenre());
        assertEquals(book.getPrice(), jsonBook.getPrice());
        assertEquals(book.getQuantity(), jsonBook.getQuantity());
        assertEquals(book.getTitle(), jsonBook.getTitle());
    }

    @Test
    public void testWriteByteArrayObjectToOutputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Author author = new Author("Bramer & Bramer", null,
                new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD });
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        josm.marshal(author, baos, null);
    }

    @Test
    public void testReadByteArrayObjectToOutputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Author author = new Author("Bramer & Bramer", null,
                new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD });
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        josm.marshal(author, baos, null);
        Author unAuthor = josm.unmarshal(Author.class, new ByteArrayInputStream(baos.toByteArray()), null);
        byte[] picture = unAuthor.getPicture();
        assertNotNull(picture);
        assertEquals(4, picture.length);
        assertEquals((byte) 0xAA, picture[0]);
        assertEquals((byte) 0xBB, picture[1]);
        assertEquals((byte) 0xCC, picture[2]);
        assertEquals((byte) 0xDD, picture[3]);
    }

    @Test
    public void testWriteListObjectToOutputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Author author = new Author("Bramer & Bramer", null,
                new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD });
        List<Book> books = Arrays.asList(new Book("C++ for Engineers", "Science", BigDecimal.valueOf(25.20), 10),
                new Book("Programing in Pascal", "Science", BigDecimal.valueOf(15.20), 20));
        List<String> account = Arrays.asList("0123456789");
        author.setBooks(books);
        author.setAccount(account);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        josm.marshal(author, baos, null);
    }

    @Test
    public void testReadListObjectToOutputStream() throws Exception {
        JSONObjectStreamer josm = (JSONObjectStreamer) this
                .getComponent(ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER);
        Author author = new Author("Bramer & Bramer", null,
                new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD });
        List<Book> books = Arrays.asList(new Book("C++ for Engineers", "Science", BigDecimal.valueOf(25.20), 10),
                new Book("Programing in Pascal", "Science", BigDecimal.valueOf(15.20), 20));
        List<String> account = Arrays.asList("0123456789");
        author.setBooks(books);
        author.setAccount(account);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        josm.marshal(author, baos, null);
        Author unAuthor = josm.unmarshal(Author.class, new ByteArrayInputStream(baos.toByteArray()), null);
        byte[] picture = unAuthor.getPicture();
        assertNotNull(picture);
        assertEquals(4, picture.length);
        assertEquals((byte) 0xAA, picture[0]);
        assertEquals((byte) 0xBB, picture[1]);
        assertEquals((byte) 0xCC, picture[2]);
        assertEquals((byte) 0xDD, picture[3]);

        books = unAuthor.getBooks();
        assertNotNull(books);
        assertEquals(2, books.size());

        Book book = books.get(0);
        assertNotNull(book);
        assertEquals("Science", book.getGenre());
        assertEquals("C++ for Engineers", book.getTitle());
        assertEquals(BigDecimal.valueOf(25.20), book.getPrice());
        assertEquals(10, book.getQuantity());

        book = books.get(1);
        assertNotNull(book);
        assertEquals("Science", book.getGenre());
        assertEquals("Programing in Pascal", book.getTitle());
        assertEquals(BigDecimal.valueOf(15.20), book.getPrice());
        assertEquals(20, book.getQuantity());

        account = unAuthor.getAccount();
        assertNotNull(account);
        assertEquals(1, account.size());
        assertEquals("0123456789", account.get(0));
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
