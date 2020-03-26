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

package com.tcdng.unify.core.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Tests default implementation of runtime java class manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RuntimeJavaClassManagerTest extends AbstractUnifyComponentTest {

    private RuntimeJavaClassManager rjcm;

    private final String engHelloSrc = "package com.tcdng.unify.core.runtime;" + "@Language(\"English\")"
            + "public class EnglishHello implements Hello {" + " public String sayHello() {" + "     return \"Hello\";"
            + " }" + "}";

    private final String naijaHelloSrc1 = "package com.tcdng.unify.core.runtime;" + "@Language(\"Naija\")"
            + "public class NaijaHello implements Hello {" + " public String sayHello() {" + "     return \"How now?\";"
            + " }" + "}";

    private final String naijaHelloSrc2 = "package com.tcdng.unify.core.runtime;" + "@Language(\"Naija\")"
            + "public class NaijaHello implements Hello {" + " public String sayHello() {" + "     return \"How far?\";"
            + " }" + "}";

    @Test
    public void testCompileAndLoadClassString() throws Exception {
        Class<?> clazz = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.EnglishHello", engHelloSrc);
        assertNotNull(clazz);
        assertEquals("com.tcdng.unify.core.runtime.EnglishHello", clazz.getName());
        assertTrue(clazz.isAnnotationPresent(Language.class));
        Language la = clazz.getAnnotation(Language.class);
        assertNotNull(la);
        assertEquals("English", la.value());
    }

    @Test
    public void testCompileAndLoadClassFunction() throws Exception {
        Class<?> clazz = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1);
        assertNotNull(clazz);
        Hello hello = (Hello) ReflectUtils.newInstance(clazz);
        assertEquals("How now?", hello.sayHello());
    }

    @Test
    public void testCompileAndSaveJavaClass() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
    }

    @Test
    public void testCompileAndSaveJavaClassInvalidVersion() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 0));
        assertFalse(success);
    }

    @Test
    public void testCompileAndSaveJavaClassOlderVersion() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 4));
        assertTrue(success);
        success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 2));
        assertFalse(success);
    }

    @Test
    public void testCompileAndSaveJavaClassNewVersion() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
        success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 2));
        assertTrue(success);
    }

    @Test
    public void testCompileAndSaveJavaClassMultiple() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.EnglishHello", engHelloSrc, 1));
        assertTrue(success);
        success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
    }

    @Test
    public void testCompileAndSaveJavaClassSameButDifferentGroup() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp11",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
        success = rjcm.compileAndSaveJavaClass("languageGrp12",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
    }

    @Test(expected = UnifyException.class)
    public void testCompileAndSaveJavaClassBadSource() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.EnglishHello", "Bad code.", 1));
    }

    @Test
    public void testGetSavedJavaClassVersionUnknownClass() throws Exception {
        long version = rjcm.getSavedJavaClassVersion("languageGrp1", "com.tcdng.unify.core.runtime.EnglishHello");
        assertEquals(0, version);
    }

    @Test
    public void testGetSavedJavaClassVersion() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.EnglishHello", engHelloSrc, 10));
        long version = rjcm.getSavedJavaClassVersion("languageGrp1", "com.tcdng.unify.core.runtime.EnglishHello");
        assertEquals(10, version);
    }

    @Test
    public void testGetSavedJavaClassVersionUnchanged() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 20));
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 10));
        long version = rjcm.getSavedJavaClassVersion("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertEquals(20, version);
    }

    @Test
    public void testGetSavedJavaClassVersionChanged() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 20));
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 21));
        long version = rjcm.getSavedJavaClassVersion("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertEquals(21, version);
    }

    @Test(expected = UnifyException.class)
    public void testGetSavedJavaClassUnknown() throws Exception {
        rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
    }

    @Test
    public void testGetSavedJavaClass() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
        Class<?> clazz = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz);
        assertEquals("com.tcdng.unify.core.runtime.NaijaHello", clazz.getName());
        assertTrue(clazz.isAnnotationPresent(Language.class));
        Language la = clazz.getAnnotation(Language.class);
        assertNotNull(la);
        assertEquals("Naija", la.value());
    }

    @Test
    public void testGetSavedJavaClassMultipleCallsSameVersion() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
        Class<?> clazz1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz1);
        assertEquals("com.tcdng.unify.core.runtime.NaijaHello", clazz1.getName());
        assertTrue(clazz1.isAnnotationPresent(Language.class));
        Language la = clazz1.getAnnotation(Language.class);
        assertNotNull(la);
        assertEquals("Naija", la.value());

        Class<?> clazz2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertTrue(clazz1 == clazz2);

        Class<?> clazz3 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertTrue(clazz1 == clazz3);
    }

    @Test
    public void testGetSavedJavaClassMultipleCallsDiffVersion() throws Exception {
        boolean success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        assertTrue(success);
        Class<?> clazz1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz1);
        assertEquals("com.tcdng.unify.core.runtime.NaijaHello", clazz1.getName());
        assertTrue(clazz1.isAnnotationPresent(Language.class));
        Language la = clazz1.getAnnotation(Language.class);
        assertNotNull(la);
        assertEquals("Naija", la.value());

        success = rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 10));
        assertTrue(success);
        Class<?> clazz2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz2);
        assertEquals("com.tcdng.unify.core.runtime.NaijaHello", clazz2.getName());
        assertTrue(clazz2.isAnnotationPresent(Language.class));
        la = clazz2.getAnnotation(Language.class);
        assertNotNull(la);
        assertEquals("Naija", la.value());

        assertFalse(clazz1.equals(clazz2));
    }

    @Test
    public void testGetSavedJavaClassChangedClassLoader() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.EnglishHello", engHelloSrc, 1));
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        Class<?> engClass1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.EnglishHello");
        Class<?> naijaClazz1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(engClass1);
        assertNotNull(naijaClazz1);
        assertFalse(engClass1.equals(naijaClazz1));

        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 2));

        Class<?> engClass2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.EnglishHello");
        Class<?> naijaClazz2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertFalse(engClass1.equals(engClass2));
        assertFalse(naijaClazz1.equals(naijaClazz2));
    }

    @Test
    public void testGetSavedJavaClassDiffGroup() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        Class<?> naijaClazz1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");

        rjcm.compileAndSaveJavaClass("languageGrp2",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 2));
        Class<?> naijaClazz2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        Class<?> naijaClazz3 = rjcm.getSavedJavaClass("languageGrp2", "com.tcdng.unify.core.runtime.NaijaHello");

        assertTrue(naijaClazz1.equals(naijaClazz2));
        assertFalse(naijaClazz1.equals(naijaClazz3));
    }

    @Test
    public void testGetSavedJavaClassFunction() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        Class<?> clazz = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz);
        Hello hello = (Hello) ReflectUtils.newInstance(clazz);
        assertEquals("How now?", hello.sayHello());
    }

    @Test
    public void testGetSavedJavaClassFunctionChangedVersion() throws Exception {
        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1, 1));
        Class<?> clazz1 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz1);
        Hello hello = (Hello) ReflectUtils.newInstance(clazz1);
        assertEquals("How now?", hello.sayHello());

        rjcm.compileAndSaveJavaClass("languageGrp1",
                new StringJavaClassSource("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc2, 2));
        Class<?> clazz2 = rjcm.getSavedJavaClass("languageGrp1", "com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz2);
        hello = (Hello) ReflectUtils.newInstance(clazz2);
        assertEquals("How far?", hello.sayHello());
    }

    @Override
    protected void onSetup() throws Exception {
        rjcm = (RuntimeJavaClassManager) getComponent(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        rjcm.clearCachedSaveJavaClasses("languageGrp1");
        deleteAll(SingleVersionBlob.class);
    }

}
