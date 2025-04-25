/*
 * Copyright 2018-2025 The Code Department.
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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;

/**
 * Entity type utilities tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class EntityTypeUtilsTest extends AbstractUnifyComponentTest {

    @Test
    public void testGetEntityTypeInfoFromJsonNull() throws Exception {
    	List<EntityTypeInfo> list = EntityTypeUtils.getEntityTypeInfoFromJson(null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
	public void testGetEntityTypeInfoFromJsonEmpty() throws Exception {
		List<EntityTypeInfo> list = EntityTypeUtils.getEntityTypeInfoFromJson("{}");
		assertNotNull(list);
		assertEquals(1, list.size());

		EntityTypeInfo entityTypeInfo = list.get(0);
		assertNotNull(entityTypeInfo);
		assertEquals("root", entityTypeInfo.getName());
		assertNotNull(entityTypeInfo.getFields());
		assertEquals(0, entityTypeInfo.getFields().size());
	}

    @Test
	public void testGetEntityTypeInfoFromJsonSimple() throws Exception {
		List<EntityTypeInfo> list = EntityTypeUtils.getEntityTypeInfoFromJson(
				"{\"title\":\"C++ for Engineers\", \"quantity\":250, \"price\":22500.25, \"history\":[11236.75, 12450.75], \"onSale\":true}");
		assertNotNull(list);
		assertEquals(1, list.size());

		EntityTypeInfo entityTypeInfo = list.get(0);
		assertNotNull(entityTypeInfo);
		assertEquals("root", entityTypeInfo.getName());
		List<EntityTypeFieldInfo> fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(5, fields.size());

		EntityTypeFieldInfo fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("title", fieldInfo.getName());
		assertEquals("TITLE", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertFalse(fieldInfo.isArray());
		assertEquals("C++ for Engineers", fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("quantity", fieldInfo.getName());
		assertEquals("QUANTITY", fieldInfo.getColumn());
		assertEquals(DataType.INTEGER, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertFalse(fieldInfo.isArray());
		assertEquals("250", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("price", fieldInfo.getName());
		assertEquals("PRICE", fieldInfo.getColumn());
		assertEquals(DataType.DECIMAL, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertFalse(fieldInfo.isArray());
		assertEquals("22500.25", fieldInfo.getSample());

		fieldInfo = fields.get(3);
		assertNotNull(fieldInfo);
		assertEquals("history", fieldInfo.getName());
		assertEquals("HISTORY", fieldInfo.getColumn());
		assertEquals(DataType.DECIMAL, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertTrue(fieldInfo.isArray());
		assertEquals("11236.75", fieldInfo.getSample());

		fieldInfo = fields.get(4);
		assertNotNull(fieldInfo);
		assertEquals("onSale", fieldInfo.getName());
		assertEquals("ON_SALE", fieldInfo.getColumn());
		assertEquals(DataType.BOOLEAN, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertFalse(fieldInfo.isArray());
		assertEquals("true", fieldInfo.getSample());
	}

    @Test
	public void testGetEntityTypeInfoFromJsonCompound() throws Exception {
		List<EntityTypeInfo> list = EntityTypeUtils.getEntityTypeInfoFromJson(
				"{\"title\":\"C++ for Engineers\", \"quantity\":250, \"price\":22500.25, \"onSale\":true, \"author\":{\"name\":\"Susan Bramer\", \"dateOfBirth\":\"27-01-1964\"}}");
		assertNotNull(list);
		assertEquals(2, list.size());

		EntityTypeInfo entityTypeInfo = list.get(0);
		assertNotNull(entityTypeInfo);
		assertEquals("root", entityTypeInfo.getName());
		List<EntityTypeFieldInfo> fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(5, fields.size());

		EntityTypeFieldInfo fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("title", fieldInfo.getName());
		assertEquals("TITLE", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("C++ for Engineers", fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("quantity", fieldInfo.getName());
		assertEquals("QUANTITY", fieldInfo.getColumn());
		assertEquals(DataType.INTEGER, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("250", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("price", fieldInfo.getName());
		assertEquals("PRICE", fieldInfo.getColumn());
		assertEquals(DataType.DECIMAL, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("22500.25", fieldInfo.getSample());

		fieldInfo = fields.get(3);
		assertNotNull(fieldInfo);
		assertEquals("onSale", fieldInfo.getName());
		assertEquals("ON_SALE", fieldInfo.getColumn());
		assertEquals(DataType.BOOLEAN, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("true", fieldInfo.getSample());

		fieldInfo = fields.get(4);
		assertNotNull(fieldInfo);
		assertEquals("author", fieldInfo.getName());
		assertNull(fieldInfo.getColumn());
		assertNull(fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.CHILD, fieldInfo.getType());
		assertEquals("rootAuthor", fieldInfo.getChildEntityName());
		assertNull(fieldInfo.getSample());

		entityTypeInfo = list.get(1);
		assertNotNull(entityTypeInfo);
		assertEquals("rootAuthor", entityTypeInfo.getName());
		fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(3, fields.size());

		fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("rootId", fieldInfo.getName());
		assertEquals("ROOT_ID", fieldInfo.getColumn());
		assertEquals(DataType.LONG, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FOREIGN_KEY, fieldInfo.getType());
		assertEquals("root", fieldInfo.getParentEntityName());
		assertNull(fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("name", fieldInfo.getName());
		assertEquals("NAME", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("Susan Bramer", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("dateOfBirth", fieldInfo.getName());
		assertEquals("DATE_OF_BIRTH", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("27-01-1964", fieldInfo.getSample());
	}

    @Test
	public void testGetEntityTypeInfoFromJsonCompoundDeep() throws Exception {
		List<EntityTypeInfo> list = EntityTypeUtils.getEntityTypeInfoFromJson(
				"{\"title\":\"C++ for Engineers\", \"quantity\":250, \"price\":22500.25, \"onSale\":false, \"author\":{\"name\":\"Susan Bramer\", \"dateOfBirth\":\"27-01-1964\", \"addresses\":[{\"line1\":\"24 Parklane\", \"line2\":\"Apapa\"}]}}");
		assertNotNull(list);
		assertEquals(3, list.size());

		// Type 1
		EntityTypeInfo entityTypeInfo = list.get(0);
		assertNotNull(entityTypeInfo);
		assertEquals("root", entityTypeInfo.getName());
		List<EntityTypeFieldInfo> fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(5, fields.size());

		EntityTypeFieldInfo fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("title", fieldInfo.getName());
		assertEquals("TITLE", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("C++ for Engineers", fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("quantity", fieldInfo.getName());
		assertEquals("QUANTITY", fieldInfo.getColumn());
		assertEquals(DataType.INTEGER, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("250", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("price", fieldInfo.getName());
		assertEquals("PRICE", fieldInfo.getColumn());
		assertEquals(DataType.DECIMAL, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("22500.25", fieldInfo.getSample());

		fieldInfo = fields.get(3);
		assertNotNull(fieldInfo);
		assertEquals("onSale", fieldInfo.getName());
		assertEquals("ON_SALE", fieldInfo.getColumn());
		assertEquals(DataType.BOOLEAN, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("false", fieldInfo.getSample());

		fieldInfo = fields.get(4);
		assertNotNull(fieldInfo);
		assertEquals("author", fieldInfo.getName());
		assertNull(fieldInfo.getColumn());
		assertNull(fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.CHILD, fieldInfo.getType());
		assertEquals("rootAuthor", fieldInfo.getChildEntityName());
		assertNull(fieldInfo.getSample());

		// Type 2
		entityTypeInfo = list.get(1);
		assertNotNull(entityTypeInfo);
		assertEquals("rootAuthor", entityTypeInfo.getName());
		fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(4, fields.size());

		fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("rootId", fieldInfo.getName());
		assertEquals("ROOT_ID", fieldInfo.getColumn());
		assertEquals(DataType.LONG, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FOREIGN_KEY, fieldInfo.getType());
		assertEquals("root", fieldInfo.getParentEntityName());
		assertNull(fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("name", fieldInfo.getName());
		assertEquals("NAME", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("Susan Bramer", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("dateOfBirth", fieldInfo.getName());
		assertEquals("DATE_OF_BIRTH", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("27-01-1964", fieldInfo.getSample());

		fieldInfo = fields.get(3);
		assertNotNull(fieldInfo);
		assertEquals("addresses", fieldInfo.getName());
		assertNull(fieldInfo.getColumn());
		assertNull(fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.CHILDLIST, fieldInfo.getType());
		assertEquals("rootAuthorAddresses", fieldInfo.getChildEntityName());
		assertNull(fieldInfo.getSample());

		// Type 3
		entityTypeInfo = list.get(2);
		assertNotNull(entityTypeInfo);
		assertEquals("rootAuthorAddresses", entityTypeInfo.getName());
		fields = entityTypeInfo.getFields();
		assertNotNull(fields);
		assertEquals(3, fields.size());

		fieldInfo = fields.get(0);
		assertNotNull(fieldInfo);
		assertEquals("rootAuthorId", fieldInfo.getName());
		assertEquals("ROOT_AUTHOR_ID", fieldInfo.getColumn());
		assertEquals(DataType.LONG, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FOREIGN_KEY, fieldInfo.getType());
		assertEquals("rootAuthor", fieldInfo.getParentEntityName());
		assertNull(fieldInfo.getSample());

		fieldInfo = fields.get(1);
		assertNotNull(fieldInfo);
		assertEquals("line1", fieldInfo.getName());
		assertEquals("LINE_1", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("24 Parklane", fieldInfo.getSample());

		fieldInfo = fields.get(2);
		assertNotNull(fieldInfo);
		assertEquals("line2", fieldInfo.getName());
		assertEquals("LINE_2", fieldInfo.getColumn());
		assertEquals(DataType.STRING, fieldInfo.getDataType());
		assertEquals(DynamicEntityFieldType.FIELD, fieldInfo.getType());
		assertEquals("Apapa", fieldInfo.getSample());
	}

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
