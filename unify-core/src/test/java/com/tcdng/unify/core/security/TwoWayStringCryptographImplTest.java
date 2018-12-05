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
package com.tcdng.unify.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.UnifyException;

/**
 * Default two-way string cryptograph implementation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TwoWayStringCryptographImplTest extends AbstractUnifyComponentTest {

	@Test
	public void testEncryptNull() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		assertNull(stringCryptographA.encrypt(null));
	}

	@Test
	public void testEncryptString() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		String encrypted = stringCryptographA.encrypt("Hello World!");
		assertNotNull(encrypted);
		assertFalse("Hello World!".equals(encrypted));
	}

	@Test
	public void testEncryptStringConsistent() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		String encryptedA = stringCryptographA.encrypt("Hello World!");
		String encryptedB = stringCryptographA.encrypt("Hello World!");
		String encryptedC = stringCryptographA.encrypt("Hello World!");
		assertEquals(encryptedA, encryptedB);
		assertEquals(encryptedB, encryptedC);
	}

	@Test
	public void testEncryptStringWithDifferentCryptographs() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		TwoWayStringCryptograph stringCryptographB = (TwoWayStringCryptograph) getComponent("cryptographB");
		String encryptedA = stringCryptographA.encrypt("Hello World!");
		String encryptedB = stringCryptographB.encrypt("Hello World!");
		assertFalse(encryptedA.equals(encryptedB));
	}

	@Test
	public void testDecryptNull() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		assertNull(stringCryptographA.decrypt(null));
	}

	@Test(expected = UnifyException.class)
	public void testDecryptString() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		stringCryptographA.decrypt("Hello World!");
	}

	@Test
	public void testDecryptEncryptedString() throws Exception {
		TwoWayStringCryptograph stringCryptographA = (TwoWayStringCryptograph) getComponent("cryptographA");
		String encrypted = stringCryptographA.encrypt("Piggy plans to kidnap Kermit");
		String decrypted = stringCryptographA.decrypt(encrypted);
		assertEquals("Piggy plans to kidnap Kermit", decrypted);
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addDependency("cryptographA", TwoWayStringCryptographImpl.class, new Setting("encryptionKey", "Neptune"));
		addDependency("cryptographB", TwoWayStringCryptographImpl.class, new Setting("encryptionKey", "Pluto"));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
