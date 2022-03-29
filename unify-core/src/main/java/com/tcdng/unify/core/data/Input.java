/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.data;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * An input data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class Input<T> {

    protected T value;

    private String name;

    private String description;

    private String editor;

    private Class<T> type;

    private boolean mandatory;

    public Input(Class<T> type, String name, String description, String editor, boolean mandatory) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.editor = editor;
        this.mandatory = mandatory;
    }

    public Class<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getStringValue() throws UnifyException {
        return DataUtils.convert(String.class, value);
    }
    
    public void setStringValue(String value) throws UnifyException  {
        this.value = DataUtils.convert(type, value);
    }

    public Object getTypeValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }

    public String getEditor() {
        return editor;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
