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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Used for generating UPL sources.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UplGenerator extends UnifyComponent {

    /**
     * Returns the UPL component name that this generator generates UPL for
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getUplComponentName() throws UnifyException;

    /**
     * Generates a UPL source for specified target.
     * 
     * @param target
     *            the name of the target to generate a UPL source for
     * @return the generated UPL
     * @throws UnifyException
     *             if an error occurs
     */
    String generateUplSource(String target) throws UnifyException;

    /**
     * Returns true if target source has a newer version.
     * 
     * @param target
     *            the name of the target
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isNewerVersion(String target) throws UnifyException;
}
