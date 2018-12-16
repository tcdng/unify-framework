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

import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * Interface for accessing attribute values for a UPL document.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UplDocumentAttributes extends UplElementAttributes {

    /**
     * Returns the document child element long names.
     */
    Set<String> getLongNames();

    /**
     * Returns the document child short element names
     */
    Set<String> getShortNames();

    /**
     * Gets an element by long name.
     * 
     * @param longName
     *            the specified long name
     * @return the element attributes
     * @throws UnifyException
     *             if an error occurs
     */
    UplElementAttributes getChildElementByLongName(String longName) throws UnifyException;

    /**
     * Tests if there is an element with the specified long name.
     * 
     * @param longName
     *            the long name
     * @return a true value if element eith long name exists, otherwise false/.
     */
    boolean isElementWithLongName(String longName);
}
