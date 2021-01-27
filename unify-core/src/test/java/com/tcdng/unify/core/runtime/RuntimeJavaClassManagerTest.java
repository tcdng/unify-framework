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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
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
    public void testCompileAndLoadClassAsProvider() throws Exception {
        Class<?> clazz1 = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc1);
        assertNotNull(clazz1);
        Class<?> clazz2 = ReflectUtils.classForName("com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz2);
        assertSame(clazz1, clazz2);
    }

    @Override
    protected void onSetup() throws Exception {
        rjcm = (RuntimeJavaClassManager) getComponent(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER);
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
