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

package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

/**
 * Large string writer tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class LargeStringWriterTest {

	@Test
	public void testDefaultInitialCapacity() throws Exception {
		LargeStringWriter writer = new LargeStringWriter();
		assertEquals(1024, writer.capacity());
		writer.close();
	}

	@Test
	public void testInitialCapacity() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		assertEquals(16, writer.capacity());
		writer.close();
	}

	@Test
	public void testExpandCapacityDouble() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("It's Monday morning!");
		assertEquals(32, writer.capacity());
		writer.close();
	}

	@Test
	public void testExpandCapacityBeyoundDouble() throws Exception {
		String str = "It's Monday morning! I had a very wonderful weekend.";
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write(str);
		assertEquals(str.length(), writer.capacity());
		writer.close();
	}

	@Test
	public void testToString() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("It's Monday morning!");
		assertEquals("It's Monday morning!", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteSingleCharacter() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write('J');
		assertEquals("J", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteMultipleCharacter() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write('H');
		writer.write('e');
		writer.write('l');
		writer.write('l');
		writer.write('o');
		writer.write(' ');
		writer.write('w');
		writer.write('o');
		writer.write('r');
		writer.write('l');
		writer.write('d');
		writer.write('!');
		assertEquals("Hello world!", writer.toString());
		writer.close();
	}

	@Test(expected = NullPointerException.class)
	public void testWriteNullCharArray() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg = null;
		writer.write(msg);
		assertEquals("Jane", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteSingleCharArray() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg = { 'J', 'a', 'n', 'e' };
		writer.write(msg);
		assertEquals("Jane", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteMultipleCharArray() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg1 = { 'G', '.', 'I', '.', ' ' };
		char[] msg2 = { 'J', 'a', 'n', 'e' };
		writer.write(msg1);
		writer.write(msg2);
		assertEquals("G.I. Jane", writer.toString());
		writer.close();
	}

	@Test(expected = NullPointerException.class)
	public void testWriteNullCharArrayWithOffsetAndLength() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg = null;
		writer.write(msg, 1, 2);
		assertEquals("an", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteSingleCharArrayWithOffsetAndLength() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg = { 'J', 'a', 'n', 'e' };
		writer.write(msg, 1, 2);
		assertEquals("an", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteMultipleCharArrayWithOffsetAndLength() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		char[] msg1 = { 'G', '.', 'I', '.', ' ' };
		char[] msg2 = { 'J', 'a', 'n', 'e' };
		writer.write(msg1, 0, 4);
		writer.write(msg2, 1, 3);
		assertEquals("G.I.ane", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteNullString() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		String msg = null;
		writer.write(msg);
		assertEquals("null", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteSingleString() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("An apple a day keeps the doctor away.");
		assertEquals("An apple a day keeps the doctor away.", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteMultipleString() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("An apple a day keeps");
		writer.write(" the doctor away.");
		assertEquals("An apple a day keeps the doctor away.", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteSingleStringWithOffsetAndLength() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("An apple a day keeps the doctor away.", 9, 22);
		assertEquals("a day keeps the doctor", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteMultipleStringWithOffsetAndLength() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		writer.write("An apple a day keeps", 0, 9);
		writer.write(" the doctor away.", 12, 5);
		assertEquals("An apple away.", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendNullCharSequence() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		CharSequence seq = null;
		writer.append(seq);
		assertEquals("null", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendSingleCharSequence() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		CharSequence seq = "To every action...";
		writer.append(seq);
		assertEquals("To every action...", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendMultipleCharSequence() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		CharSequence seq1 = "To every action";
		CharSequence seq2 = " there is an equal and opposite reaction.";
		writer.append(seq1).append(seq2);
		assertEquals("To every action there is an equal and opposite reaction.", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendSingleCharSequenceWithStartAndEnd() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		CharSequence seq = "To every action...";
		writer.append(seq, 3, 15);
		assertEquals("every action", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendMultipleCharSequenceWithStartAndEnd() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(16);
		CharSequence seq1 = "To every action...";
		CharSequence seq2 = " there is an equal and opposite reaction.";
		writer.append(seq1, 3, 15).append(seq2, 6, 18);
		assertEquals("every action is an equal", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendChar() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(4);
		writer.append('E').append('n').append('t').append('e').append('r');
		assertEquals("Enter", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendBoolean() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(4);
		writer.append(true).append(',').append(false);
		assertEquals("true,false", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendString() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(4);
		writer.append("Do try this").append(" at work!");
		assertEquals("Do try this at work!", writer.toString());
		writer.close();
	}

	@Test
	public void testAppendObject() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(4);
		writer.append("I am ").append(2.86).append(" metres tall and ").append(2).append(" years old!");
		assertEquals("I am 2.86 metres tall and 2 years old!", writer.toString());
		writer.close();
	}

	@Test
	public void testWriteToWriter() throws Exception {
		LargeStringWriter writer = new LargeStringWriter(4);
		writer.append("You must be a tree!");

		StringWriter sw = new StringWriter();
		writer.writeTo(sw);
		assertEquals("You must be a tree!", sw.toString());
		writer.close();
	}
}
