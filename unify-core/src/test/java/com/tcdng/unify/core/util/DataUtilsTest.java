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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.tcdng.unify.common.constants.StandardFormatType;
import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.criterion.Order;
import com.tcdng.unify.core.data.JsonFieldComposition;
import com.tcdng.unify.core.data.JsonObjectComposition;
import com.tcdng.unify.core.format.Formatter;

/**
 * CalendarUtils tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DataUtilsTest extends AbstractUnifyComponentTest {

	private Formatter<?> commaArrayFormatter;

	private Formatter<?> pipeArrayFormatter;

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
	public void testSplitA() throws Exception {
		List<List<Long>> list = DataUtils.split(Arrays.asList(1L,2L,3L,4L,5L,6L,7L), 3);
		assertNotNull(list);
		assertEquals(3, list.size());
		
		List<Long> _l = list.get(0);
		assertEquals(3, _l.size());
		assertEquals(Long.valueOf(1L), _l.get(0));
		assertEquals(Long.valueOf(2L), _l.get(1));
		assertEquals(Long.valueOf(3L), _l.get(2));
		
		_l = list.get(1);
		assertEquals(3, _l.size());
		assertEquals(Long.valueOf(4L), _l.get(0));
		assertEquals(Long.valueOf(5L), _l.get(1));
		assertEquals(Long.valueOf(6L), _l.get(2));
		
		_l = list.get(2);
		assertEquals(1, _l.size());
		assertEquals(Long.valueOf(7L), _l.get(0));		
	}

	@Test
	public void testSplitB() throws Exception {
		List<List<Long>> list = DataUtils.split(Arrays.asList(1L,2L,3L,4L,5L,6L), 3);
		assertNotNull(list);
		assertEquals(2, list.size());
		
		List<Long> _l = list.get(0);
		assertEquals(3, _l.size());
		assertEquals(Long.valueOf(1L), _l.get(0));
		assertEquals(Long.valueOf(2L), _l.get(1));
		assertEquals(Long.valueOf(3L), _l.get(2));
		
		_l = list.get(1);
		assertEquals(3, _l.size());
		assertEquals(Long.valueOf(4L), _l.get(0));
		assertEquals(Long.valueOf(5L), _l.get(1));
		assertEquals(Long.valueOf(6L), _l.get(2));
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
		List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class, new String[] { "240", "72" });
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(Long.valueOf(240L), result.get(0));
		assertEquals(Long.valueOf(72L), result.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionConvertFromString() throws Exception {
		List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class, "240,72");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(Long.valueOf(240L), result.get(0));
		assertEquals(Long.valueOf(72L), result.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionFromString() throws Exception {
		List<String> result = (List<String>) DataUtils.convert(List.class, String.class, "240,72");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("240", result.get(0));
		assertEquals("72", result.get(1));
	}

	@Test
	public void testConvertDateToString() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2021, 11, 25, 13, 26, 5);
		cal.set(Calendar.MILLISECOND, 0);
		Date date1 = cal.getTime();
		String conv = DataUtils.convert(String.class, date1);
		assertEquals("2021-12-25 13:26:05.000", conv);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertDateListToStringList() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2021, 11, 25, 13, 26, 5);
		cal.set(Calendar.MILLISECOND, 25);
		Date date1 = cal.getTime();
		cal = Calendar.getInstance();
		cal.set(2021, 10, 14, 10, 26, 14);
		cal.set(Calendar.MILLISECOND, 200);
		Date date2 = cal.getTime();

		List<String> dateStrList = DataUtils.convert(List.class, String.class, Arrays.asList(date1, date2));
		assertNotNull(dateStrList);
		assertEquals(2, dateStrList.size());
		assertEquals("2021-12-25 13:26:05.025", dateStrList.get(0));
		assertEquals("2021-11-14 10:26:14.200", dateStrList.get(1));
	}

	@Test
	public void testConvertStringToDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2021, 11, 25, 13, 26, 5);
		cal.set(Calendar.MILLISECOND, 25);
		Date date1 = cal.getTime();
		Date date10 = DataUtils.convert(Date.class, "2021-12-25 13:26:05.025");
		assertEquals(date1, date10);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertStringListToDateList() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2021, 11, 25, 13, 26, 5);
		cal.set(Calendar.MILLISECOND, 25);
		Date date1 = cal.getTime();
		cal = Calendar.getInstance();
		cal.set(2021, 10, 14, 10, 26, 14);
		cal.set(Calendar.MILLISECOND, 200);
		Date date2 = cal.getTime();

		List<Date> dateList = DataUtils.convert(List.class, Date.class,
				Arrays.asList("2021-12-25 13:26:05.025", "2021-11-14 10:26:14.200"));
		assertNotNull(dateList);
		assertEquals(date1, dateList.get(0));
		assertEquals(date2, dateList.get(1));
	}

	@Test
	public void testConvertArrayToArray() throws Exception {
		Long[] result = DataUtils.convert(Long[].class, new String[] { "240", "72" });
		assertNotNull(result);
		assertEquals(2, result.length);
		assertEquals(Long.valueOf(240L), result[0]);
		assertEquals(Long.valueOf(72L), result[1]);
	}

	@Test
	public void testConvertDateArrayToLongArray() throws Exception {
		Date date1 = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 2);
		Date date2 = cal.getTime();
		Long[] result = DataUtils.convert(Long[].class, new Date[] { date1, date2 });
		assertNotNull(result);
		assertEquals(2, result.length);
		assertFalse(result[0].equals(result[1]));

		Date[] dates = DataUtils.convert(Date[].class, result);
		assertNotNull(dates);
		assertEquals(2, dates.length);
		assertEquals(date1, dates[0]);
		assertEquals(date2, dates[1]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollectionConvertFromCollection() throws Exception {
		List<Long> result = (List<Long>) DataUtils.convert(List.class, Long.class,
				Arrays.asList(new String[] { "240", "72" }));
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
	public void testSortByOrderNull() throws Exception {
		DataUtils.sort(bookList, Book.class, null);
		assertEquals("Saladin", bookList.get(0).getAuthor());
		assertEquals("Paladin", bookList.get(1).getAuthor());
		assertEquals("Aladin", bookList.get(2).getAuthor());
		assertEquals("Maladin", bookList.get(3).getAuthor());
	}

	@Test
	public void testSortByOrderAscending() throws Exception {
		Order order = new Order().add("author");
		DataUtils.sort(bookList, Book.class, order);
		assertEquals("Aladin", bookList.get(0).getAuthor());
		assertEquals("Maladin", bookList.get(1).getAuthor());
		assertEquals("Paladin", bookList.get(2).getAuthor());
		assertEquals("Saladin", bookList.get(3).getAuthor());

		order = new Order().add("price");
		DataUtils.sort(bookList, Book.class, order);
		assertEquals("Saladin", bookList.get(0).getAuthor());
		assertEquals("Maladin", bookList.get(1).getAuthor());
		assertEquals("Aladin", bookList.get(2).getAuthor());
		assertEquals("Paladin", bookList.get(3).getAuthor());
	}

	@Test
	public void testSortByOrderDescending() throws Exception {
		Order order = new Order().add("author", OrderType.DESCENDING);
		DataUtils.sort(bookList, Book.class, order);
		assertEquals("Saladin", bookList.get(0).getAuthor());
		assertEquals("Paladin", bookList.get(1).getAuthor());
		assertEquals("Maladin", bookList.get(2).getAuthor());
		assertEquals("Aladin", bookList.get(3).getAuthor());

		order = new Order().add("price", OrderType.DESCENDING);
		DataUtils.sort(bookList, Book.class, order);
		assertEquals("Paladin", bookList.get(0).getAuthor());
		assertEquals("Aladin", bookList.get(1).getAuthor());
		assertEquals("Maladin", bookList.get(2).getAuthor());
		assertEquals("Saladin", bookList.get(3).getAuthor());
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
		Book book = DataUtils.fromJsonString(Book.class, json);
		assertNotNull(book);
		assertNull(book.getAuthor());
		assertNull(book.getPrice());
		assertEquals(0, book.getCopies());
		assertFalse(book.isCensored());
		assertNull(book.getPriceHistory());
	}

	@Test
	public void testReadJsonObject() throws Exception {
		String json = "{\"order\":\"DESC\",\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}";
		Book book = DataUtils.fromJsonString(Book.class, json);
		assertNotNull(book);
		assertEquals(OrderType.DESCENDING, book.getOrder());
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
	public void testReadJsonArray() throws Exception {
		String json1 = "[\"10\", \"20\", \"30\"]";
		String[] arr1 = DataUtils.arrayFromJsonString(String[].class, json1);
		assertNotNull(arr1);
		assertEquals(3, arr1.length);
		assertEquals("10", arr1[0]);
		assertEquals("20", arr1[1]);
		assertEquals("30", arr1[2]);

		String json2 = "[10, 20, 30]";
		Integer[] arr2 = DataUtils.arrayFromJsonString(Integer[].class, json2);
		assertNotNull(arr2);
		assertEquals(3, arr2.length);
		assertEquals(Integer.valueOf(10), arr2[0]);
		assertEquals(Integer.valueOf(20), arr2[1]);
		assertEquals(Integer.valueOf(30), arr2[2]);
	}

	@Test
	public void testReadComplexJsonObject() throws Exception {
		String json = "{\"id\":1025,\"book\":{\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}}";
		InventoryEntry entry = DataUtils.fromJsonString(InventoryEntry.class, json);
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
		String json = "{\"entries\":[{\"id\":1025,\"book\":{\"author\":\"Bramer & Bramer\", \"price\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}},"
				+ "{\"id\":1025,\"book\":{\"author\":\"Tom Clancy\", \"price\":4.71, \"priceHistory\":[3.86], \"copies\":82, \"censored\":false}}]}";
		Inventory inventory = DataUtils.fromJsonString(Inventory.class, json);
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
		PictureAsset pictureAsset = DataUtils.fromJsonString(PictureAsset.class, json);
		assertNotNull(pictureAsset);
		assertEquals("Smart Dog", pictureAsset.getTitle());

		Picture picture = (Picture) pictureAsset.getResource();
		assertEquals("jpeg", picture.getFormat());
		assertEquals(64, picture.getWidth());
		assertEquals(128, picture.getHeight());
	}

//    @Test(expected = UnifyException.class)
//    public void testReadJsonObjectUnknownMember() throws Exception {
//        String json = "{\"author\":\"Bramer & Bramer\", \"tax\":2.54, \"priceHistory\":[2.35, 2.03], \"copies\":20, \"censored\":true}";
//        DataUtils.fromJsonString(Book.class, json);
//    }

	@Test
	public void testToEmptyJsonObjectString() throws Exception {
		String json = DataUtils.asJsonString(new Inventory(), PrintFormat.NONE);
		assertNotNull(json);
		assertEquals("{\"entries\":null}", json);
	}

	@Test
	public void testToJsonObjectString() throws Exception {
		Book book = new Book("Saladin", BigDecimal.valueOf(10.0), 20, false);
		book.setOrder(OrderType.ASCENDING);
		book.setPriceHistory(new Double[] { 8.32, 9.14 });
		book.setSignDt(new Date());
		String json = DataUtils.asJsonString(book, PrintFormat.NONE);
		assertNotNull(json);

		Book jsonBook = DataUtils.fromJsonString(Book.class, json);
		assertNotNull(jsonBook);
		assertEquals(OrderType.ASCENDING, jsonBook.getOrder());
		assertEquals(book.getAuthor(), jsonBook.getAuthor());
		assertEquals(book.getPrice(), jsonBook.getPrice());
		assertEquals(book.getCopies(), jsonBook.getCopies());
		assertEquals(book.isCensored(), jsonBook.isCensored());
		assertEquals(book.getSignDt(), jsonBook.getSignDt());

		Double[] priceHistory = jsonBook.getPriceHistory();
		assertNotNull(priceHistory);
		assertEquals(book.getPriceHistory().length, priceHistory.length);
		assertEquals(book.getPriceHistory()[0], priceHistory[0]);
		assertEquals(book.getPriceHistory()[1], priceHistory[1]);
	}

	@Test
	public void testToComplexJsonObjectString() throws Exception {
		InventoryEntry entry = new InventoryEntry();
		entry.setId(1978);
		Book book = new Book("Paladin", BigDecimal.valueOf(11.04), 62, false);
		book.setPriceHistory(new Double[] { 12.45, 11.0, 11.22 });
		entry.setBook(book);

		String json = DataUtils.asJsonString(entry, PrintFormat.NONE);
		assertNotNull(json);

		InventoryEntry jsonEntry = DataUtils.fromJsonString(InventoryEntry.class, json);
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
	public void testToMoreComplexJsonObjectString() throws Exception {
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
		String json = DataUtils.asJsonString(inventory, PrintFormat.NONE);
		assertNotNull(json);

		Inventory jsonInventory = DataUtils.fromJsonString(Inventory.class, json);
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
	public void testToJsonObjectStringJsonComposition() throws Exception {
		Doc doc = new Doc("crane", "Crane", 24);
		String json = DataUtils.asJsonString(docComposition, doc, PrintFormat.NONE);
		assertNotNull(json);
		assertEquals("{\"Name\":\"crane\",\"Title\":\"Crane\",\"Weight\":24}", json);
	}

	@Test
	public void testToJsonObjectStringJsonCompositionComplex() throws Exception {
		Owner owner = new Owner("Larry", "Barry", new SimpleDateFormat("dd-MM-yyyy").parse("27-01-1978"), new Doc[] { new Doc("crane", "Crane", 24)});
		String json = DataUtils.asJsonString(ownerComposition, owner, PrintFormat.NONE);
		assertNotNull(json);
		assertEquals("{\"firstName\":\"Larry\",\"lastName\":\"Barry\",\"dob\":\"27/01/1978\",\"documents\":[{\"Name\":\"crane\",\"Title\":\"Crane\",\"Weight\":24}]}", json);
	}

	@Test
	public void testJsonStringToObjectJsonComposition() throws Exception {
		Doc doc = DataUtils.fromJsonString(docComposition, Doc.class, "{\"Name\":\"crane\",\"Title\":\"Crane\",\"Weight\":24}");
		assertNotNull(doc);
		assertEquals("crane", doc.getName());
		assertEquals("Crane", doc.getTitle());
		assertEquals(24, doc.getWeight());
	}

	@Test
	public void testStringJsonToObjectJsonCompositionComplex() throws Exception {
		Owner owner = DataUtils.fromJsonString(ownerComposition, Owner.class,
				"{\"firstName\":\"Larry\",\"lastName\":\"Barry\",\"dob\":\"27/01/1978\",\"documents\":[{\"Name\":\"crane\",\"Title\":\"Crane\",\"Weight\":24}]}");
		assertNotNull(owner);
		assertEquals("Larry", owner.getFirstName());
		assertEquals("Barry", owner.getLastName());
		assertNotNull(owner.getDob());
		
		Doc[] docs = owner.getDocs();
		assertNotNull(docs);
		assertEquals(1, docs.length);
		assertEquals("crane", docs[0].getName());
		assertEquals("Crane", docs[0].getTitle());
		assertEquals(24, docs[0].getWeight());
	}

	@Test
	public void testToEmbeddedJsonObjectString() throws Exception {
		PictureAsset pictureAsset = new PictureAsset();
		pictureAsset.setTitle("Bright Cat");
		((Picture) pictureAsset.getResource()).setFormat("bmp");
		((Picture) pictureAsset.getResource()).setWidth(200);
		((Picture) pictureAsset.getResource()).setHeight(50);

		String json = DataUtils.asJsonString(pictureAsset, PrintFormat.NONE);
		assertNotNull(json);

		PictureAsset jsonPictureAsset = DataUtils.fromJsonString(PictureAsset.class, json);
		assertEquals(pictureAsset.getTitle(), jsonPictureAsset.getTitle());

		Picture jsonPicture = (Picture) jsonPictureAsset.getResource();
		assertEquals(((Picture) pictureAsset.getResource()).getFormat(), jsonPicture.getFormat());
		assertEquals(((Picture) pictureAsset.getResource()).getWidth(), jsonPicture.getWidth());
		assertEquals(((Picture) pictureAsset.getResource()).getHeight(), jsonPicture.getHeight());
	}

	@Test
	public void testToJsonArrayString() throws Exception {
		String json1 = DataUtils.asJsonArrayString(new String[] { "10", "20", "30" });
		assertNotNull(json1);
		assertEquals("[\"10\",\"20\",\"30\"]", json1);

		String json2 = DataUtils.asJsonArrayString(new Integer[] { 10, 20, 30 });
		assertNotNull(json2);
		assertEquals("[10,20,30]", json2);
	}

	@Test
	public void testReadStringArrayFromJsonObjectArrayBlank() throws Exception {
		String json = "{\"batchNo\":\"abc000001\", \"documents\":[]}";
		DocBatch docBatch = DataUtils.fromJsonString(DocBatch.class, json);
		assertNotNull(docBatch);
		assertEquals("abc000001", docBatch.getBatchNo());
		String[] documents = docBatch.getDocuments();
		assertNotNull(documents);
		assertEquals(0, documents.length);
	}

	@Test
	public void testReadStringArrayFromJsonObjectArray() throws Exception {
		String json = "{\"batchNo\":\"abc000002\", \"documents\":[{\"name\":\"birthCert\", \"title\":\"Birth Certificate\", \"weight\":52}, {\"name\":\"drivers\", \"title\":\"Driver's License\", \"weight\":65}]}";
		DocBatch docBatch = DataUtils.fromJsonString(DocBatch.class, json);
		assertNotNull(docBatch);
		assertEquals("abc000002", docBatch.getBatchNo());
		String[] documents = docBatch.getDocuments();
		assertNotNull(documents);
		assertEquals(2, documents.length);
		assertEquals("{\"name\":\"birthCert\",\"title\":\"Birth Certificate\",\"weight\":52}", documents[0]);
		assertEquals("{\"name\":\"drivers\",\"title\":\"Driver's License\",\"weight\":65}", documents[1]);

		Doc doc = DataUtils.fromJsonString(Doc.class, documents[0]);
		assertNotNull(doc);
		assertEquals("birthCert", doc.getName());
		assertEquals("Birth Certificate", doc.getTitle());
		assertEquals(52, doc.getWeight());

		doc = DataUtils.fromJsonString(Doc.class, documents[1]);
		assertNotNull(doc);
		assertEquals("drivers", doc.getName());
		assertEquals("Driver's License", doc.getTitle());
		assertEquals(65, doc.getWeight());
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
		assertNull(DataUtils.toArray(String.class, (List<String>) null));
		assertNull(DataUtils.toArray(String.class, (Set<String>) null));
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
		String[] arr = DataUtils.convert(String[].class, "Red,Green,Blue");
		assertNotNull(arr);
		assertEquals(3, arr.length);
		assertEquals("Red", arr[0]);
		assertEquals("Green", arr[1]);
		assertEquals("Blue", arr[2]);
	}

	@Test
	public void testConvertToStringFromPipeArray() throws Exception {
		String val = DataUtils.convert(String.class, new String[] { "Red", "Green", "Blue" });
		assertEquals("Red,Green,Blue", val);

		val = DataUtils.convert(String.class, new String[] { "Red", "Green", "Blue" }, pipeArrayFormatter);
		assertEquals("Red|Green|Blue", val);

		String[] vals = DataUtils.convert(String[].class, val, pipeArrayFormatter);
		assertNotNull(vals);
		assertEquals(3, vals.length);
		assertEquals("Red", vals[0]);
		assertEquals("Green", vals[1]);
		assertEquals("Blue", vals[2]);
	}

	@Test
	public void testConvertToStringFromCommaArray() throws Exception {
		String val = DataUtils.convert(String.class, new String[] { "Red", "Green", "Blue" });
		assertEquals("Red,Green,Blue", val);

		val = DataUtils.convert(String.class, new String[] { "Red", "Green", "Blue" }, commaArrayFormatter);
		assertEquals("Red,Green,Blue", val);

		String[] vals = DataUtils.convert(String[].class, val, commaArrayFormatter);
		assertNotNull(vals);
		assertEquals(3, vals.length);
		assertEquals("Red", vals[0]);
		assertEquals("Green", vals[1]);
		assertEquals("Blue", vals[2]);
	}

	@Test
	public void testConvertToIntegerFromCommaArray() throws Exception {
		int[] vals = DataUtils.convert(int[].class, "10,75,32", commaArrayFormatter);
		assertNotNull(vals);
		assertEquals(3, vals.length);
		assertEquals(10, vals[0]);
		assertEquals(75, vals[1]);
		assertEquals(32, vals[2]);
	}

	@Test
	public void testConvertToIntegerFromPipeArray() throws Exception {
		int[] vals = DataUtils.convert(int[].class, "10|75|32", pipeArrayFormatter);
		assertNotNull(vals);
		assertEquals(3, vals.length);
		assertEquals(10, vals[0]);
		assertEquals(75, vals[1]);
		assertEquals(32, vals[2]);
	}

	@Test
	public void testConvertToCommaArrayFromInteger() throws Exception {
		String vals = DataUtils.convert(String.class, new int[]{10,75,32}, commaArrayFormatter);
		assertNotNull(vals);
		assertEquals(vals, "10,75,32");
	}

	@Test
	public void testConvertToPipeArrayFromInteger() throws Exception {
		String vals = DataUtils.convert(String.class, new int[]{10,75,32}, pipeArrayFormatter);
		assertNotNull(vals);
		assertEquals(vals, "10|75|32");
	}

	@Test
	public void testAddBigDecimal() throws Exception {
		assertEquals(BigDecimal.ZERO, DataUtils.add(null, null));
		assertEquals(BigDecimal.TEN, DataUtils.add(BigDecimal.TEN, null));
		assertEquals(BigDecimal.TEN, DataUtils.add(null, BigDecimal.TEN));
		assertEquals(BigDecimal.valueOf(20), DataUtils.add(BigDecimal.TEN, BigDecimal.TEN));
	}

	@Override
	protected void onSetup() throws Exception {
		commaArrayFormatter = (Formatter<?>) getUplComponent(Locale.ENGLISH, "!commaarrayformat");
		pipeArrayFormatter = (Formatter<?>) getUplComponent(Locale.ENGLISH, "!pipearrayformat");
	}

	@Override
	protected void onTearDown() throws Exception {

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

		public void setResource(Object resource) {
			this.resource = resource;
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

	public static class DocBatch {

		private String batchNo;

		private String[] documents;

		public String getBatchNo() {
			return batchNo;
		}

		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}

		public String[] getDocuments() {
			return documents;
		}

		public void setDocuments(String[] documents) {
			this.documents = documents;
		}
	}

	public static class Owner {

		private String firstName;

		private String lastName;

		private Date dob;

		private Doc[] docs;

		public Owner(String firstName, String lastName, Date dob, Doc[] docs) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.dob = dob;
			this.docs = docs;
		}

		public Owner() {
			
		}
		
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Date getDob() {
			return dob;
		}

		public void setDob(Date dob) {
			this.dob = dob;
		}

		public Doc[] getDocs() {
			return docs;
		}

		public void setDocs(Doc[] docs) {
			this.docs = docs;
		}
	}

	private static JsonObjectComposition ownerComposition;

	static {
		JsonObjectComposition objectComposition2 = new JsonObjectComposition("doc", Arrays.asList(
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "name", "Name", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "title", "Title", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.INTEGER, "weight", "Weight", null, false)));
		ownerComposition = new JsonObjectComposition("owner", Arrays.asList(
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "firstName", "firstName", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "lastName", "lastName", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.DATE, "dob", "dob", null, false),
				new JsonFieldComposition(objectComposition2, DynamicEntityFieldType.CHILDLIST, null, "docs", "documents", null)),
				StandardFormatType.DATE_DDMMYYYY_SLASH, StandardFormatType.DATETIME_MMDDYYYY_SLASH);
	}

	public static class Doc {

		private String name;

		private String title;

		private int weight;

		public Doc(String name, String title, int weight) {
			this.name = name;
			this.title = title;
			this.weight = weight;
		}

		public Doc() {

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

	}

	private static JsonObjectComposition docComposition;

	static {
		docComposition = new JsonObjectComposition("doc", Arrays.asList(
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "name", "Name", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.STRING, "title", "Title", null, false),
				new JsonFieldComposition(DynamicEntityFieldType.FIELD, DataType.INTEGER, "weight", "Weight", null, false)));
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

		private OrderType order;

		private String author;

		private BigDecimal price;

		private int copies;

		private boolean censored;

		private Double[] priceHistory;

		private Date signDt;

		public Book(String author, BigDecimal price, int copies, boolean censored) {
			this.author = author;
			this.price = price;
			this.copies = copies;
			this.censored = censored;
		}

		public Book() {

		}

		public OrderType getOrder() {
			return order;
		}

		public void setOrder(OrderType order) {
			this.order = order;
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

		public Date getSignDt() {
			return signDt;
		}

		public void setSignDt(Date signDt) {
			this.signDt = signDt;
		}
	}
}
