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
package com.tcdng.unify.core.upl;

import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * Interface for accessing attribute values for a UPL element.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface UplElementAttributes {

    /**
     * Returns the parent element long name.
     */
    String getParentLongName();

    /**
     * Returns the element long name.
     */
    String getLongName();

    /**
     * Returns the element short name.
     */
    String getShortName();

    /**
     * Returns the component qualified name.
     */
    String getQualifiedName();

    /**
     * Returns the component name.
     */
    String getComponentName();

    /**
     * Returns the UPL element attributes key.
     */
    String getKey();

    /**
     * Returns the original Id for element.
     */
    String getId();

    /**
     * Returns the UPL type for this attibute set.
     */
    int getUplType();

    /**
     * Returns a true value if supplied string is an attribute
     * 
     * @param name
     *            the attribute name
     */
    boolean isAttribute(String name);

    /**
     * Returns the attribute names.
     */
    Set<String> getAttributeNames();

    /**
     * Gets a UPL attribute value with specified name.
     * 
     * @param clazz
     *            the value type
     * @param name
     *            the attribute name
     * @return the attribute value if supplied otherwise null
     * @throws UnifyException
     *             if attribute with supplied name is unknown. if an error occurs
     */
    <T> T getAttributeValue(Class<T> clazz, String name) throws UnifyException;

    /**
     * Returns a list of referenced long names for supplied attribute.
     * 
     * @param attribute
     *            the attribute name
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getShallowReferencedLongNames(String attribute) throws UnifyException;

    /**
     * Returns shallow referenced long names.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Set<String> getShallowReferencedLongNames() throws UnifyException;

    /**
     * Returns deep referenced long names.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Set<String> getDeepReferencedLongNames() throws UnifyException;

    /**
     * Returns child elements.
     */
    Set<UplElementAttributes> getChildElements();
}
