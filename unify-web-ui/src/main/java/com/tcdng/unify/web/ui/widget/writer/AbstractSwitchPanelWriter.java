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
package com.tcdng.unify.web.ui.widget.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.SwitchPanel;

/**
 * Abstract base class for switch panel writers.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractSwitchPanelWriter extends AbstractPanelWriter {

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		SwitchPanel switchPanel = (SwitchPanel) widget;
		Widget currentComponent = switchPanel.getCurrentWidget();
		if (currentComponent != null && currentComponent.isVisible()) {
			writer.writeBehavior(currentComponent, handlers);
		}
	}

	@Override
	protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
		SwitchPanel switchPanel = (SwitchPanel) container;
		Widget currentComponent = switchPanel.getCurrentWidget();
		if (currentComponent != null && currentComponent.isVisible()) {
			writer.writeStructureAndContent(currentComponent);
		}
	}

}
