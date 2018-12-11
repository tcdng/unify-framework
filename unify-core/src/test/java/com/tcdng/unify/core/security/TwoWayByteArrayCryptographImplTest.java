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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.UnifyException;

/**
 * Byte array cryptograph implementation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TwoWayByteArrayCryptographImplTest extends AbstractUnifyComponentTest {

	@Test
	public void testEncryptNull() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		assertNull(byteArrayCryptographA.encrypt(null));
	}

	@Test
	public void testEncryptByteArray() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		byte[] array = { 1, 2, 3, 4 };
		byte[] encryptedArray = byteArrayCryptographA.encrypt(array);
		assertNotNull(encryptedArray);
		assertFalse(Arrays.equals(array, encryptedArray));
	}

	@Test
	public void testEncryptByteArrayConsistent() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		byte[] array = { 1, 2, 3, 4, 5 };
		byte[] encryptedA = byteArrayCryptographA.encrypt(array);
		byte[] encryptedB = byteArrayCryptographA.encrypt(array);
		byte[] encryptedC = byteArrayCryptographA.encrypt(array);
		assertTrue(Arrays.equals(encryptedA, encryptedB));
		assertTrue(Arrays.equals(encryptedB, encryptedC));
	}

	@Test
	public void testEncryptByteArrayWithDifferentCryptographs() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		TwoWayByteArrayCryptograph byteArrayCryptographB = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographB");
		byte[] array = { 1, 2, 3, 4, 5 };
		byte[] encryptedA = byteArrayCryptographA.encrypt(array);
		byte[] encryptedB = byteArrayCryptographB.encrypt(array);
		assertFalse(Arrays.equals(encryptedA, encryptedB));
	}

	@Test
	public void testDecryptNull() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		assertNull(byteArrayCryptographA.decrypt(null));
	}

	@Test(expected = UnifyException.class)
	public void testDecryptArray() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		byte[] array = { 1, 2, 3, 4, 5 };
		byteArrayCryptographA.decrypt(array);
	}

	@Test
	public void testDecryptEncryptedByteArray() throws Exception {
		TwoWayByteArrayCryptograph byteArrayCryptographA = (TwoWayByteArrayCryptograph) this
				.getComponent("cryptographA");
		byte[] array = { 1, 2, 3, 4, 5 };
		byte[] encrypted = byteArrayCryptographA.encrypt(array);
		byte[] decrypted = byteArrayCryptographA.decrypt(encrypted);
		assertTrue(Arrays.equals(array, decrypted));
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addDependency("cryptographA", TwoWayByteArrayCryptographImpl.class, new Setting("encryptionKey", "Orange"));
		addDependency("cryptographB", TwoWayByteArrayCryptographImpl.class, new Setting("encryptionKey", "Banana"));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
