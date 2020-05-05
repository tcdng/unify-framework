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

/**
 * Java class source object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class AbstractJavaClassSource {

    private String className;
    
    private long version;

    public AbstractJavaClassSource(String className, long version) {
        this.className = className;
        this.version = version;
    }

    public String getClassName() {
        return className;
    }

    public long getVersion() {
        return version;
    }
}
