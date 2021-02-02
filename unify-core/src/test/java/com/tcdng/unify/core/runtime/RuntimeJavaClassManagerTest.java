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
            + "public class EnglishHello implements Hello {" + " public String hello() {" + "     return \"Hello\";"
            + " }" + "}";

    private final String naijaHelloSrc = "package com.tcdng.unify.core.runtime;" + "@Language(\"Naija\")"
            + "public class NaijaHello implements Hello {" + " public String hello() {" + "     return \"How now?\";"
            + " }" + "}";

    private final String hausaHelloSrc = "package com.tcdng.unify.core.runtime;" + "@Language(\"Naija\")"
            + "public class NaijaHello implements Hello {" + " public String hello() {" + "     return \"Yaya dai?\";"
            + " }" + "}";

    private final String naijaPersonSrc = "package com.tcdng.unify.core.runtime;"
            + "import com.tcdng.unify.core.runtime.NaijaHello;" + "public class NaijaPerson implements Person {"
            + " private Hello hello;" + " public NaijaPerson() {" + "     hello = new NaijaHello();" + " }"
            + " public String sayHello() {" + "     return hello.hello();" + " }" + "}";

    @Test
    public void testCompileAndLoadClassString() throws Exception {
        rjcm.clearClassLoader();
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
        rjcm.clearClassLoader();
        Class<?> clazz = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc);
        assertNotNull(clazz);
        Hello hello = (Hello) ReflectUtils.newInstance(clazz);
        assertEquals("How now?", hello.hello());
    }

    @Test
    public void testCompileAndLoadClassAsProvider() throws Exception {
        Class<?> clazz1 = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc);
        assertNotNull(clazz1);
        Class<?> clazz2 = ReflectUtils.classForName("com.tcdng.unify.core.runtime.NaijaHello");
        assertNotNull(clazz2);
        assertSame(clazz1, clazz2);
    }

    @Test
    public void testCompileAndLoadClassStringWithRef() throws Exception {
        rjcm.clearClassLoader();
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc);

        Class<?> clazz = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaPerson", naijaPersonSrc);
        assertNotNull(clazz);
        assertEquals("com.tcdng.unify.core.runtime.NaijaPerson", clazz.getName());
        Person person = (Person) ReflectUtils.newInstance(clazz);
        assertEquals("How now?", person.sayHello());
    }

    @Test
    public void testCompileAndLoadClassStringWithNewRef() throws Exception {
        rjcm.clearClassLoader();
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc);
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", hausaHelloSrc); // Test child-first

        Class<?> clazz = rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaPerson", naijaPersonSrc);
        assertNotNull(clazz);
        assertEquals("com.tcdng.unify.core.runtime.NaijaPerson", clazz.getName());
        Person person = (Person) ReflectUtils.newInstance(clazz);
        assertEquals("Yaya dai?", person.sayHello());
    }

    @Test
    public void testCompileAndLoadClassLoaderDepth() throws Exception {
        rjcm.clearClassLoader();
        assertEquals(0, rjcm.getClassLoaderDepth());
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", naijaHelloSrc);
        assertEquals(1, rjcm.getClassLoaderDepth());
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.EnglishHello", engHelloSrc);
        assertEquals(1, rjcm.getClassLoaderDepth());
        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaPerson", naijaPersonSrc);
        assertEquals(1, rjcm.getClassLoaderDepth());

        rjcm.compileAndLoadJavaClass("com.tcdng.unify.core.runtime.NaijaHello", hausaHelloSrc);
        assertEquals(2, rjcm.getClassLoaderDepth());
    }

    @Override
    protected void onSetup() throws Exception {
        rjcm = (RuntimeJavaClassManager) getComponent(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER);
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
