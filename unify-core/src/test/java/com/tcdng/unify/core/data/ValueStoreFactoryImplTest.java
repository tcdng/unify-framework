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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Default value store factory implementation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ValueStoreFactoryImplTest extends AbstractUnifyComponentTest {

	private PackableDocConfig custDocConfig;

	@Test
	public void testGetValueStore() throws Exception {
		ValueStoreFactory vsFactory = (ValueStoreFactory) getComponent(
				ApplicationComponents.APPLICATION_VALUESTOREFACTORY);

		ValueStore vs1 = vsFactory.getValueStore(new PackableDoc(custDocConfig, false), 0);
		ValueStore vs2 = vsFactory.getValueStore(new Customer(), 0);
		ValueStore vs3 = vsFactory.getValueStore(new Address(), 0);
		assertNotNull(vs1);
		assertNotNull(vs2);
		assertNotNull(vs3);
		assertNotSame(vs1.getClass(), vs2.getClass());
		assertEquals(vs2.getClass(), vs3.getClass());
	}

	@Test
	public void testGetValueStoreWithNullSource() throws Exception {
		ValueStoreFactory vsFactory = (ValueStoreFactory) getComponent(
				ApplicationComponents.APPLICATION_VALUESTOREFACTORY);
		ValueStore vs1 = vsFactory.getValueStore(null, 0);
		assertNull(vs1);
	}

	@Override
	protected void onSetup() throws Exception {
		custDocConfig = new PackableDocConfig("customerConfig", new PackableDocConfig.FieldConfig("name", String.class),
				new PackableDocConfig.FieldConfig("birthDt", Date.class),
				new PackableDocConfig.FieldConfig("balance", BigDecimal.class),
				new PackableDocConfig.FieldConfig("id", Long.class),
				new PackableDocConfig.FieldConfig("address", Address.class),
				new PackableDocConfig.FieldConfig("modeList", List.class));
	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
