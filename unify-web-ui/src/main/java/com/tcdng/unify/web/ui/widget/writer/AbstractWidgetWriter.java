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

import java.util.Collection;
import java.util.List;

import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.util.ColorUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.util.WebUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Abstract base class for widget writers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractWidgetWriter extends AbstractDhtmlWriter implements WidgetWriter {

    @Configurable
    private FontSymbolManager fontSymbolManager;

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        widget.updateInternalState();
        doWriteStructureContentContainer(writer, widget);
        widget.addPageAliases();
    }

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Widget widget, String id) throws UnifyException {
        String origId = widget.getId();
        try {
            widget.setId(id);
            widget.updateInternalState();
            doWriteStructureContentContainer(writer, widget);
            widget.addPageAliases();
        } finally {
            widget.setId(origId);
        }
    }

    @Override
    public void writeSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionId)
            throws UnifyException {
        doWriteSectionStructureAndContent(writer, widget, sectionId);
    }

    @Override
    public final void writeBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
     	doWriteBehavior(writer, widget, widget.getEventHandlers());
    }

    @Override
	public final void writeBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers)
			throws UnifyException {
        doWriteBehavior(writer, widget, eventHandlers);
	}

	@Override
	public void writeBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers,
			Collection<String> events) throws UnifyException {
		doWriteBehavior(writer, widget, eventHandlers, events);
	}

	@Override
    public void writeBehavior(ResponseWriter writer, Widget widget, String id) throws UnifyException {
        String origId = widget.getId();
        try {
            widget.setId(id);
        	doWriteBehavior(writer, widget, widget.getEventHandlers());
        } finally {
            widget.setId(origId);
        }
    }

    @Override
    public void writeSectionBehavior(ResponseWriter writer, Widget widget, String sectionId) throws UnifyException {
        doWriteSectionBehavior(writer, widget, sectionId);
    }

    protected abstract void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException;

    protected final boolean isWithFontSymbolManager() {
        return fontSymbolManager != null;
    }
    
    protected List<String> getFontResources() throws UnifyException {
        return fontSymbolManager.getFontResources();
    }

    protected String resolveSymbolUnicode(String symbolName) throws UnifyException {
        return fontSymbolManager.resolveSymbolUnicode(symbolName);
    }

    protected String resolveSymbolHtmlHexCode(String symbolName) throws UnifyException {
        return fontSymbolManager.resolveSymbolHtmlHexCode(symbolName);
    }

    protected void doWriteSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionId)
            throws UnifyException {

    }
    
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers)
			throws UnifyException {
		doWriteBehavior(writer, widget, eventHandlers, null);
	}
	
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] eventHandlers,
			Collection<String> events) throws UnifyException {
		if (eventHandlers != null && !widget.isContainerDisabled()) {
			final int indexed = widget.getIndexedHandlerCount();
			String id = widget.getId();
			if (widget.isBindEventsToFacade()) {
				id = widget.getFacadeId();
			}

			getRequestContext().setQuickReference(widget.getValueStore());

			if (indexed > 0) {
				final int initial = writer.getDataIndex();
				try {
					for (int i = 0; i < indexed; i++) {
						writer.setDataIndex(i);
						final String _id = id + i;
						for (EventHandler eventHandler : eventHandlers) {
							if (events == null || events.contains(eventHandler.getEvent())) {
								final String eventBinding = eventHandler.getEventBinding();
								final String preferredEvent = !StringUtils.isBlank(eventBinding)
										? widget.getValue(String.class, eventBinding)
										: null;
								writer.writeBehavior(eventHandler, _id, widget.getBinding(), preferredEvent);
							}
						}
					}
				} finally  {
					writer.setDataIndex(initial);
				}
			} else {
				for (EventHandler eventHandler : eventHandlers) {
					if (events == null || events.contains(eventHandler.getEvent())) {
						final String eventBinding = eventHandler.getEventBinding();
						final String preferredEvent = !StringUtils.isBlank(eventBinding)
								? widget.getValue(String.class, eventBinding)
								: null;
						writer.writeBehavior(eventHandler, id, widget.getBinding(), preferredEvent);
					}
				}
			}
		}
	}

    protected void doWriteSectionBehavior(ResponseWriter writer, Widget widget, String sectionId)
            throws UnifyException {

    }

    protected PageManager getPageManager() throws UnifyException {
        return (PageManager) getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
    }

    protected String getCommandURL() throws UnifyException {
        return getContextURL(getRequestContextUtil().getResponsePathParts().getControllerPathId(), "/command");
    }

	protected String getContextURL(String path, String... pathElement) throws UnifyException {
		RequestContext requestContext = getRequestContext();
		return WebUtils.getContextURL(requestContext, getRequestContextUtil().isRemoteViewer(), path, pathElement);
	}

    protected String getUserColorStyleClass(String classBase) throws UnifyException {
        UserToken userToken = getUserToken();
        if (userToken != null && StringUtils.isNotBlank(userToken.getColorScheme())) {
            return classBase + userToken.getColorScheme();
        } else {
            String configColorScheme = ColorUtils.getConformingColorSchemeCode(
                    getContainerSetting(String.class, UnifyCorePropertyConstants.APPLICATION_COLORSCHEME));
            if (!StringUtils.isBlank(configColorScheme)) {
                return classBase + configColorScheme;
            }
        }

        return classBase;
    }

	private void doWriteStructureContentContainer(ResponseWriter writer, Widget widget) throws UnifyException {
		if (widget.isRefreshesContainer()) {
			writer.write("<div class=\"ui-wcont\" id=\"wcont_").write(widget.getId()).write("\">");
		}

		doWriteStructureAndContent(writer, widget);

		if (widget.isRefreshesContainer()) {
			writer.write("</div>");
		}
	}

}
