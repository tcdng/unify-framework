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

package com.tcdng.unify.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Compiler utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class CompilerUtils {

    private static final CompilerUtilsClassLoader compilerUtilsClassLoader = new CompilerUtilsClassLoader();

    private CompilerUtils() {

    }

    /**
     * Compiles and loads a java class.
     * 
     * @param className
     *            the java class name
     * @param source
     *            the java source
     * @return the compiled and loaded class
     * @throws UnifyException
     *             if an error occurs
     */
    public static Class<?> compileAndLoadClass(String className, String source) throws UnifyException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollectors = new DiagnosticCollector<JavaFileObject>();
        CompiledClassForwardingJavaFileManager inJavaFileManager = new CompiledClassForwardingJavaFileManager(
                compiler.getStandardFileManager(diagnosticCollectors, null, null));
        try {
            CompilationTask task = compiler.getTask(null, inJavaFileManager, diagnosticCollectors, null, null,
                    Arrays.asList(new SourceJavaFileObject(className, source)));
            if (!task.call()) {
                List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticCollectors.getDiagnostics();
                if (!DataUtils.isBlank(diagnostics)) {
                    Diagnostic<? extends JavaFileObject> diagnostic = diagnostics.get(0);
                    throw new UnifyException(UnifyCoreErrorConstants.JAVA_SOURCE_COMPILATION_ERROR,
                            String.format("Compile error on line %d in %s%n", diagnostic.getLineNumber(),
                                    diagnostic.getSource().toUri()));
                } else {
                    throw new UnifyException(UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
                }
            }

            synchronized (CompilerUtils.class) {
                compilerUtilsClassLoader.setCompiledJavaFileObject(inJavaFileManager);
                return compilerUtilsClassLoader.loadClass(className);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        } finally {
            try {
                inJavaFileManager.close();
            } catch (IOException e) {
            }
        }
    }

    private static class CompilerUtilsClassLoader extends ClassLoader {

        private CompiledClassForwardingJavaFileManager inJavaFileManager;

        public void setCompiledJavaFileObject(CompiledClassForwardingJavaFileManager inJavaFileManager) {
            this.inJavaFileManager = inJavaFileManager;
        }

        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            byte[] classImg = inJavaFileManager.getCompiledObject().getBytes();
            return defineClass(className, classImg, 0, classImg.length);
        }
    }

    private static class CompiledJavaFileObject extends SimpleJavaFileObject {

        private ByteArrayOutputStream baos;

        protected CompiledJavaFileObject(String className, JavaFileObject.Kind kind) {
            super(URI.create("string:///" + className.replaceAll("\\.", "/") + kind.extension), kind);
            baos = new ByteArrayOutputStream();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return baos;
        }

        public byte[] getBytes() {
            return baos.toByteArray();
        }
    }

    private static class SourceJavaFileObject extends SimpleJavaFileObject {
        private String source;

        protected SourceJavaFileObject(String className, String source) {
            super(URI.create("string:///" + className.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return source;
        }
    }

    private static class CompiledClassForwardingJavaFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private CompiledJavaFileObject compiledObject;

        public CompiledClassForwardingJavaFileManager(StandardJavaFileManager javaFileManager) {
            super(javaFileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
                JavaFileObject.Kind kind, FileObject sibling) {
            compiledObject = new CompiledJavaFileObject(className, kind);
            return compiledObject;
        }

        public CompiledJavaFileObject getCompiledObject() {
            return compiledObject;
        }
    }
}
