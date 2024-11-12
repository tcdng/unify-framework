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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.EntityFieldType;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.DynamicFieldInfo;

/**
 * Entity type utilities tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class EntityTypeUtilsTest extends AbstractUnifyComponentTest {

    @Test
    public void testGetDynamicEntityInfoFromJsonNull() throws Exception {
    	List<DynamicEntityInfo> list = EntityTypeUtils.getDynamicEntityInfoFromJson(null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
	public void testGetDynamicEntityInfoFromJsonEmpty() throws Exception {
		List<DynamicEntityInfo> list = EntityTypeUtils.getDynamicEntityInfoFromJson("{}");
		assertNotNull(list);
		assertEquals(1, list.size());

		DynamicEntityInfo dynamicEntityInfo = list.get(0);
		assertNotNull(dynamicEntityInfo);
		assertEquals("root", dynamicEntityInfo.getClassName());
		assertEquals(DynamicEntityType.INFO_ONLY, dynamicEntityInfo.getType());
		assertNotNull(dynamicEntityInfo.getFieldInfos());
		assertEquals(0, dynamicEntityInfo.getFieldInfos().size());
	}

    @Test
	public void testGetDynamicEntityInfoFromJsonSimple() throws Exception {
		List<DynamicEntityInfo> list = EntityTypeUtils.getDynamicEntityInfoFromJson(
				"{\"title\":\"C++ for Engineers\", \"quantity\":250, \"price\":22500.25, \"onSale\":true}");
		assertNotNull(list);
		assertEquals(1, list.size());

		DynamicEntityInfo dynamicEntityInfo = list.get(0);
		assertNotNull(dynamicEntityInfo);
		assertEquals("root", dynamicEntityInfo.getClassName());
		assertEquals(DynamicEntityType.INFO_ONLY, dynamicEntityInfo.getType());
		List<DynamicFieldInfo> fields = dynamicEntityInfo.getFieldInfos();
		assertNotNull(fields);
		assertEquals(4, fields.size());
		
		DynamicFieldInfo dynamicFieldInfo = fields.get(0);
		assertNotNull(dynamicFieldInfo);
		assertEquals("title", dynamicFieldInfo.getFieldName());
		assertEquals("TITLE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.STRING, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());
		
		dynamicFieldInfo = fields.get(1);
		assertNotNull(dynamicFieldInfo);
		assertEquals("quantity", dynamicFieldInfo.getFieldName());
		assertEquals("QUANTITY", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.INTEGER, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());
		
		dynamicFieldInfo = fields.get(2);
		assertNotNull(dynamicFieldInfo);
		assertEquals("price", dynamicFieldInfo.getFieldName());
		assertEquals("PRICE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.DECIMAL, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());
		
		dynamicFieldInfo = fields.get(3);
		assertNotNull(dynamicFieldInfo);
		assertEquals("onSale", dynamicFieldInfo.getFieldName());
		assertEquals("ON_SALE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.BOOLEAN, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());		
	}

    @Test
	public void testGetDynamicEntityInfoFromJsonCompound() throws Exception {
		List<DynamicEntityInfo> list = EntityTypeUtils.getDynamicEntityInfoFromJson(
				"{\"title\":\"C++ for Engineers\", \"quantity\":250, \"price\":22500.25, \"onSale\":true, \"author\":{\"name\":\"Susan Bramer\", \"dateOfBirth\":\"27-01-1964\"}}");
		assertNotNull(list);
		assertEquals(2, list.size());

		DynamicEntityInfo dynamicEntityInfo = list.get(0);
		assertNotNull(dynamicEntityInfo);
		assertEquals("root", dynamicEntityInfo.getClassName());
		assertEquals(DynamicEntityType.INFO_ONLY, dynamicEntityInfo.getType());
		List<DynamicFieldInfo> fields = dynamicEntityInfo.getFieldInfos();
		assertNotNull(fields);
		assertEquals(5, fields.size());

		DynamicFieldInfo dynamicFieldInfo = fields.get(0);
		assertNotNull(dynamicFieldInfo);
		assertEquals("title", dynamicFieldInfo.getFieldName());
		assertEquals("TITLE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.STRING, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(1);
		assertNotNull(dynamicFieldInfo);
		assertEquals("quantity", dynamicFieldInfo.getFieldName());
		assertEquals("QUANTITY", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.INTEGER, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(2);
		assertNotNull(dynamicFieldInfo);
		assertEquals("price", dynamicFieldInfo.getFieldName());
		assertEquals("PRICE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.DECIMAL, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(3);
		assertNotNull(dynamicFieldInfo);
		assertEquals("onSale", dynamicFieldInfo.getFieldName());
		assertEquals("ON_SALE", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.BOOLEAN, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(4);
		assertNotNull(dynamicFieldInfo);
		assertEquals("rootAuthor", dynamicFieldInfo.getFieldName());
		assertNull(dynamicFieldInfo.getColumnName());
		assertNull(dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.CHILD, dynamicFieldInfo.getFieldType());

		dynamicEntityInfo = list.get(1);
		assertNotNull(dynamicEntityInfo);
		assertEquals("rootAuthor", dynamicEntityInfo.getClassName());
		assertEquals(DynamicEntityType.INFO_ONLY, dynamicEntityInfo.getType());
		fields = dynamicEntityInfo.getFieldInfos();
		assertNotNull(fields);
		assertEquals(3, fields.size());

		dynamicFieldInfo = fields.get(0);
		assertNotNull(dynamicFieldInfo);
		assertEquals("rootId", dynamicFieldInfo.getFieldName());
		assertEquals("ROOT_ID", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.LONG, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.FOREIGN_KEY, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(1);
		assertNotNull(dynamicFieldInfo);
		assertEquals("name", dynamicFieldInfo.getFieldName());
		assertEquals("NAME", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.STRING, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());

		dynamicFieldInfo = fields.get(2);
		assertNotNull(dynamicFieldInfo);
		assertEquals("dateOfBirth", dynamicFieldInfo.getFieldName());
		assertEquals("DATE_OF_BIRTH", dynamicFieldInfo.getColumnName());
		assertEquals(DataType.STRING, dynamicFieldInfo.getDataType());
		assertEquals(EntityFieldType.TABLE_COLUMN, dynamicFieldInfo.getFieldType());
	}

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
