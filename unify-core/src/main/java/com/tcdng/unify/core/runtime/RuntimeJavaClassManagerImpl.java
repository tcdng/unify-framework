/*
 * Copyright 2018-2023 The Code Department.
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

import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.ResourceFinder;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.codehaus.janino.JavaSourceClassLoader;
import org.codehaus.janino.SimpleCompiler;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation of runtime java class manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER)
public class RuntimeJavaClassManagerImpl extends AbstractRuntimeJavaClassManager {

    private Map<String, Class<?>> classByName;

    public RuntimeJavaClassManagerImpl() {
        reset();
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
    public <T> List<Class<? extends T>> compileAndLoadJavaClasses(Class<T> typeClass, List<JavaClassSource> sourceList)
            throws UnifyException {
        try {
            return innerCompileAndLoadClasses(typeClass, sourceList);
        } catch (UnifyException e) {
            for (JavaClassSource source : sourceList) {
                logDebug("@Source: \n{0}", source.getSrc());
            }

            throw e;
        }
    }

    @Override
    public synchronized void reset() {
        classByName = new ConcurrentHashMap<String, Class<?>>();
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

    private synchronized Class<?> innerCompileAndLoadClass(String className, Reader reader) throws UnifyException {
        try {
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.setParentClassLoader(getClass().getClassLoader());
            compiler.cook(reader);
            Class<?> clazz = compiler.getClassLoader().loadClass(className);
            classByName.put(className, clazz);
            return clazz;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> List<Class<? extends T>> innerCompileAndLoadClasses(Class<T> typeClass, List<JavaClassSource> sourceList)
            throws UnifyException {
        List<Class<? extends T>> resultList = new ArrayList<Class<? extends T>>();
        try {
            List<String> classNameList = new ArrayList<String>();
            StringResource[] sourceResources = new StringResource[sourceList.size()];
            for (int i = 0; i < sourceResources.length; i++) {
                JavaClassSource javaSource = sourceList.get(i);
                classNameList.add(javaSource.getClassName());
                sourceResources[i] = new StringResource(javaSource.getClassName().replace('.', '/') + ".java",
                        javaSource.getSrc());
            }
            
            MapResourceFinder resourceFinder = new MapResourceFinder(sourceResources);
            ClassLoader loader = new JavaSourceClassLoader(getClass().getClassLoader(), resourceFinder, null);

            for (String className : classNameList) {
                Class<? extends T> clazz = (Class<? extends T>) loader.loadClass(className);
                classByName.put(className, clazz);
                resultList.add(clazz);
            }
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }

        return resultList;
    }

    private class MapResourceFinder extends ResourceFinder {
        private Map<String, StringResource> map;

        public MapResourceFinder(StringResource[] sources) {
            this.map = new HashMap<String, StringResource>();
            for (StringResource src : sources) {
                this.map.put(src.getFileName(), src);
            }
        }

        @Override
        public Resource findResource(final String resourceName) {
            return map.get(resourceName);
        }
    }

}
