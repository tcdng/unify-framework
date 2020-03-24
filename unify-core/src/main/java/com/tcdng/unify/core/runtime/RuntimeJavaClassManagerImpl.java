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
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Default implementation of runtime java class manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_RUNTIMEJAVACLASSMANAGER)
public class RuntimeJavaClassManagerImpl extends AbstractRuntimeJavaClassManager {

    private static final long GET_COMPILER_TIMEOUT = 8000; // 8 Seconds

    @Configurable("32")
    private int maxCompilers;

    private SimpleCompilerPool simpleCompilerPool;

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
    protected void onInitialize() throws UnifyException {
        simpleCompilerPool = new SimpleCompilerPool();
    }

    private Class<?> innerCompileAndLoadClass(String className, Reader reader) throws UnifyException {
        SimpleCompiler compiler = simpleCompilerPool.borrowObject();
        try {
            compiler.cook(reader);
            return compiler.getClassLoader().loadClass(className);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        } finally {
            simpleCompilerPool.returnObject(compiler);
        }
    }

    private class SimpleCompilerPool extends AbstractPool<SimpleCompiler> {

        public SimpleCompilerPool() {
            super(GET_COMPILER_TIMEOUT, 0, maxCompilers);
        }

        @Override
        protected SimpleCompiler createObject(Object... params) throws Exception {
            return new SimpleCompiler();
        }

        @Override
        protected void destroyObject(SimpleCompiler simpleCompiler) {

        }
    }
}
