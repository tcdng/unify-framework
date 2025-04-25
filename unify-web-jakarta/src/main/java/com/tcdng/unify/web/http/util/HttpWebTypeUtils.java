/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.http.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.scannotation.AnnotationDB;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.TypeUtils;

import jakarta.servlet.ServletContext;

/**
 * Provides utility methods for type information.
 * Credits to <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * 
 * @author The Code Department
 * @since 4.1
 */
public class HttpWebTypeUtils extends TypeUtils {

    private static TypeRepository servletContextTypeRepository;

    public static TypeRepository getTypeRepositoryFromServletContext(ServletContext servletContext)
            throws UnifyException {
        if (servletContextTypeRepository == null) {
            synchronized (HttpWebTypeUtils.class) {
                if (servletContextTypeRepository == null) {
                    try {
                        AnnotationDB classpathDB = new AnnotationDB();
                        classpathDB.setScanFieldAnnotations(false);
                        classpathDB.setScanMethodAnnotations(false);
                        classpathDB.setScanParameterAnnotations(false);

                        URL classPathUrl = HttpWebTypeUtils.findWebInfClassesPath(servletContext);
                        List<URL> urls = HttpWebTypeUtils.findWebInfLibClasspaths(servletContext);
                        if (classPathUrl != null) {
                            urls.add(classPathUrl);
                        }

                        classpathDB.scanArchives(DataUtils.toArray(URL.class, urls));
                        servletContextTypeRepository = new TypeRepositoryImpl(classpathDB);
                    } catch (Exception e) {
                        throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
                    }
                }
            }
        }

        return servletContextTypeRepository;
    }

    public static List<URL> findWebInfLibClasspaths(ServletContext servletContext) {
        List<URL> list = new ArrayList<URL>();
        Set<String> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
        if (libJars != null) {
            for (Object jar : libJars) {
                try {
                    list.add(servletContext.getResource((String) jar));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return list;
    }

    public static URL findWebInfClassesPath(ServletContext servletContext) {
        String path = servletContext.getRealPath("/WEB-INF/classes");
        if (path == null) {
            return null;
        }
        
        File fp = new File(path);
        if (fp.exists() == false) {
            return null;
        }
        
        try {
            return fp.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
