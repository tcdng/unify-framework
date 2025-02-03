/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.WebStringWriter;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.core.util.html.HtmlTextWriter;
import com.tcdng.unify.core.util.json.JsonWriter;

/**
 * Used for writing response to a user request. Writers are determined at
 * runtime with target platform indicators obtained automatically from current
 * user session context.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ResponseWriter extends HtmlTextWriter {

    /**
     * Writes a message using application locale.
     * 
     * @param message
     *                the message to resolve
     * @param params
     *                the message parameters
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeResolvedApplicationMessage(String message, Object... params) throws UnifyException;

    /**
     * Writes a message using current session locale.
     * 
     * @param message
     *                the message to resolve
     * @param params
     *                the message parameters
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeResolvedSessionMessage(String message, Object... params) throws UnifyException;

    /**
     * Writes component structure and content.
     * 
     * @param component
     *                  the user interface component to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeStructureAndContent(Widget component) throws UnifyException;

    /**
     * Writes component structure and content.
     * 
     * @param component
     *                  the user interface component to write
     * @param id
     *                  the id to use
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeStructureAndContent(Widget component, String id) throws UnifyException;

    /**
     * Writes structure and content of a document using supplied layout.
     * 
     * @param document
     *                 the document
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeStructureAndContent(DocumentLayout documentLayout, Document document) throws UnifyException;

    /**
     * Writes container component based on layout.
     * 
     * @param layout
     *                  the layout to use
     * @param container
     *                  the container to write
     * @throws UnifyException
     */
    ResponseWriter writeStructureAndContent(Layout layout, Container container) throws UnifyException;

    /**
     * Writes panel inner structure and content.
     * 
     * @param panel
     *              the panel to write
     * @throws UnifyException
     *                        - If an error occurs
     */
    ResponseWriter writeInnerStructureAndContent(Panel panel) throws UnifyException;

    /**
     * Writes behavior of a document layout.
     * 
     * @param documentLayout
     *                       the document layout
     * @param document
     *                       the document
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeBehavior(DocumentLayout documentLayout, Document document) throws UnifyException;

    /**
     * Writes widget behavior.
     * 
     * @param widget
     *                  the user interface widget to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeBehavior(Widget widget) throws UnifyException;

	/**
	 * Writes widget behavior with event handlers.
	 * 
	 * @param widget        the user interface widget to write
	 * @param eventHandlers the event handlers
	 * @throws UnifyException if an error occurs
	 */
	ResponseWriter writeBehavior(Widget widget, EventHandler[] eventHandlers) throws UnifyException;

	/**
	 * Writes widget behavior with event handlers.
	 * 
	 * @param widget        the user interface widget to write
	 * @param eventHandlers the event handlers
	 * @param events        the events to write for
	 * @throws UnifyException if an error occurs
	 */
	ResponseWriter writeBehavior(Widget widget, EventHandler[] eventHandlers, Collection<String> events)
			throws UnifyException;

    /**
     * Writes widget behavior.
     * 
     * @param widget
     *                  the user interface widget to write
     * @param id
     *                  the id to use
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeBehavior(Widget widget, String id) throws UnifyException;

    /**
     * Writes behavior for a specified behavior.
     * 
     * @param behavior
     *                 the behavior to write
     * @param id
     *                 the component id
     * @param cmdTag
     *                 optional command tag
     * @param preferredEvent
     *                 preferred event
     * @throws UnifyException
     *                        if an error occurs
     */
	ResponseWriter writeBehavior(Behavior behavior, String id, String cmdTag, String preferredEvent)
			throws UnifyException;

    /**
     * Writes object and returns this writer.
     */
    ResponseWriter write(Object object);

    /**
     * Writes character and returns this writer.
     */
    ResponseWriter write(char ch);

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
     *               string to write
     * @return this writer
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonQuote(String string) throws UnifyException;

    /**
     * Writes a JSON quoted string.
     * 
     * @param lsw
     *            large string writer source
     * @return this writer
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonQuote(WebStringWriter lsw) throws UnifyException;

    /**
     * Writes a JSON string array.
     * 
     * @param stringArr
     *                  the string array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(String... stringArr) throws UnifyException;

    /**
     * Writes a JSON integer array.
     * 
     * @param intArr
     *               the integer array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(Integer... intArr) throws UnifyException;

    /**
     * Writes a JSON long array.
     * 
     * @param longArr
     *                the long array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(Long... longArr) throws UnifyException;

    /**
     * Writes a JSON big decimal array.
     * 
     * @param bigArr
     *               the big decimal array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(BigDecimal... bigArr) throws UnifyException;

    /**
     * Writes a JSON double array.
     * 
     * @param doubleArr
     *                  the double array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(Double... doubleArr) throws UnifyException;

    /**
     * Writes a JSON boolean array.
     * 
     * @param boolArr
     *                the boolean array to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(Boolean... boolArr) throws UnifyException;

    /**
     * Writes a JSON array form a collection of objects.
     * 
     * @param col
     *            the collection of objects to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonArray(Collection<?> col) throws UnifyException;

    /**
     * Writes a JSON pattern object.
     * 
     * @param paramName
     *                  the parameter name
     * @param pa
     *                  the pattern to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeParam(String paramName, Pattern[] pa) throws UnifyException;

    /**
     * Writes a JSON pattern object.
     * 
     * @param pa
     *           the pattern to write
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonPatternObject(Pattern[] pa) throws UnifyException;

    /**
     * Writes a JSON format date-time format object.
     * 
     * @param paramName
     *                       the parameter name
     * @param dateTimeFormat
     *                       the date-time format
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeParam(String paramName, DateTimeFormat[] dateTimeFormat) throws UnifyException;

    /**
     * Writes a JSON format date-time format object.
     * 
     * @param dateTimeFormat
     *                       the date-time format
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonDateTimeFormatObject(DateTimeFormat[] dateTimeFormat) throws UnifyException;

    /**
     * Writes JSON array of page name aliases in the current request context.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonPageNameAliasesArray() throws UnifyException;

    /**
     * Writes a JSON path variable
     * 
     * @param name
     *             the variable name
     * @param path
     *             the sub path
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonPathVariable(String name, String path) throws UnifyException;

    /**
     * Writes a JSON panel object.
     * 
     * @param panel
     *                  the panel to write
     * @param innerOnly
     *                  indicates if inner contents of panel only
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonPanel(Panel panel, boolean innerOnly) throws UnifyException;

    /**
     * Writes a JSON widget section object.
     * 
     * @param widget
     *                        the widget to write
     * @param sectionPageName
     *                        the section page name
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeJsonSection(Widget widget, String sectionPageName) throws UnifyException;

    /**
     * Writes a context request URL using supplied path elements
     * 
     * @param path
     *                    the main path
     * @param pathElement
     *                    the other path elements to use (optional).
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeContextURL(String path, String... pathElement) throws UnifyException;

    /**
     * Writes a context request URL using supplied path elements to supplied buffer.
     * 
     * @param sb
     *                    the buffer to write to
     * @param path
     *                    the main path
     * @param pathElement
     *                    the other path elements to use (optional).
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeContextURL(StringBuilder sb, String path, String... pathElement) throws UnifyException;

    /**
     * Writes a resource URL using supplied parameters and application context path.
     * 
     * @param path
     *                     the resource controller name (path)
     * @param contentType
     *                     the content type. Example: <em>"text/css"</em> for
     *                     cascaded style sheet resource and
     *                     <em>"text/javascript"</em> for a javascript resource
     * @param resourceName
     *                     the name of the target resource
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName) throws UnifyException;

    /**
     * Writes a resource URL using supplied parameters and application context path
     * with flag indicating if resource should be download as attachment.
     * 
     * @param path
     *                     the resource controller name (path)
     * @param contentType
     *                     the content type. Example: <em>"text/css"</em> for
     *                     cascaded style sheet resource and
     *                     <em>"text/javascript"</em> for a javascript resource
     * @param resourceName
     *                     the name of the target resource
     * @param scope
     *                     the scope to read resource from
     * @param attachment
     *                     the attachment flag
     * @param clearOnRead
     *                     indicates if resource should be cleared from scope after
     *                     read
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName, String scope,
            boolean attachment, boolean clearOnRead) throws UnifyException;

    /**
     * Writes a URL parameter.
     * 
     * @param name
     *              the parameter name which is converted to a page name
     * @param value
     *              the parameter value which is URL encoded
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeURLParameter(String name, String value) throws UnifyException;

    /**
     * Writes an image file resource URL using supplied image src and application
     * context then appends the URL to supplied string builder.
     * 
     * @param src
     *            the image source
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeFileImageContextURL(String src) throws UnifyException;

    /**
     * Writes a scope image resource URL using supplied image name and application
     * context then appends the URL.
     * 
     * @param imageName
     *                  the image name
     * @param clearOnRead clear on read
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeScopeImageContextURL(String imageName, boolean clearOnRead) throws UnifyException;

	/**
	 * Writes a streamer resource URL using supplied streamer component
	 * 
	 * @param mimeType     the MIME type
	 * @param streamer     the streamer
	 * @param resourceName the resource name
	 * @throws UnifyException if an error occurs
	 */
	ResponseWriter writeStreamerContextURL(MimeType mimeType, String streamer, String resourceName)
			throws UnifyException;

    /**
     * Writes default command URL.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeCommandURL() throws UnifyException;

    /**
     * Writes command URL for specified controller
     * 
     * @param pageControllerName
     *                           the page controller name
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeCommandURL(String pageControllerName) throws UnifyException;

    /**
     * Returns true if this response writer is empty.
     */
    boolean isEmpty();

    /**
     * Writes beginning of a function call.
     * 
     * @param functionName
     *                     the function name
     * @return this writer
     * @throws UnifyException
     *                        if function is already begun. If an error occurs
     */
    ResponseWriter beginFunction(String functionName) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, String[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, String val) throws UnifyException;

    /**
     * Write a resolved function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeResolvedParam(String paramName, String val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, Number[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, Number val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, Boolean[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, Boolean val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, char[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, char val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, int[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, int val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, long[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, long val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, short[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, short val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, float[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, float val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, double[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, double val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, boolean[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, boolean val) throws UnifyException;

    /**
     * Write a object parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeObjectParam(String paramName, Object val) throws UnifyException;

    /**
     * Write a object array parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeObjectParam(String paramName, Object[] val) throws UnifyException;

    /**
     * Write a function parameter.
     * 
     * @param paramName
     *                  the parameter name
     * @param val
     *                  the parameter value
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter writeParam(String paramName, JsonWriter val) throws UnifyException;

    /**
     * Writes a context request URL parameter using supplied path elements
     * 
     * @param paramName
     *                    the parameter name
     * @param path
     *                    the main path
     * @param pathElement
     *                    the other path elements to use (optional).
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeContextURLParam(String paramName, String path, String... pathElement) throws UnifyException;

    /**
     * Writes default parameter command URL.
     * 
     * @param paramName
     *                  the parameter name
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeCommandURLParam(String paramName) throws UnifyException;

    /**
     * Writes command URL parameter for specified controller
     * 
     * @param paramName
     *                           the parameter name
     * @param pageControllerName
     *                           the page controller name
     * @throws UnifyException
     *                        if an error occurs
     */
    ResponseWriter writeCommandURLParam(String paramName, String pageControllerName) throws UnifyException;

    /**
     * Write closure of a function call
     * 
     * @return this writer
     * @throws UnifyException
     *                        if write function is not begun. If an error occurs
     */
    ResponseWriter endFunction() throws UnifyException;

    /**
     * Returns the current buffer.
     */
    WebStringWriter getStringWriter();

    /**
     * Writes entire current buffer to supplied writer.
     * 
     * @param writer
     *               the writer to write to
     * @throws UnifyException
     *                        if an error occurs
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

	/**
	 * Sets dynamic confirmation message.
	 * 
	 * @param confirm the message to set
	 */
	void setConfirm(String confirm);

	/**
	 * Gets dynamic confirmation.
	 * 
	 * @return the confirmation.
	 */
	String getConfirm();

	/**
	 * Clears dynamic confirmation message.
	 */
	void clearConfirm();

	/**
	 * Checks if writer is with dynamic confirmation message
	 * 
	 * @return true if exists otherwise false
	 */
	boolean isWithConfirm();
    
    /**
     * Resets this response writer for reuse.
     * 
     * @param writers
     *                the writers to use
     */
    void reset(Map<Class<? extends UplComponent>, UplComponentWriter> writers);

    /**
     * Sets the response write table mode.
     * 
     * @param enabled
     *                the value to set
     */
    void setTableMode(boolean enabled);

    /**
     * Tests if writing should use table mode.
     * 
     * @return a true value if table mode is set
     */
    boolean isTableMode();

	/**
	 * Gets the current writer context data index.
	 * 
	 * @return the context data index;
	 */
	int getDataIndex();

	/**
	 * Sets the current context data index
	 * 
	 * @param dataIndex the data index to set
	 */
	void setDataIndex(int dataIndex);
	
	/**
	 * Checks if data index is valid
	 * 
	 * @return true if valid otherwise false
	 */
	boolean isWithDataIndex();
	
	/**
	 * Indicates post command references should be kept.
	 * 
	 * @return true if set otherwise false
	 */
	boolean isKeepPostCommandRefs();

	/**
	 * Enables keeping of post command references for this writer
	 * 
	 * @return true if no command references is currently kept otherwise false
	 */
	boolean setKeepPostCommandRefs();

	/**
	 * Disables keeping of post command references for this writer
	 */
	void clearKeepPostCommandRefs();

	/**
	 * Keeps post command references.
	 * 
	 * @param widgetIds the references to keep.
	 */
	void keepPostCommandRefs(Collection<String> widgetIds);

	/**
	 * Gets the post command references kept so far by this writer
	 * 
	 * @return the references
	 */
	Set<String> getPostCommandRefs();
	

	/**
	 * Checks if writer is in plain resource mode.
	 * @return true if plain otherwise false
	 */
	boolean isPlainResourceMode();

	/**
	 * Sets writer to plain resource mode
	 */
	void setPlainResourceMode();

	/**
	 * Clears writer from plain resource mode
	 */
	void clearPlainResourceMode();

}
