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
package com.tcdng.unify.web.ui;

import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.NumberSymbols;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.data.WebStringWriter;

/**
 * Used for writing response to a user request. Writers are determined at
 * runtime with target platform indicators obtained automatically from current
 * user session context.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ResponseWriter extends UnifyComponent {

    /**
     * Writes component structure and content.
     * 
     * @param component
     *            the user interface component to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeStructureAndContent(Widget component) throws UnifyException;

    /**
     * Writes component structure and content.
     * 
     * @param component
     *            the user interface component to write
     * @param id
     *            the id to use
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeStructureAndContent(Widget component, String id) throws UnifyException;

    /**
     * Writes structure and content of a document using supplied layout.
     * 
     * @param document
     *            the document
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeStructureAndContent(DocumentLayout documentLayout, Document document) throws UnifyException;

    /**
     * Writes container component based on layout.
     * 
     * @param layout
     *            the layout to use
     * @param container
     *            the container to write
     * @throws UnifyException
     */
    ResponseWriter writeStructureAndContent(Layout layout, Container container) throws UnifyException;

    /**
     * Writes panel inner structure and content.
     * 
     * @param panel
     *            the panel to write
     * @throws UnifyException
     *             - If an error occurs
     */
    ResponseWriter writeInnerStructureAndContent(Panel panel) throws UnifyException;

    /**
     * Writes behavior of a document layout.
     * 
     * @param documentLayout
     *            the document layout
     * @param document
     *            the document
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeBehavior(DocumentLayout documentLayout, Document document) throws UnifyException;

    /**
     * Writes component behavior.
     * 
     * @param component
     *            the user interface component to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeBehavior(Widget component) throws UnifyException;

    /**
     * Writes component behavior.
     * 
     * @param component
     *            the user interface component to write
     * @param id
     *            the id to use
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeBehavior(Widget component, String id) throws UnifyException;

    /**
     * Writes behavior for a specified behavior.
     * 
     * @param behavior
     *            the behavior to write
     * @param id
     *            the component id
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeBehavior(Behavior behavior, String id) throws UnifyException;

    /**
     * Writes object and returns this writer.
     */
    ResponseWriter write(Object object);

    /**
     * Writes object if object is not null and returns this writer.
     */
    ResponseWriter writeNotNull(Object object);

    /**
     * Writes HTML fixed space (&nbsp;).
     */
    ResponseWriter writeHtmlFixedSpace();

    /**
     * Writes string with HTML escape and returns this writer.
     */
    ResponseWriter writeWithHtmlEscape(String string);

    /**
     * Writes a JSON quoted string.
     * 
     * @param string
     *            string to write
     * @return this writer
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonQuote(String string) throws UnifyException;

    /**
     * Writes a JSON quoted string.
     * 
     * @param lsw
     *            large string writer source
     * @return this writer
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonQuote(WebStringWriter lsw) throws UnifyException;

    /**
     * Writes a JSON string array.
     * 
     * @param stringArr
     *            the string array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(String... stringArr) throws UnifyException;

    /**
     * Writes a JSON integer array.
     * 
     * @param intArr
     *            the integer array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(Integer... intArr) throws UnifyException;

    /**
     * Writes a JSON long array.
     * 
     * @param longArr
     *            the long array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(Long... longArr) throws UnifyException;

    /**
     * Writes a JSON big decimal array.
     * 
     * @param bigArr
     *            the big decimal array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(BigDecimal... bigArr) throws UnifyException;

    /**
     * Writes a JSON double array.
     * 
     * @param doubleArr
     *            the double array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(Double... doubleArr) throws UnifyException;

    /**
     * Writes a JSON boolean array.
     * 
     * @param boolArr
     *            the boolean array to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(Boolean... boolArr) throws UnifyException;

    /**
     * Writes a JSON array form a collection of objects.
     * 
     * @param col
     *            the collection of objects to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonArray(Collection<?> col) throws UnifyException;

    /**
     * Writes a JSON pattern object.
     * 
     * @param pa
     *            the pattern to write
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonPatternObject(Pattern[] pa) throws UnifyException;

    /**
     * Writes a JSON format date-time format object.
     * 
     * @param dateTimeFormat
     *            the date-time format
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonDateTimeFormatObject(DateTimeFormat[] dateTimeFormat) throws UnifyException;

    /**
     * Writes JSON array of page name aliases in the current request context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonPageNameAliasesArray() throws UnifyException;

    /**
     * Writes a JSON path variable
     * 
     * @param name
     *            the variable name
     * @param path
     *            the sub path
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonPathVariable(String name, String path) throws UnifyException;

    /**
     * Writes a JSON panel object.
     * 
     * @param panel
     *            the panel to write
     * @param innerOnly
     *            indicates if inner contents of panel only
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonPanel(Panel panel, boolean innerOnly) throws UnifyException;

    /**
     * Writes a JSON widget section object.
     * 
     * @param widget
     *            the widget to write
     * @param sectionPageName
     *            the section page name
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeJsonSection(Widget widget, String sectionPageName) throws UnifyException;

    /**
     * Writes a context request URL using supplied path elements
     * 
     * @param path
     *            the main path
     * @param pathElement
     *            the other path elements to use (optional).
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeContextURL(String path, String... pathElement) throws UnifyException;

    /**
     * Writes a resource URL using supplied parameters and application context path.
     * 
     * @param path
     *            the resource controller name (path)
     * @param contentType
     *            the content type. Example: <em>"text/css"</em> for cascaded style
     *            sheet resource and <em>"text/javascript"</em> for a javascript
     *            resource
     * @param resourceName
     *            the name of the target resource
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName) throws UnifyException;

    /**
     * Writes a resource URL using supplied parameters and application context path
     * with flag indicating if resource should be download as attachment.
     * 
     * @param path
     *            the resource controller name (path)
     * @param contentType
     *            the content type. Example: <em>"text/css"</em> for cascaded style
     *            sheet resource and <em>"text/javascript"</em> for a javascript
     *            resource
     * @param resourceName
     *            the name of the target resource
     * @param scope
     *            the scope to read resource from
     * @param attachment
     *            the attachment flag
     * @param clearOnRead
     *            indicates if resource should be cleared from scope after read
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName, String scope,
            boolean attachment, boolean clearOnRead) throws UnifyException;

    /**
     * Writes a URL parameter.
     * 
     * @param name
     *            the parameter name which is converted to a page name
     * @param value
     *            the parameter value which is URL encoded
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeURLParameter(String name, String value) throws UnifyException;

    /**
     * Writes an image file resource URL using supplied image src and application
     * context then appends the URL to supplied string builder.
     * 
     * @param src
     *            the image source
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeFileImageContextURL(String src) throws UnifyException;

    /**
     * Writes a scope image resource URL using supplied image name and application
     * context then appends the URL.
     * 
     * @param imageName
     *            the image name
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeScopeImageContextURL(String imageName) throws UnifyException;

    /**
     * Writes default command URL.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeCommandURL() throws UnifyException;

    /**
     * Writes command URL for specified controller
     * 
     * @param pageControllerName
     *            the page controller name
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeCommandURL(String pageControllerName) throws UnifyException;

    /**
     * Returns true if this response writer is empty.
     */
    boolean isEmpty();

    /**
     * Writes javascript REGEX that allows only alphanumeric characters and,
     * optionally, some special characters.
     * 
     * @param underscore
     *            indicates if regex should permit underscore character
     * @param dollar
     *            indicates if regex should permit dollar character
     * @param period
     *            indicates if regex should permit period character
     * @param dash
     *            indicates if regex should permit dash character
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeNameFormatRegex(boolean underscore, boolean dollar, boolean period, boolean dash)
            throws UnifyException;

    /**
     * Writes javascript REGEX for identifiers.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeIdentifierFormatRegex() throws UnifyException;

    /**
     * Returns javascript REGEX that allows only alphabetic characters.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeWordFormatRegex() throws UnifyException;

    /**
     * Writes a number formatting javascript REGEX.
     * 
     * @param numberSymbols
     *            the reference number symbols
     * @param precision
     *            the precision of the number
     * @param scale
     *            the scale of the number
     * @param acceptNegative
     *            if REGEX should accept negative values
     * @param useGrouping
     *            if REGEX should accept grouping characters
     * @throws UnifyException
     *             if an error occurs
     */
    ResponseWriter writeNumberFormatRegex(NumberSymbols numberSymbols, int precision, int scale, boolean acceptNegative,
            boolean useGrouping) throws UnifyException;

    /**
     * Returns the current buffer.
     */
    WebStringWriter getStringWriter();

    /**
     * Writes entire current buffer to supplied writer.
     * 
     * @param writer
     *            the writer to write to
     * @throws UnifyException
     *             if an error occurs
     */
    void writeTo(Writer writer) throws UnifyException;

    /**
     * Instructs this writer to use a secondary buffer for all write operations.
     */
    void useSecondary();

    /**
     * Instructs this writer to use a secondary buffer for all write operations.
     * 
     * @param initialCapacity
     *            the initial capacity
     */
    void useSecondary(int initialCapacity);

    /**
     * Discards current secondary buffer.
     * 
     * @return the discarded buffer otherwise null
     */
    WebStringWriter discardSecondary();

    /**
     * Resets this response writer for reuse.
     * 
     * @param writers
     *            the writers to use
     */
    void reset(Map<Class<? extends UplComponent>, UplComponentWriter> writers);

    /**
     * Sets the response write table mode.
     * 
     * @param enabled
     *            the value to set
     */
    void setTableMode(boolean enabled);

    /**
     * Tests if writing should use table mode.
     * 
     * @return a true value if table mode is set
     */
    boolean isTableMode();
}
