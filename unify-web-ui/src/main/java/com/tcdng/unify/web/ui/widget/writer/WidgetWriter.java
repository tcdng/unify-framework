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
package com.tcdng.unify.web.ui.widget.writer;

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * A widget writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface WidgetWriter extends UplComponentWriter {

    /**
     * Writes widget structure and content to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the user interface widget to write
     * @throws UnifyException
     *             if an error occurs
     */
    void writeStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException;

    /**
     * Writes widget structure and content to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the widget to write
     * @param id
     *            the id to use
     * @throws UnifyException
     *             if an error occurs
     */
    void writeStructureAndContent(ResponseWriter writer, Widget widget, String id) throws UnifyException;

    /**
     * Writes widget section structure and content to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the user interface widget to write
     * @param sectionId
     *            the section page name
     * @throws UnifyException
     *             if an error occurs
     */
    void writeSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionId) throws UnifyException;

    /**
     * Writes widget behavior to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the user interface widget to write
     * @throws UnifyException
     *             if an error occurs
     */
    void writeBehavior(ResponseWriter writer, Widget widget) throws UnifyException;

	/**
	 * Writes widget behavior to response writer with event handlers.
	 * 
	 * @param writer        the response writer
	 * @param widget        the user interface widget to write
	 * @param eventHandlers event handlers
	 * @throws UnifyException if an error occurs
	 */
	void writeBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers) throws UnifyException;

	/**
	 * Writes widget behavior to response writer with event handlers.
	 * 
	 * @param writer        the response writer
	 * @param widget        the user interface widget to write
	 * @param eventHandlers event handlers
	 * @param events         the events to trigger
	 * @throws UnifyException if an error occurs
	 */
	void writeBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers, Collection<String> events)
			throws UnifyException;

    /**
     * Writes widget behavior to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the user interface widget to write
     * @param id
     *            the id to use
     * @throws UnifyException
     *             if an error occurs
     */
    void writeBehavior(ResponseWriter writer, Widget widget, String id) throws UnifyException;

    /**
     * Writes widget section behavior to response writer.
     * 
     * @param writer
     *            the response writer
     * @param widget
     *            the user interface widget to write
     * @param sectionId
     *            the section page name
     * @throws UnifyException
     *             if an error occurs
     */
    void writeSectionBehavior(ResponseWriter writer, Widget widget, String sectionId) throws UnifyException;
}
