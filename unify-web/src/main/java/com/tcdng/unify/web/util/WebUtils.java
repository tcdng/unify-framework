/*
 * Copyright 2018 The Code Department
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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.TypeUtils;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.constant.ShortcutFlagConstants;

/**
 * Web utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class WebUtils {

    public static final String DEFAULT_THEME_PATH = "/web/themes/farko";

    private WebUtils() {

    }

    public static String expandThemeTag(String resouceName) throws UnifyException {
        return WebUtils.expandThemeTag(resouceName, null);
    }

    public static String expandThemeTag(String resouceName, String themePath) throws UnifyException {
        if (TokenUtils.isThemeTag(resouceName)) {
            resouceName = TokenUtils.extractTokenValue(resouceName);
            if (StringUtils.isBlank(themePath)) {
                themePath = WebUtils.DEFAULT_THEME_PATH;
            }

            if (!StringUtils.isBlank(themePath)) {
                if (themePath.endsWith("/")) {
                    if (resouceName.startsWith("/")) {
                        return themePath + resouceName.substring("/".length());
                    }

                    return themePath + resouceName;
                }

                if (resouceName.startsWith("/")) {
                    return themePath + resouceName;
                }

                return themePath + "/" + resouceName;
            }
        }

        return resouceName;
    }

    public static String generateBeanIndexedPathFromPath(String path, Object index) throws UnifyException {
        if (index != null) {
            int intIndex = path.lastIndexOf('/');
            if (intIndex >= 0) {
                StringBuilder sb = new StringBuilder(path);
                sb.insert(intIndex, ':');
                sb.insert(intIndex + 1, index);
                return sb.toString();
            }
        }

        return path;
    }

    public static String extractPathFromBeanIndexedPath(String beanIndexedPath) throws UnifyException {
        int index0 = beanIndexedPath.lastIndexOf(':');
        if (index0 >= 0) {
            int index1 = beanIndexedPath.lastIndexOf('/');
            if (index1 > index0) {
                StringBuilder sb = new StringBuilder(beanIndexedPath);
                sb.replace(index0, index1, "");
                return sb.toString();
            } else {
                return beanIndexedPath.substring(0, index0);
            }
        }

        return beanIndexedPath;
    }

    public static String extractBeanIdFromBeanIndexedPath(String beanIndexedPath) throws UnifyException {
        int colIndex = beanIndexedPath.lastIndexOf(':');
        int slashIndex = beanIndexedPath.lastIndexOf('/');
        if (colIndex >= 0) {
            if (slashIndex > colIndex) {
                return beanIndexedPath.substring(0, slashIndex);
            } else {
                return beanIndexedPath;
            }
        }

        return beanIndexedPath.substring(0, slashIndex);
    }

    /**
     * Encodes a shortcut string.
     * 
     * @param shortcut
     *            the shortcut string to encode
     * @return the encodede shortcut
     * @throws UnifyException
     *             if an error occurs
     */
    public static String encodeShortcut(String shortcut) throws UnifyException {
        String encodedShortcut = null;
        if (!StringUtils.isBlank(shortcut)) {
            int encoded = 0;
            String[] elements = shortcut.toUpperCase().split("\\+");
            boolean validShortcut = false;
            for (int i = 0; i < elements.length; i++) {
                if ("SHIFT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.SHIFT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.SHIFT;
                } else if ("CTRL".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.CTRL) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.CTRL;
                } else if ("ALT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.ALT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.ALT;
                } else {
                    if (!(validShortcut = ((encoded & 0x00FF) == 0) && (elements[i].length() == 1))) {
                        break;
                    }
                    encoded += elements[i].charAt(0);
                }
            }
            if (!validShortcut) {
                throw new UnifyException(UnifyWebErrorConstants.KEYCOMBO_IS_INVALID, shortcut);
            }
            encodedShortcut = String.valueOf(encoded);
        }
        return encodedShortcut;
    }

    public static TypeRepository buildTypeRepositoryFromServletContext(ServletContext servletContext)
            throws UnifyException {
        try {
            AnnotationDB servletContextDB = new AnnotationDB();
            servletContextDB.setScanFieldAnnotations(false);
            servletContextDB.setScanMethodAnnotations(false);
            servletContextDB.setScanParameterAnnotations(false);

            URL[] urls = WarUrlFinder.findWebInfLibClasspaths(servletContext);
            servletContextDB.scanArchives(urls);

            URL classPathUrl = WarUrlFinder.findWebInfClassesPath(servletContext);
            if (classPathUrl != null) {
                servletContextDB.scanArchives(classPathUrl);
            }
            return new TypeUtils.TypeRepositoryImpl(servletContextDB);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.ANNOTATIONUTIL_ERROR);
        }
    }
}
