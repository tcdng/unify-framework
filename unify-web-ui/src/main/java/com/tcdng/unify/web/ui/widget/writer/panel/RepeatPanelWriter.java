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

package com.tcdng.unify.web.ui.widget.writer.panel;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.RepeatPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Repeat panel writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(RepeatPanel.class)
@Component("repeatpanel-writer")
public class RepeatPanelWriter extends AbstractPanelWriter {

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		RepeatPanel repeatPanel = (RepeatPanel) widget;
		Widget innerWidget = repeatPanel.getWidgetByLongName(repeatPanel.getLayoutWidgetLongNames().get(0));
		List<ValueStore> valueStoreList = repeatPanel.getRepeatValueStores();
		if (DataUtils.isNotBlank(valueStoreList)) {
			for (ValueStore valueStore : valueStoreList) {
				if (innerWidget.isVisible()) {
					innerWidget.setValueStore(valueStore);
					writer.writeBehavior(innerWidget);
				}
			}
		}
	}

}
