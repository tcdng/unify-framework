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

import org.codehaus.janino.SimpleCompiler;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.system.SingleVersionLargeObjectService;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.LockUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of runtime java class manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER)
public class RuntimeJavaClassManagerImpl extends AbstractRuntimeJavaClassManager {

    private static final String RUNTIMECLASS_APPLICATION = "app::runtimeJavaClass";

    private final FactoryMap<String, GroupSavedJavaClassLoader> groupSavedJavaClassLoaders =
            new FactoryMap<String, GroupSavedJavaClassLoader>() {
                @Override
                protected GroupSavedJavaClassLoader create(String groupName, Object... params) throws Exception {
                    return new GroupSavedJavaClassLoader(groupName);
                }
            };

    // Do NOT use injection here because the default business proxy
    // generator uses this component. Injection causes the
    // SingleVersionLargeObjectService instance to be instantiated before its
    // corresponding proxy type can be created for it. Use #getComponent() at point
    // of usage instead.
    // @Configurable
    // private SingleVersionLargeObjectService singleVersionLargeObjectService;

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
    public Class<?> compileAndLoadJavaClass(String className, String string) throws UnifyException {
        return innerCompileAndLoadClass(className, new StringReader(string));
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
    public boolean compileAndSaveJavaClass(String groupName, InputStreamJavaClassSource inputStreamJavaClassSource)
            throws UnifyException {
        InputStreamReader reader = new InputStreamReader(inputStreamJavaClassSource.getSource());
        try {
            return innerCompileAndSaveClass(groupName, inputStreamJavaClassSource, reader);
        } finally {
            IOUtils.close(reader);
        }
    }

    @Override
    public boolean compileAndSaveJavaClass(String groupName, ReaderJavaClassSource readerJavaClassSource)
            throws UnifyException {
        return innerCompileAndSaveClass(groupName, readerJavaClassSource, readerJavaClassSource.getSource());
    }

    @Override
    public boolean compileAndSaveJavaClass(String groupName, StringJavaClassSource stringJavaClassSource)
            throws UnifyException {
        return innerCompileAndSaveClass(groupName, stringJavaClassSource,
                new StringReader(stringJavaClassSource.getSource()));
    }

    @Override
    public boolean compileAndSaveJavaClass(String groupName, FileJavaClassSource fileJavaClassSource)
            throws UnifyException {
        FileReader reader = null;
        try {
            reader = new FileReader(fileJavaClassSource.getSource());
            return innerCompileAndSaveClass(groupName, fileJavaClassSource, reader);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        } finally {
            IOUtils.close(reader);
        }
    }

    @Override
    public Class<?> getSavedJavaClass(String groupName, String className) throws UnifyException {
        try {
            return Class.forName(groupName, true, groupSavedJavaClassLoaders.get(groupName));
        } catch (ClassNotFoundException e) {
            throwOperationErrorException(e);
        }

        return null;
    }

    @Override
    public long getSavedJavaClassVersion(String groupName, String className) throws UnifyException {
        SingleVersionLargeObjectService svlos =
                (SingleVersionLargeObjectService) getComponent(
                        ApplicationComponents.APPLICATION_SINGLEVERSIONLOBSERVICE);
        return svlos.getBlobVersion(RUNTIMECLASS_APPLICATION, groupName, className);
    }

    private class GroupSavedJavaClassLoader extends ClassLoader {

        private String groupName;
        
        
        public GroupSavedJavaClassLoader(String groupName) {
            this.groupName = groupName;
        }


        @Override
        protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
            // We are not checking parent so we implement loadClass()
            Class<?> clazz = findLoadedClass(className);
            if (clazz != null) {
                return clazz;
            }
            
            SecurityManager sm = System.getSecurityManager();
            int pIndex = className.lastIndexOf('.');
            if (pIndex >= 0) {
                sm.checkPackageDefinition(className.substring(0, pIndex));
            }
            
            byte[] byteCode = null;
            try {
                SingleVersionLargeObjectService svlos =
                        (SingleVersionLargeObjectService) getComponent(
                                ApplicationComponents.APPLICATION_SINGLEVERSIONLOBSERVICE);
                byteCode = svlos.retrieveBlob(RUNTIMECLASS_APPLICATION, groupName, className);
            } catch (UnifyException e) {
                throw new ClassNotFoundException(className, e);
            }
            
            if (byteCode == null) {
                throw new ClassNotFoundException(className);
            }
            
            clazz = defineClass(className, byteCode, 0, byteCode.length);
            if (resolve) {
                resolveClass(clazz);
            }
            
            return clazz;
        }

    }

    private Class<?> innerCompileAndLoadClass(String className, Reader reader) throws UnifyException {
        try {
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(reader);
            return compiler.getClassLoader().loadClass(className);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }
    }

    private boolean innerCompileAndSaveClass(String groupName, AbstractJavaClassSource srcDetails, Reader reader)
            throws UnifyException {
        final String className = srcDetails.getClassName();
        final long version = srcDetails.getVersion();
        SingleVersionLargeObjectService svlos =
                (SingleVersionLargeObjectService) getComponent(
                        ApplicationComponents.APPLICATION_SINGLEVERSIONLOBSERVICE);
        if (svlos.getBlobVersion(RUNTIMECLASS_APPLICATION, groupName, className) < version) {
            synchronized (LockUtils.getStringLockObject(StringUtils.dotify(RUNTIMECLASS_APPLICATION, groupName))) {
                if (svlos.getBlobVersion(RUNTIMECLASS_APPLICATION, groupName,
                        className) < version) {
                    synchronized (LockUtils
                            .getStringLockObject(StringUtils.dotify(RUNTIMECLASS_APPLICATION, groupName))) {
                        try {
                            SimpleCompiler compiler = new SimpleCompiler();
                            compiler.cook(reader);
                            byte[] byteCode = compiler.getBytecodes().get(className);
                            boolean result = svlos.storeBlob(RUNTIMECLASS_APPLICATION,
                                    groupName, className, byteCode, version);
                            if (result) {
                                // Invalidate group class loader
                                groupSavedJavaClassLoaders.remove(groupName);
                            }

                            return result;
                        } catch (Exception e) {
                            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
                        }
                    }
                }
            }
        }

        return false;
    }
}
