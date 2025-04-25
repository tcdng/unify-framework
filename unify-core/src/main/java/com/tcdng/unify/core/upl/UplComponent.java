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
package com.tcdng.unify.core.upl;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A component that can be declared with a UPL descriptor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface UplComponent extends UnifyComponent {

    /**
     * Returns the UPL component parent long name.
     */
    String getParentLongName() throws UnifyException;

    /**
     * Returns the UPL component long name.
     */
    String getLongName() throws UnifyException;

    /**
     * Returns the UPL component short name.
     */
    String getShortName() throws UnifyException;

    /**
     * Returns the original ID or system generated ID
     */
    String getUplId() throws UnifyException;

    /**
     * Returns referenced long names for specified attribute.
     * 
     * @param attribute
     *            the attribute name
     */
    List<String> getShallowReferencedLongNames(String attribute) throws UnifyException;

    /**
     * Sets the component UPL attributes.
     * 
     * @param uplAttributes
     *            the UPL attributes to set
     */
    void setUplAttributes(UplElementAttributes uplAttributes);

    /**
     * Returns this component's UPL attributes.
     */
    UplElementAttributes getUplElementAttributes();

    /**
     * Tests if supplied name is a UPL attribute.
     * 
     * @param name
     *            the name to test
     * @return
     */
    boolean isUplAttribute(String name) throws UnifyException;

    /**
     * Returns the UPL attribute for a component.
     * 
     * @param clazz
     *            the attribute type
     * @param attribute
     *            the attribute name
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T getUplAttribute(Class<T> clazz, String attribute) throws UnifyException;
}
