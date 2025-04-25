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

package com.tcdng.unify.web.font;

import java.util.List;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Font symbol manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface FontSymbolManager extends UnifyComponent {

    /**
     * Gets the font resource files for this symbol manager
     * 
     * @return list of relative file paths
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getFontResources() throws UnifyException;

	/**
	 * Gets the synbol names for this font symbol manager
	 * 
	 * @return the symbol names
	 * @throws UnifyException if an error occurs
	 */
	List<String> getSymbolNames() throws UnifyException;
    
    /**
     * Resolves uni-code code from supplied symbol code.
     * 
     * @param symbolName
     *            the symbol name (case insensitive)
     * @return the supplied symbol name if valid uni-code resolved from
     *         symbol name, otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String resolveSymbolUnicode(String symbolName) throws UnifyException;

    /**
     * Resolves HTML hexadecimal code from supplied symbol code.
     * 
     * @param symbolName
     *            the symbol name (case insensitive)
     * @return the supplied symbol name if valid hex code or hex code resolved from
     *         symbol name, otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String resolveSymbolHtmlHexCode(String symbolName) throws UnifyException;
}
