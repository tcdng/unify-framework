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
package com.tcdng.unify.web.ui.widget.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.Layout;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * User interface layout writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface LayoutWriter extends UplComponentWriter {

    /**
     * Writes structure and content of components using supplied layout.
     * 
     * @param writer
     *            the response writer
     * @param layout
     *            the layout component.
     * @param container
     *            the container
     * @throws UnifyException
     *             if an error occurs
     */
    void writeStructureAndContent(ResponseWriter writer, Layout layout, Container container) throws UnifyException;
}
