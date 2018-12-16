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
package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.ui.Behavior;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Behavior writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface BehaviorWriter extends UplComponentWriter {

    /**
     * Writes behavior for a specified component.
     * 
     * @param writer
     *            the response writer
     * @param behavior
     *            the behavior to write
     * @param id
     *            the component id
     * @throws UnifyException
     *             if an error occurs
     */
    void writeBehavior(ResponseWriter writer, Behavior behavior, String id) throws UnifyException;
}
