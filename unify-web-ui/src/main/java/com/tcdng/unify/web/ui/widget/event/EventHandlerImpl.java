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
package com.tcdng.unify.web.ui.widget.event;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.upl.UplElementAttributes;
import com.tcdng.unify.web.ui.widget.AbstractEventHandler;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PageAction;

/**
 * Event handler implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-event")
public class EventHandlerImpl extends AbstractEventHandler {

	@Override
	public EventHandler wrap() {
		return new WrappedEventHandlerImpl(this);
	}

	private static class WrappedEventHandlerImpl extends AbstractEventHandler {

		private final EventHandler srcHandler;

	    private PageAction[] pageAction;

		public WrappedEventHandlerImpl(EventHandler srcHandler) {
			this.srcHandler = srcHandler;
			this.pageAction = srcHandler.getPageAction();
		}

		@Override
		public String getEvent() throws UnifyException {
			return srcHandler.getEvent();
		}

		@Override
		public String getParentLongName() throws UnifyException {
			return srcHandler.getParentLongName();
		}

		@Override
		public String getLongName() throws UnifyException {
			return srcHandler.getLongName();
		}

		@Override
		public String getShortName() throws UnifyException {
			return srcHandler.getShortName();
		}

		@Override
		public String getUplId() throws UnifyException {
			return srcHandler.getUplId();
		}

		@Override
		public UplElementAttributes getUplElementAttributes() {
			return srcHandler.getUplElementAttributes();
		}

		@Override
		public boolean isUplAttribute(String name) throws UnifyException {
			return srcHandler.isUplAttribute(name);
		}

		@Override
		public <T> T getUplAttribute(Class<T> clazz, String attribute) throws UnifyException {
			return srcHandler.getUplAttribute(clazz, attribute);
		}

		@Override
		public List<String> getShallowReferencedLongNames(String attribute) throws UnifyException {
			return srcHandler.getShallowReferencedLongNames(attribute);
		}

		@Override
	    public void setPageAction(PageAction[] pageAction) {
	        this.pageAction = pageAction;
	    }

		@Override
	    public PageAction[] getPageAction() {
	        return pageAction;
	    }

		@Override
		public EventHandler wrap() {
			return new WrappedEventHandlerImpl(srcHandler);
		}
		
	}
}
