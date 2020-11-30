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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.PrintFormat;

/**
 * CalendarUtils tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DataUtilsTest {

    private List<Book> bookList;

    @Before
    public void setup() throws Exception {
        bookList = new ArrayList<Book>();
        bookList.add(new Book("Saladin", BigDecimal.valueOf(10.0), 20, false));
        bookList.add(new Book("Paladin", BigDecimal.valueOf(40.0), 5, true));
        bookList.add(new Book("Aladin", BigDecimal.valueOf(30.0), 15, false));
        bookList.add(new Book("Maladin", BigDecimal.valueOf(20.0), 1, false));
    }

    @Test
    public void testUnmodifiableValuesListNull() throws Exception {
        List<String> list = DataUtils.unmodifiableValuesList(null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testUnmodifiableValuesList() throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("One", "Tom Hanks");
        map.put("Two", "Samuel L. Jackson");
        
        List<String> list = DataUtils.unmodifiableValuesList(map);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("Tom Hanks", list.get(0));
        assertEquals("Samuel L. Jackson", list.get(1));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCollectionConvertFromArray() throws Exception {
        List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class, new String[] { "240", "72" }, null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(240L), result.get(0));
        assertEquals(Long.valueOf(72L), result.get(1));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCollectionConvertFromString() throws Exception {
        List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class, "240,72", null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(240L), result.get(0));
        assertEquals(Long.valueOf(72L), result.get(1));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCollectionFromString() throws Exception {
        List<String> result = (List<String>) DataUtils.convert(List.class, String.class, "240,72", null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("240", result.get(0));
        assertEquals("72", result.get(1));
    }
    
    @Test
    public void testConvertArrayToArray() throws Exception {
        Long[] result = DataUtils.convert(Long[].class, new String[] { "240", "72" }, null);
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(Long.valueOf(240L), result[0]);
        assertEquals(Long.valueOf(72L), result[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCollectionConvertFromCollection() throws Exception {
        List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class,
                Arrays.asList(new String[] { "240", "72" }), null);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(240L), result.get(0));
        assertEquals(Long.valueOf(72L), result.get(1));
    }

    @Test
    public void testSetBeanProperty() throws Exception {
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);
        DataUtils.setBeanProperty(customer, "firstName", "Claus");
        DataUtils.setBeanProperty(customer, "id", Long.valueOf(10));
        DataUtils.setNestedBeanProperty(customer, "address.addressLine1", "24 Parklane");
        assertEquals("Claus", customer.getFirstName());
        assertEquals(Long.valueOf(10L), customer.getId());
        assertEquals("24 Parklane", address.getAddressLine1());

        // Lets set nulls here
        DataUtils.setBeanProperty(customer, "firstName", null);
        DataUtils.setBeanProperty(customer, "id", null);
        DataUtils.setNestedBeanProperty(customer, "address.addressLine1", null);
        assertNull(customer.getFirstName());
        assertNull(customer.getId());
        assertNull(address.getAddressLine1());

        // Test set assignable property. customer.setAddress(new
        // ContactAddress());
        ContactAddress contactAddress = new ContactAddress();
        DataUtils.setBeanProperty(customer, "address", contactAddress);
        DataUtils.setNestedBeanProperty(customer, "address.addressLine2", "38 Warehouse Road");
        assertEquals("38 Warehouse Road", contactAddress.getAddressLine2());
    }

    @Test
    public void testSetBeanPropertyWithConversion() throws Exception {
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddress(address);

        // Test Integer value to Integer array
        DataUtils.setBeanProperty(customer, "orders", Integer.valueOf(35), null);
        assertNotNull(customer.getOrders());
        assertEquals(1, customer.getOrders().length);
        assertSame(35, customer.getOrders()[0]);

        // Test String value to Integer array
        DataUtils.setBeanProperty(customer, "orders", "22", null);
        assertNotNull(customer.getOrders());
        assertEquals(1, customer.getOrders().length);
        assertSame(22, customer.getOrders()[0]);

        // Test Integer array to Integer array
        Integer[] integerValues = { Integer.valueOf(72), Integer.valueOf(89), Integer.valueOf(11) };
        DataUtils.setBeanProperty(customer, "orders", integerValues, null);
        assertNotNull(customer.getOrders());
        assertEquals(3, customer.getOrders().length);
        assertEquals(72, customer.getOrders()[0].intValue());
        assertEquals(89, customer.getOrders()[1].intValue());
        assertEquals(11, customer.getOrders()[2].intValue());

        // Test String array to Integer array
        String[] stringValues = { "1000", "450000", "2", "300" };
        DataUtils.setBeanProperty(customer, "orders", stringValues, null);
        assertNotNull(customer.getOrders());
        assertEquals(4, customer.getOrders().length);
        assertEquals(1000, customer.getOrders()[0].intValue());
        assertEquals(450000, customer.getOrders()[1].intValue());
        assertEquals(2, customer.getOrders()[2].intValue());
        assertEquals(300, customer.getOrders()[3].intValue());

        // Test int array to Integer array
        int[] intValues = { 12, 0, -4 };
        DataUtils.setBeanProperty(customer, "orders", intValues, null);
        assertNotNull(customer.getOrders());
        assertEquals(3, customer.getOrders().length);
        assertEquals(12, customer.getOrders()[0].intValue());
        assertEquals(0, customer.getOrders()[1].intValue());
        assertEquals(-4, customer.getOrders()[2].intValue());

        // Test Integer collection to Integer array
        Collection<Integer> integerCollection = new ArrayList<Integer>();
        integerCollection.add(Integer.valueOf(2020));
        integerCollection.add(Integer.valueOf(-56789));
        integerCollection.add(Integer.valueOf(11));
        integerCollection.add(Integer.valueOf(43434343));
        integerCollection.add(Integer.valueOf(-0000001));
        DataUtils.setBeanProperty(customer, "orders", integerCollection, null);
        assertNotNull(customer.getOrders());
        assertEquals(5, customer.getOrders().length);
        assertEquals(2020, customer.getOrders()[0].intValue());
        assertEquals(-56789, customer.getOrders()[1].intValue());
        assertEquals(11, customer.getOrders()[2].intValue());
        assertEquals(43434343, customer.getOrders()[3].intValue());
        assertEquals(-1, customer.getOrders()[4].intValue());

        // Test String collection to Integer array
        Collection<String> stringCollection = new ArrayList<String>();
        stringCollection.add("16");
        stringCollection.add("100");
        stringCollection.add("-5");
        DataUtils.setBeanProperty(customer, "orders", stringCollection, null);
        assertNotNull(customer.getOrders());
        assertEquals(3, customer.getOrders().length);
        assertEquals(16, customer.getOrders()[0].intValue());
        assertEquals(100, customer.getOrders()[1].intValue());
        assertEquals(-5, customer.getOrders()[2].intValue());
    }

    @Test
    public void testSortAscending() throws Exception {
        DataUtils.sortAscending(bookList, Book.class, "author");
        assertEquals("Aladin", bookList.get(0).getAuthor());
        assertEquals("Maladin", bookList.get(1).getAuthor());
        assertEquals("Paladin", bookList.get(2).getAuthor());
        assertEquals("Saladin", bookList.get(3).getAuthor());

        DataUtils.sortAscending(bookList, Book.class, "price");
        assertEquals("Saladin", bookList.get(0).getAuthor());
        assertEquals("Maladin", bookList.get(1).getAuthor());
        assertEquals("Aladin", bookList.get(2).getAuthor());
        assertEquals("Paladin", bookList.get(3).getAuthor());

        DataUtils.sortAscending(bookList, Book.class, "copies");
        assertEquals("Maladin", bookList.get(0).getAuthor());
        assertEquals("Paladin", bookList.get(1).getAuthor());
        assertEquals("Aladin", bookList.get(2).getAuthor());
        assertEquals("Saladin", bookList.get(3).getAuthor());

        DataUtils.sortAscending(bookList, Book.class, "censored");
        assertEquals("Paladin", bookList.get(3).getAuthor());
    }

    @Test
    public void testSortDescending() throws Exception {
        DataUtils.sortDescending(bookList, Book.class, "author");
        assertEquals("Saladin", bookList.get(0).getAuthor());
        assertEquals("Paladin", bookList.get(1).getAuthor());
        assertEquals("Maladin", bookList.get(2).getAuthor());
        assertEquals("Aladin", bookList.get(3).getAuthor());

        DataUtils.sortDescending(bookList, Book.class, "price");
        assertEquals("Paladin", bookList.get(0).getAuthor());
        assertEquals("Aladin", bookList.get(1).getAuthor());
        assertEquals("Maladin", bookList.get(2).getAuthor());
        assertEquals("Saladin", bookList.get(3).getAuthor());

        DataUtils.sortDescending(bookList, Book.class, "copies");
        assertEquals("Saladin", bookList.get(0).getAuthor());
        assertEquals("Aladin", bookList.get(1).getAuthor());
        assertEquals("Paladin", bookList.get(2).getAuthor());
        assertEquals("Maladin", bookList.get(3).getAuthor());

        DataUtils.sortDescending(bookList, Book.class, "censored");
        assertEquals("Paladin", bookList.get(0).getAuthor());
    }

    @Test
    public void testReadEmptyJsonObject() throws Exception {
        String json = "{}";
        Book book = DataUtils.readJsonObject(Book.class, json);
        assertNotNull(book);
        assertNull(book.getAuthor());
        assertNull(book.getPrice());
        assertEquals(0, book.getCopies());
        assertFalse(book.isCensored());
        assertNull(book.getPriceHistory());
    }

    @Test
    public void testReadJsonObject() throws Exception {
        String json =
                "{\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}";
        Book book = DataUtils.readJsonObject(Book.class, json);
        assertNotNull(book);
        assertEquals("Bramer & Bramer", book.getAuthor());
        assertEquals(BigDecimal.valueOf(2.54), book.getPrice());
        assertEquals(20, book.getCopies());
        assertEquals(true, book.isCensored());
        Double[] priceHistory = book.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(2, priceHistory.length);
        assertEquals(Double.valueOf(2.35), priceHistory[0]);
        assertEquals(Double.valueOf(2.03), priceHistory[1]);
    }

    @Test
    public void testReadComplexJsonObject() throws Exception {
        String json =
                "{\"id\":1025,\"book\":{\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}}";
        InventoryEntry entry = DataUtils.readJsonObject(InventoryEntry.class, json);
        assertNotNull(entry);
        assertEquals(1025, entry.getId());

        Book book = entry.getBook();
        assertNotNull(book);
        assertEquals("Bramer & Bramer", book.getAuthor());
        assertEquals(BigDecimal.valueOf(2.54), book.getPrice());
        assertEquals(20, book.getCopies());
        assertEquals(true, book.isCensored());
        Double[] priceHistory = book.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(2, priceHistory.length);
        assertEquals(Double.valueOf(2.35), priceHistory[0]);
        assertEquals(Double.valueOf(2.03), priceHistory[1]);
    }

    @Test
    public void testReadMoreComplexJsonObject() throws Exception {
        String json =
                "{\"entries\":[{\"id\":1025,\"book\":{\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}},"
                        + "{\"id\":1025,\"book\":{\"author\":\"Tom Clancy\", \"price\":4.71, \"priceHistory\":[3.86], \"copies\":82, \"censored\":false}}]}";
        Inventory inventory = DataUtils.readJsonObject(Inventory.class, json);
        assertNotNull(inventory);
        InventoryEntry[] entries = inventory.getEntries();
        assertNotNull(entries);
        assertEquals(2, entries.length);

        InventoryEntry entry = entries[0];
        assertNotNull(entry);
        assertEquals(1025, entry.getId());

        Book book = entry.getBook();
        assertNotNull(book);
        assertEquals("Bramer & Bramer", book.getAuthor());
        assertEquals(BigDecimal.valueOf(2.54), book.getPrice());
        assertEquals(20, book.getCopies());
        assertEquals(true, book.isCensored());
        Double[] priceHistory = book.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(2, priceHistory.length);
        assertEquals(Double.valueOf(2.35), priceHistory[0]);
        assertEquals(Double.valueOf(2.03), priceHistory[1]);

        entry = entries[1];
        assertNotNull(entry);
        assertEquals(1025, entry.getId());

        book = entry.getBook();
        assertNotNull(book);
        assertEquals("Tom Clancy", book.getAuthor());
        assertEquals(BigDecimal.valueOf(4.71), book.getPrice());
        assertEquals(82, book.getCopies());
        assertEquals(false, book.isCensored());
        priceHistory = book.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(1, priceHistory.length);
        assertEquals(Double.valueOf(3.86), priceHistory[0]);
    }

    @Test
    public void testReadEmbeddedJsonObject() throws Exception {
        String json = "{\"title\":\"Smart Dog\", \"resource\": {\"format\":\"jpeg\", \"width\":64, \"height\":128}}";
        PictureAsset pictureAsset = DataUtils.readJsonObject(PictureAsset.class, json);
        assertNotNull(pictureAsset);
        assertEquals("Smart Dog", pictureAsset.getTitle());

        Picture picture = (Picture) pictureAsset.getResource();
        assertEquals("jpeg", picture.getFormat());
        assertEquals(64, picture.getWidth());
        assertEquals(128, picture.getHeight());
    }

    @Test(expected = UnifyException.class)
    public void testReadJsonObjectUnknownMember() throws Exception {
        String json =
                "{\"author\":\"Bramer & Bramer\", \"tax\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}";
        DataUtils.readJsonObject(Book.class, json);
    }

    @Test
    public void testWriteEmptyJsonObject() throws Exception {
        String json = DataUtils.writeJsonObject(new Inventory(), PrintFormat.NONE);
        assertNotNull(json);
        assertEquals("{\"entries\":null}", json);
    }

    @Test
    public void testWriteJsonObject() throws Exception {
        Book book = new Book("Saladin", BigDecimal.valueOf(10.0), 20, false);
        book.setPriceHistory(new Double[] { 8.32, 9.14 });
        String json = DataUtils.writeJsonObject(book, PrintFormat.NONE);
        assertNotNull(json);

        Book jsonBook = DataUtils.readJsonObject(Book.class, json);
        assertNotNull(jsonBook);
        assertEquals(book.getAuthor(), jsonBook.getAuthor());
        assertEquals(book.getPrice(), jsonBook.getPrice());
        assertEquals(book.getCopies(), jsonBook.getCopies());
        assertEquals(book.isCensored(), jsonBook.isCensored());

        Double[] priceHistory = jsonBook.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(book.getPriceHistory().length, priceHistory.length);
        assertEquals(book.getPriceHistory()[0], priceHistory[0]);
        assertEquals(book.getPriceHistory()[1], priceHistory[1]);
    }

    @Test
    public void testWriteComplexJsonObject() throws Exception {
        InventoryEntry entry = new InventoryEntry();
        entry.setId(1978);
        Book book = new Book("Paladin", BigDecimal.valueOf(11.04), 62, false);
        book.setPriceHistory(new Double[] { 12.45, 11.0, 11.22 });
        entry.setBook(book);

        String json = DataUtils.writeJsonObject(entry, PrintFormat.NONE);
        assertNotNull(json);

        InventoryEntry jsonEntry = DataUtils.readJsonObject(InventoryEntry.class, json);
        assertNotNull(jsonEntry);
        assertEquals(entry.getId(), jsonEntry.getId());

        Book jsonBook = jsonEntry.getBook();
        assertNotNull(jsonBook);
        assertEquals(book.getAuthor(), jsonBook.getAuthor());
        assertEquals(book.getPrice(), jsonBook.getPrice());
        assertEquals(book.getCopies(), jsonBook.getCopies());
        assertEquals(book.isCensored(), jsonBook.isCensored());

        Double[] priceHistory = jsonBook.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(book.getPriceHistory().length, priceHistory.length);
        assertEquals(book.getPriceHistory()[0], priceHistory[0]);
        assertEquals(book.getPriceHistory()[1], priceHistory[1]);
        assertEquals(book.getPriceHistory()[2], priceHistory[2]);
    }

    @Test
    public void testWriteMoreComplexJsonObject() throws Exception {
        InventoryEntry entry1 = new InventoryEntry();
        entry1.setId(1978);
        Book book1 = new Book("Paladin", BigDecimal.valueOf(11.04), 62, false);
        book1.setPriceHistory(new Double[] { 12.45, 11.0, 11.22 });
        entry1.setBook(book1);

        InventoryEntry entry2 = new InventoryEntry();
        entry2.setId(2017);
        Book book2 = new Book("Maladin", BigDecimal.valueOf(25.33), 34, true);
        book2.setPriceHistory(new Double[] { 22.41 });
        entry2.setBook(book2);

        Inventory inventory = new Inventory();
        inventory.setEntries(new InventoryEntry[] { entry1, entry2 });
        String json = DataUtils.writeJsonObject(inventory, PrintFormat.NONE);
        assertNotNull(json);

        Inventory jsonInventory = DataUtils.readJsonObject(Inventory.class, json);
        assertNotNull(jsonInventory);
        InventoryEntry[] entries = jsonInventory.getEntries();
        assertNotNull(entries);
        assertEquals(2, entries.length);

        InventoryEntry jsonEntry = entries[0];
        assertNotNull(jsonEntry);
        assertEquals(entry1.getId(), jsonEntry.getId());

        Book jsonBook = jsonEntry.getBook();
        assertNotNull(jsonBook);
        assertEquals(book1.getAuthor(), jsonBook.getAuthor());
        assertEquals(book1.getPrice(), jsonBook.getPrice());
        assertEquals(book1.getCopies(), jsonBook.getCopies());
        assertEquals(book1.isCensored(), jsonBook.isCensored());

        Double[] priceHistory = jsonBook.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(book1.getPriceHistory().length, priceHistory.length);
        assertEquals(book1.getPriceHistory()[0], priceHistory[0]);
        assertEquals(book1.getPriceHistory()[1], priceHistory[1]);
        assertEquals(book1.getPriceHistory()[2], priceHistory[2]);

        jsonEntry = entries[1];
        assertNotNull(jsonEntry);
        assertEquals(entry2.getId(), jsonEntry.getId());

        jsonBook = jsonEntry.getBook();
        assertNotNull(jsonBook);
        assertEquals(book2.getAuthor(), jsonBook.getAuthor());
        assertEquals(book2.getPrice(), jsonBook.getPrice());
        assertEquals(book2.getCopies(), jsonBook.getCopies());
        assertEquals(book2.isCensored(), jsonBook.isCensored());

        priceHistory = jsonBook.getPriceHistory();
        assertNotNull(priceHistory);
        assertEquals(book2.getPriceHistory().length, priceHistory.length);
        assertEquals(book2.getPriceHistory()[0], priceHistory[0]);
    }

    @Test
    public void testWriteEmbeddedJsonObject() throws Exception {
        PictureAsset pictureAsset = new PictureAsset();
        pictureAsset.setTitle("Bright Cat");
        ((Picture) pictureAsset.getResource()).setFormat("bmp");
        ((Picture) pictureAsset.getResource()).setWidth(200);
        ((Picture) pictureAsset.getResource()).setHeight(50);

        String json = DataUtils.writeJsonObject(pictureAsset, PrintFormat.NONE);
        assertNotNull(json);

        PictureAsset jsonPictureAsset = DataUtils.readJsonObject(PictureAsset.class, json);
        assertEquals(pictureAsset.getTitle(), jsonPictureAsset.getTitle());

        Picture jsonPicture = (Picture) jsonPictureAsset.getResource();
        assertEquals(((Picture) pictureAsset.getResource()).getFormat(), jsonPicture.getFormat());
        assertEquals(((Picture) pictureAsset.getResource()).getWidth(), jsonPicture.getWidth());
        assertEquals(((Picture) pictureAsset.getResource()).getHeight(), jsonPicture.getHeight());
    }

    @Test
    public void testUnmodifiableListCollection() throws Exception {
        List<String> list = DataUtils.unmodifiableList(new HashSet<String>(Arrays.asList("Red", "Green", "Blue")));
        assertNotNull(list);
        assertEquals(3, list.size());
        assertTrue(list.contains("Red"));
        assertTrue(list.contains("Green"));
        assertTrue(list.contains("Blue"));
    }
    
    @Test
    public void testToArrayNull() throws Exception {
        assertNull(DataUtils.toArray(String.class, null));
    }
    
    @Test
    public void testToArray() throws Exception {
        String[] arr = DataUtils.toArray(String.class, Arrays.asList("Red", "Green", "Blue"));
        assertNotNull(arr);
        assertEquals(3, arr.length);
        assertEquals("Red", arr[0]);
        assertEquals("Green", arr[1]);
        assertEquals("Blue", arr[2]);
    }
    
    @Test
    public void testConvertToArrayFromCommaString() throws Exception {
        String[] arr = DataUtils.convert(String[].class, "Red,Green,Blue", null);
        assertNotNull(arr);
        assertEquals(3, arr.length);
        assertEquals("Red", arr[0]);
        assertEquals("Green", arr[1]);
        assertEquals("Blue", arr[2]);
    }
    
    @Test
    public void testConvertToStringFromArray() throws Exception {
        String val = DataUtils.convert(String.class, new String[] {"Red","Green","Blue"}, null);
        assertEquals("Red,Green,Blue", val);
    }
    
    public static abstract class Asset {

        private String title;

        private Object resource;

        public Asset(Object resource) {
            this.resource = resource;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getResource() {
            return resource;
        }
    }

    public static class PictureAsset extends Asset {

        public PictureAsset() {
            super(new Picture());
        }
    }

    public static class Picture {

        private String format;

        private int width;

        private int height;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class Inventory {
        private InventoryEntry[] entries;

        public InventoryEntry[] getEntries() {
            return entries;
        }

        public void setEntries(InventoryEntry[] entries) {
            this.entries = entries;
        }
    }

    public static class InventoryEntry {

        private int id;

        private Book book;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }
    }

    public static class Book {
        private String author;

        private BigDecimal price;

        private int copies;

        private boolean censored;

        private Double[] priceHistory;

        public Book(String author, BigDecimal price, int copies, boolean censored) {
            this.author = author;
            this.price = price;
            this.copies = copies;
            this.censored = censored;
        }

        public Book() {

        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public int getCopies() {
            return copies;
        }

        public void setCopies(int copies) {
            this.copies = copies;
        }

        public boolean isCensored() {
            return censored;
        }

        public void setCensored(boolean censored) {
            this.censored = censored;
        }

        public Double[] getPriceHistory() {
            return priceHistory;
        }

        public void setPriceHistory(Double[] priceHistory) {
            this.priceHistory = priceHistory;
        }
    }
}
