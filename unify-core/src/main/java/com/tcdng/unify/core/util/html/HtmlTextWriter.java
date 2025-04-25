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
package com.tcdng.unify.core.util.html;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.data.WebStringWriter;

/**
 * HTML text writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface HtmlTextWriter extends UnifyComponent {

    /**
     * Writes object and returns this writer.
     */
    HtmlTextWriter write(Object object);

    /**
     * Writes character and returns this writer.
     */
    HtmlTextWriter write(char ch);

    /**
     * Writes object if object is not null and returns this writer.
     */
    HtmlTextWriter writeNotNull(Object object);

    /**
     * Writes HTML fixed space (&nbsp;).
     */
    HtmlTextWriter writeHtmlFixedSpace();

    /**
     * Writes string with HTML escape and returns this writer.
     */
    HtmlTextWriter writeWithHtmlEscape(String string);

    /**
     * Instructs this writer to use a secondary buffer for all write operations.
     */
    void useSecondary();

    /**
     * Instructs this writer to use a secondary buffer for all write operations.
     * 
     * @param initialCapacity
     *                        the initial capacity
     */
    void useSecondary(int initialCapacity);

    /**
     * Discards current secondary buffer.
     * 
     * @return the discarded buffer otherwise null
     */
    WebStringWriter discardSecondary();

    /**
     * Discards current secondary buffer with merge.
     * 
     * @return the discarded buffer otherwise null
     */
    WebStringWriter discardMergeSecondary();

}
