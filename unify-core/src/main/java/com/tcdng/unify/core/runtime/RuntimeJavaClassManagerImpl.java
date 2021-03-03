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

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ICompiler;
import org.codehaus.commons.compiler.ICompilerFactory;
import org.codehaus.commons.compiler.util.resource.MapResourceCreator;
import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.codehaus.janino.SimpleCompiler;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation of runtime java class manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER)
public class RuntimeJavaClassManagerImpl extends AbstractRuntimeJavaClassManager {

    private Map<String, Class<?>> classByName;

    private RuntimeClassLoader runtimeClassLoader;

    private int classLoaderDepth;

    public RuntimeJavaClassManagerImpl() {
        classByName = new ConcurrentHashMap<String, Class<?>>();
    }

    @Override
    public Class<?> classForName(String className) throws UnifyException {
        return classByName.get(className);
    }

    @Override
    public Class<?> compileAndLoadJavaClass(String className, InputStream is) throws UnifyException {
        InputStreamReader reader = new InputStreamReader(is);
        try {
            return innerCompileAndLoadClass(className, reader);
        } finally {
            IOUtils.close(reader);
        }
    }

    @Override
    public Class<?> compileAndLoadJavaClass(String className, Reader reader) throws UnifyException {
        return innerCompileAndLoadClass(className, reader);
    }

    @Override
    public Class<?> compileAndLoadJavaClass(String className, String src) throws UnifyException {
        try {
            return innerCompileAndLoadClass(className, new StringReader(src));
        } catch (UnifyException e) {
            logDebug("@Source: \n{0}", src);
            throw e;
        }
    }

    @Override
    public Class<?> compileAndLoadJavaClass(String className, File file) throws UnifyException {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            return innerCompileAndLoadClass(className, reader);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        } finally {
            IOUtils.close(reader);
        }
    }

    @Override
    public Class<?> compileAndLoadJavaClass(JavaClassSource source) throws UnifyException {
        return compileAndLoadJavaClass(source.getClassName(), source.getSrc());
    }

    @Override
    public List<Class<?>> compileAndLoadJavaClasses(List<JavaClassSource> sourceList) throws UnifyException {
        try {
            return innerCompileAndLoadClasses(sourceList);
        } catch (UnifyException e) {
            for(JavaClassSource source: sourceList) {
                logDebug("@Source: \n{0}", source.getSrc());
            }
            
            throw e;
        }
    }

    @Override
    public int getClassLoaderDepth() {
        return classLoaderDepth;
    }

    @Override
    public synchronized void clearClassLoader() {
        classByName.clear();
        runtimeClassLoader = null;
        classLoaderDepth = 0;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();
        ReflectUtils.registerClassForNameProvider(this);
    }

    @Override
    protected void onTerminate() throws UnifyException {
        ReflectUtils.unregisterClassForNameProvider(this);
        super.onTerminate();
    }

    private class RuntimeClassLoader extends ClassLoader {

        private Map<String, byte[]> byteCodes;

        public RuntimeClassLoader() {
            byteCodes = new HashMap<String, byte[]>();
        }

        public RuntimeClassLoader(ClassLoader parent) {
            super(parent);
            byteCodes = new HashMap<String, byte[]>();
        }

        public RuntimeClassLoader setByteCodes(String className, byte[] byteCode) {
            byteCodes.put(className, byteCode);
            return this;
        }

        // Implement child-first
        @Override
        protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
            Class<?> loadedClass = findLoadedClass(className);
            if (loadedClass == null) {
                try {
                    loadedClass = findClass(className);
                } catch (ClassNotFoundException e) {
                    loadedClass = super.loadClass(className, resolve);
                }
            }

            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }

        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            byte[] byteCode = byteCodes.remove(className);
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                int pIndex = className.lastIndexOf('.');
                if (pIndex >= 0) {
                    sm.checkPackageDefinition(className.substring(0, pIndex));
                }
            }

            if (byteCode == null) {
                throw new ClassNotFoundException(className);
            }

            Class<?> clazz = defineClass(className, byteCode, 0, byteCode.length);
            byteCode = null;
            return clazz;
        }

    }

    private synchronized Class<?> innerCompileAndLoadClass(String className, Reader reader) throws UnifyException {
        try {
            RuntimeClassLoader loader = getRuntimeClassLoader(className);
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.setParentClassLoader(loader);
            compiler.cook(reader);
            byte[] byteCode = compiler.getBytecodes().get(className);
            Class<?> clazz = loader.setByteCodes(className, byteCode).loadClass(className);
            classByName.put(className, clazz);
            return clazz;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }
    }

    private synchronized List<Class<?>> innerCompileAndLoadClasses(List<JavaClassSource> sourceList)
            throws UnifyException {
        List<Class<?>> resultList = new ArrayList<Class<?>>();
        try {
            ICompilerFactory compilerFactory = CompilerFactoryFactory.getDefaultCompilerFactory();
            ICompiler compiler = compilerFactory.newCompiler();

            List<String> classNameList = new ArrayList<String>();
            Resource[] sourceResources = new Resource[sourceList.size()];
            for (int i = 0; i < sourceResources.length; i++) {
                JavaClassSource javaSource = sourceList.get(i);
                classNameList.add(javaSource.getClassName());
                sourceResources[i] = new StringResource(javaSource.getClassName().replace('.', '/') + ".java",
                        javaSource.getSrc());
            }

            Map<String, byte[]> compiledClasses = new HashMap<String, byte[]>();
            compiler.setClassFileCreator(new MapResourceCreator(compiledClasses));
            compiler.compile(sourceResources);
            RuntimeClassLoader loader = getRuntimeClassLoader(DataUtils.toArray(String.class, classNameList));
            for (Map.Entry<String, byte[]> entry : compiledClasses.entrySet()) {
                int lastIndex = entry.getKey().lastIndexOf('.');
                loader.setByteCodes(entry.getKey().substring(0, lastIndex).replace('/', '.'), entry.getValue());
            }

            for (String className : classNameList) {
                Class<?> clazz = loader.loadClass(className);
                classByName.put(className, clazz);
                resultList.add(clazz);
            }
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }

        return resultList;
    }

    private RuntimeClassLoader getRuntimeClassLoader(String... classNames) {
        if (runtimeClassLoader == null) {
            runtimeClassLoader = new RuntimeClassLoader();
            classLoaderDepth++;
        } else {
            for (String className : classNames) {
                if (classByName.containsKey(className)) {
                    runtimeClassLoader = new RuntimeClassLoader(runtimeClassLoader);
                    classLoaderDepth++;
                    break;
                }
            }
        }

        return runtimeClassLoader;
    }

}
