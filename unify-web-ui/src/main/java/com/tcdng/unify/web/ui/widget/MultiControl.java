/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl.ChildWidgetInfo;

/**
 * User interface control with multiple child widgets.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface MultiControl extends Control, WidgetContainer {
    /**
     * Adds a child widget to this component.
     * 
     * @param widget
     *            the child widget to add
     * @throws UnifyException
     *             if and error occurs
     */
    void addChildWidget(Widget widget) throws UnifyException;

    /**
     * Returns child widget information.
     * 
     * @param childId
     *            the child widget ID
     */
    ChildWidgetInfo getChildWidgetInfo(String childId);

    /**
     * Returns all child widget information.
     */
    Collection<ChildWidgetInfo> getChildWidgetInfos();

    /**
     * Returns child widget count.
     */
    int getChildWidgetCount();
}
