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
package com.tcdng.unify.core.upl;

import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Singleton;

/**
 * Convenient base class for UPL components.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractUplComponent extends AbstractUnifyComponent implements UplComponent {

    private UplElementAttributes uplAttributes;

    @Override
    public String getParentLongName() throws UnifyException {
        return uplAttributes.getParentLongName();
    }

    @Override
    public String getLongName() throws UnifyException {
        return uplAttributes.getLongName();
    }

    @Override
    public String getShortName() throws UnifyException {
        return uplAttributes.getShortName();
    }

    @Override
    public String getUplId() throws UnifyException {
        return uplAttributes.getId();
    }

    @Override
    public void setUplAttributes(UplElementAttributes uplAttributes) {
        this.uplAttributes = uplAttributes;
    }

    @Override
    public UplElementAttributes getUplElementAttributes() {
        return uplAttributes;
    }

    @Override
    public boolean isUplAttribute(String name) throws UnifyException {
        return uplAttributes.isAttribute(name);
    }

    @Override
    public <T> T getUplAttribute(Class<T> clazz, String attribute) throws UnifyException {
        return uplAttributes.getAttributeValue(clazz, attribute);
    }

    @Override
    public List<String> getShallowReferencedLongNames(String attribute) throws UnifyException {
        return uplAttributes.getShallowReferencedLongNames(attribute);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
