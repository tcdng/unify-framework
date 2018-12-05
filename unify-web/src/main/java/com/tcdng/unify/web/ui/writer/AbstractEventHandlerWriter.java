/*
 * Copyright 2014 The Code Department
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
import com.tcdng.unify.web.ui.AbstractBehaviorWriter;
import com.tcdng.unify.web.ui.Behavior;
import com.tcdng.unify.web.ui.EventHandler;
import com.tcdng.unify.web.ui.PageAction;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.util.WriterUtils;

/**
 * Abstract base class for event handler writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractEventHandlerWriter extends AbstractBehaviorWriter implements EventHandlerWriter {

	@Override
	public void writeBehavior(ResponseWriter writer, Behavior behavior, String id) throws UnifyException {
		EventHandler eventHandler = (EventHandler) behavior;
		String event = eventHandler.getUplAttribute(String.class, "event");
		if (!"none".equals(event)) {
			if (eventHandler.getPageAction() != null) {
				event = WriterUtils.getEventJS(event.toLowerCase());
				for (PageAction pageAction : eventHandler.getPageAction()) {
					String function = WriterUtils.getActionJSFunction(pageAction.getAction().toLowerCase());
					String eventParams = this.writeActionParamsJS(writer, event, function, id, pageAction, null, null,
							null);
					writer.write("ux.setOnEvent(").write(eventParams).write(");");
				}
			}
		}
	}
}
