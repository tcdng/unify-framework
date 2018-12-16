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
package com.tcdng.unify.core.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Convenient abstract enumeration constants XML adapter class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractEnumConstXmlAdapter<T extends EnumConst> extends XmlAdapter<String, T> {

    private Class<T> clazz;

    public AbstractEnumConstXmlAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String marshal(T type) throws Exception {
        return type.name();
    }

    @Override
    public T unmarshal(String typeStr) throws Exception {
        return EnumUtils.fromName(clazz, typeStr);
    }
}
