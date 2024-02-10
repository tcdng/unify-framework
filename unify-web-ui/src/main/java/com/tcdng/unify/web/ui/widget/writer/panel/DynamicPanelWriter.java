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
package com.tcdng.unify.web.ui.widget.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.DynamicPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Dynamic panel writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DynamicPanel.class)
@Component("dynamicpanel-writer")
public class DynamicPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DynamicPanel dynamicPanel = (DynamicPanel) widget;
        writer.write("<input type=\"hidden\"");
        writeTagId(writer, dynamicPanel.getHiddenId());
        writer.write("/>");
        writeLayoutContent(writer, dynamicPanel);
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        DynamicPanel dynamicPanel = (DynamicPanel) container;
        getRequestContextUtil().setDynamicPanelPageName(dynamicPanel.getId(), dynamicPanel.getContainer().getId());
        try {
            writer.writeInnerStructureAndContent(dynamicPanel.getStandalonePanel());
        } finally {
            getRequestContextUtil().clearDynamicPanelPageName();
        }
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		DynamicPanel dynamicPanel = (DynamicPanel) widget;
		getRequestContextUtil().setDynamicPanelPageName(dynamicPanel.getId(), dynamicPanel.getContainer().getId());
		try {
			writer.writeBehavior(dynamicPanel.getStandalonePanel(), handlers);
		} finally {
			getRequestContextUtil().clearDynamicPanelPageName();
		}
	}
}
