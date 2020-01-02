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
package com.tcdng.unify.web.util;

import java.net.URL;

import javax.servlet.ServletContext;

import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.TypeUtils;

/**
 * Provides utility methods for type information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class WebTypeUtils extends TypeUtils {

    private static TypeRepository servletContextTypeRepository;

    public static TypeRepository getTypeRepositoryFromServletContext(ServletContext servletContext)
            throws UnifyException {
        if (servletContextTypeRepository == null) {
            synchronized (WebTypeUtils.class) {
                if (servletContextTypeRepository == null) {
                    try {
                        AnnotationDB classpathDB = new AnnotationDB();
                        classpathDB.setScanFieldAnnotations(false);
                        classpathDB.setScanMethodAnnotations(false);
                        classpathDB.setScanParameterAnnotations(false);

                        URL[] urls = null;
                        URL classPathUrl = WarUrlFinder.findWebInfClassesPath(servletContext);
                        URL[] libUrls = WarUrlFinder.findWebInfLibClasspaths(servletContext);
                        if (classPathUrl != null) {
                            urls = new URL[libUrls.length + 1];
                            int i = 0;
                            for (; i < libUrls.length; i++) {
                                urls[i] = libUrls[i];
                            }
                            urls[i] = classPathUrl;
                        } else {
                            urls = libUrls;
                        }
                        classpathDB.scanArchives(urls);
                        servletContextTypeRepository = new TypeRepositoryImpl(classpathDB);
                    } catch (Exception e) {
                        throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
                    }
                }
            }
        }

        return servletContextTypeRepository;
    }

}
