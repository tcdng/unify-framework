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
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.AbstractAutoRefreshMultiControl;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Abstract base class for auto-referesh multi-control writers.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractAutoRefreshMultiControlWriter extends AbstractControlWriter {

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		AbstractAutoRefreshMultiControl control = (AbstractAutoRefreshMultiControl) widget;
		String[] refs = DataUtils.toArray(String.class, writer.getPostCommandRefs());
		if (handlers != null || refs != null) {
			control.saveForRefresh(handlers, refs);
		}

		super.doWriteBehavior(writer, widget, control.getHandlers());
	}
}
