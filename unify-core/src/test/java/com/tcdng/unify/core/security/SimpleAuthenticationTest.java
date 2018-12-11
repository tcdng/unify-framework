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
package com.tcdng.unify.core.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.Setting;

/**
 * Simple password authentication tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SimpleAuthenticationTest extends AbstractUnifyComponentTest {

	@Test
	public void testEncrypted() throws Exception {
		Authentication dpa = (SimpleAuthentication) getComponent("simple-auth1");
		assertEquals("scott", dpa.getUsername());
		assertEquals("tiger", dpa.getPassword());
	}

	@Test
	public void testUnencrypted() throws Exception {
		Authentication dpa = (SimpleAuthentication) getComponent("simple-auth2");
		assertEquals("scott", dpa.getUsername());
		assertEquals("tiger", dpa.getPassword());
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addDependency("twowaycryptograph", TwoWayStringCryptographImpl.class,
				new Setting("encryptionKey", "Into The Sun"));
		addDependency("simple-auth1", SimpleAuthentication.class, new Setting("username", "scott"),
				new Setting("password", "FjmgFbJrrNY="), new Setting("cryptograph", "twowaycryptograph"));
		addDependency("simple-auth2", SimpleAuthentication.class, new Setting("username", "scott"),
				new Setting("password", "tiger"));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
