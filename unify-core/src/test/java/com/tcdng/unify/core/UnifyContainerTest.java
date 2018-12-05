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
package com.tcdng.unify.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.annotation.Configurable;

/**
 * Unify container tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyContainerTest extends AbstractUnifyComponentTest {

	@Test(expected = UnifyException.class)
	public void testGetUnknownComponent() throws Exception {
		getComponent("component-c");
	}

	@Test
	public void testGetSingletonComponent() throws Exception {
		UnifyComponent c1 = getComponent("component-a1");
		UnifyComponent c2 = getComponent("component-a1");
		UnifyComponent c3 = getComponent("component-a1");
		assertSame(c1, c2);
		assertSame(c1, c3);
		assertSame(c2, c3);
	}

	@Test
	public void testGetNonSingletonComponent() throws Exception {
		UnifyComponent c1 = getComponent("component-a2");
		UnifyComponent c2 = getComponent("component-a2");
		UnifyComponent c3 = getComponent("component-a2");
		assertNotSame(c1, c2);
		assertNotSame(c1, c3);
		assertNotSame(c2, c3);
	}

	@Test
	public void testGetComponentWithConfigurableProperties() throws Exception {
		TestComponentB testComponentB = (TestComponentB) getComponent("component-b");
		assertNotNull(testComponentB.getTestComponentA1());
		assertTrue(TestComponentA1.class.equals(testComponentB.getTestComponentA1().getClass()));
		assertEquals("127.0.0.1", testComponentB.getAddress());
	}

	@Test
	public void testGetUnknownComponentConfig() throws Exception {
		assertNull(getComponentConfig("component-c"));
	}

	@Test
	public void testGetSingletonComponentConfig() throws Exception {
		UnifyComponentConfig ucc = getComponentConfig("component-a1");
		assertNotNull(ucc);
		assertTrue(ucc.isSingleton());
	}

	@Test
	public void testGetNonSingletonComponentConfig() throws Exception {
		UnifyComponentConfig ucc = getComponentConfig("component-a2");
		assertNotNull(ucc);
		assertFalse(ucc.isSingleton());
	}

	@Test
	public void testGetComponentConfigs() throws Exception {
		List<UnifyComponentConfig> unifyComponentConfigList = getComponentConfigs(UnifyComponent.class);
		assertNotNull(unifyComponentConfigList);
		Set<String> componentNames = new HashSet<String>();
		for (UnifyComponentConfig unifyComponentConfig : unifyComponentConfigList) {
			componentNames.add(unifyComponentConfig.getName());
		}
		assertTrue(componentNames.contains("component-a1"));
		assertTrue(componentNames.contains("component-a2"));
		assertTrue(componentNames.contains("component-b"));
	}

	@Test
	public void testGetComponentNames() throws Exception {
		List<String> unifyComponentNameList = getComponentNames(UnifyComponent.class);
		assertNotNull(unifyComponentNameList);
		Set<String> componentNames = new HashSet<String>();
		componentNames.addAll(unifyComponentNameList);
		assertTrue(componentNames.contains("component-a1"));
		assertTrue(componentNames.contains("component-a2"));
		assertTrue(componentNames.contains("component-b"));
	}

	@Test
	public void testComponentCustomisation() throws Exception {
		UnifyComponent component1 = getComponent("component-b3");
		assertNotNull(component1);
		assertTrue(component1 instanceof TestComponentC);

		UnifyComponent component2 = getComponent("component-b3_tiger");
		assertNotNull(component2);
		assertTrue(component2 == component1);
	}

	public void testComponentCustomisationResolved() throws Exception {
	}

	public static class TestComponentA1 extends AbstractUnifyComponent {

		@Override
		protected void onInitialize() throws UnifyException {

		}

		@Override
		protected void onTerminate() throws UnifyException {

		}
	}

	public static class TestComponentA2 extends AbstractUnifyComponent {

		@Override
		protected void onInitialize() throws UnifyException {

		}

		@Override
		protected void onTerminate() throws UnifyException {

		}
	}

	public static class TestComponentB extends AbstractUnifyComponent {

		@Configurable("component-a1")
		private TestComponentA1 testComponentA1;

		@Configurable("127.0.0.1")
		private String address;

		public TestComponentA1 getTestComponentA1() {
			return testComponentA1;
		}

		public String getAddress() {
			return address;
		}

		@Override
		protected void onInitialize() throws UnifyException {

		}

		@Override
		protected void onTerminate() throws UnifyException {

		}
	}

	public static class TestComponentC extends AbstractUnifyComponent {

		@Override
		protected void onInitialize() throws UnifyException {

		}

		@Override
		protected void onTerminate() throws UnifyException {

		}
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addContainerSetting(UnifyCorePropertyConstants.APPLICATION_CUSTOMISATION, "tiger"); // Customise
		// for
		// tiger

		addDependency("component-a1", TestComponentA1.class);// Singleton
		addDependency("component-a2", TestComponentA2.class, false);// Non-singleton
		addDependency("component-b", TestComponentB.class);// Singleton

		addDependency("component-b3", TestComponentB.class);// For custom
															// override test
		addDependency("component-b3_tiger", TestComponentC.class);
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
