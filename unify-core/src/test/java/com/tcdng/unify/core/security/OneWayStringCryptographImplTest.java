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
package com.tcdng.unify.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.Setting;

/**
 * Default one-way string cryptograph implementation test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class OneWayStringCryptographImplTest extends AbstractUnifyComponentTest {

    @Test
    public void testEncryptNull() throws Exception {
        OneWayStringCryptograph stringCryptographA = (OneWayStringCryptograph) getComponent("cryptographA");
        OneWayStringCryptograph stringCryptographB = (OneWayStringCryptograph) getComponent("cryptographB");
        OneWayStringCryptograph stringCryptographC = (OneWayStringCryptograph) getComponent("cryptographC");

        assertNull(stringCryptographA.encrypt(null));
        assertNull(stringCryptographB.encrypt(null));
        assertNull(stringCryptographC.encrypt(null));
    }

    @Test
    public void testEncryptStringConsistent() throws Exception {
        OneWayStringCryptograph stringCryptographA = (OneWayStringCryptograph) getComponent("cryptographA");
        OneWayStringCryptograph stringCryptographB = (OneWayStringCryptograph) getComponent("cryptographB");
        String encryptedA = stringCryptographA.encrypt("Hello");
        String encryptedB = stringCryptographB.encrypt("Hello");
        assertEquals(encryptedA, encryptedB);
    }

    @Test
    public void testEncryptStringWithDifferentCryptographs() throws Exception {
        OneWayStringCryptograph stringCryptographA = (OneWayStringCryptograph) getComponent("cryptographA");
        OneWayStringCryptograph stringCryptographC = (OneWayStringCryptograph) getComponent("cryptographC");
        String encryptedA = stringCryptographA.encrypt("Hello");
        String encryptedB = stringCryptographC.encrypt("Hello");
        assertFalse(encryptedA.equals(encryptedB));
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addDependency("cryptographA", OneWayStringCryptographImpl.class, new Setting("encryptionKey", "Slink"));
        addDependency("cryptographB", OneWayStringCryptographImpl.class, new Setting("encryptionKey", "Slink"));
        addDependency("cryptographC", OneWayStringCryptographImpl.class,
                new Setting("encryptionKey", "The Code Department"));
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
