/*
 * Copyright (c) 2018-2025 The Code Department.
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

public class TypeInfo {

    private String canonicalName;

    private String packageName;

    private String simpleName;

    private boolean java;

    private boolean lang;

    public TypeInfo(Class<?> clazz) {
        this(clazz.getCanonicalName());
    }

    public TypeInfo(String canonicalName) {
        this.canonicalName = canonicalName;
        int index = canonicalName.lastIndexOf('.');
        if (index > 0) {
            this.packageName = canonicalName.substring(0, index);
            this.simpleName = canonicalName.substring(index + 1);
            lang = "java.lang".equals(packageName);
        } else {
            this.packageName = "";
            this.simpleName = canonicalName;
            lang = true;
        }

        java = canonicalName.startsWith("java.");
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public boolean isJava() {
        return java;
    }

    public boolean isLang() {
        return lang;
    }
}
