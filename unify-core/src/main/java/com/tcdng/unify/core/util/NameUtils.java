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

import com.tcdng.unify.core.database.Entity;

/**
 * Provides utility methods for naming.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class NameUtils {

    private NameUtils() {

    }

    /**
     * Returns the name of a entity type.
     * 
     * @param entityClass
     *            the entity type
     * @return the entity type name
     */
    public static String getName(Class<? extends Entity> entityClass) {
        String name = entityClass.getSimpleName();
        if (name.endsWith("Data")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }

    /**
     * Returns the description of a entity type.
     * 
     * @param entityClass
     *            the entity type
     * @return the entity type description
     */
    public static String getDescription(Class<? extends Entity> entityClass) {
        String description = entityClass.getSimpleName();
        if (description.endsWith("Data")) {
            description = description.substring(0, description.length() - 4);
        }
        return NameUtils.describeName(description);
    }

    /**
     * Describes a name. Breaks down a name into spaced words by detecting a
     * lowercase-uppercase character junction and inserting the space character, '
     * ', in between. The first character of the name is always capitalized. For
     * instance
     * 
     * <pre>
     *     age -&gt; Age
     *     authorId -&gt; Author Id
     *     numberOfDays -&gt; Number Of Days
     * </pre>
     * 
     * @param name
     *            the name to describe
     * @return the name description
     */
    public static String describeName(String name) {
        int len = 0;
        if (name != null && (len = name.length()) > 0) {
            if (name.startsWith("data.")) {
                name = name.substring("data.".length());
                len = name.length();
            }

            boolean isPrevLowerCase = false;
            boolean isPrevSpace = false;
            StringBuilder sb = new StringBuilder();
            sb.append(Character.toUpperCase(name.charAt(0)));
            for (int i = 1; i < len; i++) {
                char ch = name.charAt(i);
                if (ch == '.') {
                    ch = ' ';
                }

                if (Character.isUpperCase(ch) && isPrevLowerCase) {
                    sb.append(' ');
                } else {
                    isPrevLowerCase = Character.isLowerCase(ch);
                }

                if (isPrevSpace) {
                    ch = Character.toUpperCase(ch);
                }

                isPrevSpace = Character.isWhitespace(ch);
                sb.append(ch);
            }
            return sb.toString();
        }
        return name;
    }

    public static String describeCode(String code) {
        int len = 0;
        if (code != null && (len = code.length()) > 0) {
            StringBuilder sb = new StringBuilder();
            boolean isPrevSpace = true;
            for (int i = 0; i < len; i++) {
                char ch = code.charAt(i);
                if (ch == '_') {
                    if (sb.length() > 0) {
                        sb.append(' ');
                        isPrevSpace = true;
                    }
                } else if (Character.isWhitespace(ch)) {
                    sb.append(ch);
                    isPrevSpace = true;
                } else {
                    if (isPrevSpace) {
                        sb.append(Character.toUpperCase(ch));
                    } else {
                        sb.append(Character.toLowerCase(ch));
                    }
                    isPrevSpace = false;
                }
            }
            return sb.toString();
        }
        return code;
    }

    public static String getFilenameByCategory(String template, String templateClass) {
        int index = template.lastIndexOf('.');
        if (index >= 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(template.substring(0, index)).append('_').append(templateClass).append(template.substring(index));
            return sb.toString();
        }
        return template;
    }

    public static String getComponentMethodName(String componentName, String methodName) {
        return componentName + '.' + methodName;
    }
}
