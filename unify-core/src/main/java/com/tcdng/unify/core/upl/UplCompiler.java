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
package com.tcdng.unify.core.upl;

import java.util.Locale;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Used for compiling UPL sources.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UplCompiler extends UnifyComponent {

    /**
     * Compiles a simple descriptor. Simple descriptors have no foreign or guarded
     * component references.
     * 
     * @param descriptor
     *            the descriptor
     * @param locale
     *            the locale to compile for
     * @return UplElementAttributes the compiled element
     * @throws UnifyException
     *             if an error occurs
     */
    UplElementAttributes compileDescriptor(Locale locale, String descriptor) throws UnifyException;

    /**
     * Compiles all UPL documents associated with component specified by name.
     * 
     * @param locale
     *            the locale to compile for
     * @param componentName
     *            the component name
     * @return UplDocument the compiled document
     * @throws UnifyException
     *             if an error occurs
     */
    UplDocumentAttributes compileComponentDocuments(Locale locale, String componentName) throws UnifyException;

    /**
     * In validates a stale document
     * 
     * @param name
     *            the document name
     * @return true if document was stale and invalidated.
     * @throws UnifyException
     *             if an error occurs
     */
    boolean invalidateStaleDocument(String name) throws UnifyException;

    /**
     * Retrieves a UPL element attributes object using supplied attributes key.
     * 
     * @param locale
     *            the locale
     * @param attributesKey
     *            the attributes to use
     * @return the element attributes object
     * @throws UnifyException
     *             if an error occurs
     */
    UplElementAttributes getUplElementAttributes(Locale locale, String attributesKey) throws UnifyException;

    /**
     * Returns component UPL attributes information.
     * 
     * @param componentName
     *            the component name
     * @return the UPL attribute information
     * @throws UnifyException
     *             if an error occurs
     */
    UplAttributesInfo getUplAttributesInfo(String componentName) throws UnifyException;

    /**
     * Resets compiler
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void reset() throws UnifyException;
}
