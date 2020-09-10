/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.util.ColorUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.ui.EventHandler;
import com.tcdng.unify.web.ui.PageManager;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract base class for widget writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractWidgetWriter extends AbstractDhtmlWriter implements WidgetWriter {

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        widget.updateInternalState();
        doWriteStructureAndContent(writer, widget);
        widget.addPageAliases();
    }

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Widget widget, String id) throws UnifyException {
        String origId = widget.getId();
        try {
            widget.setId(id);
            widget.updateInternalState();
            doWriteStructureAndContent(writer, widget);
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
    public void writeBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        doWriteBehavior(writer, widget);
    }

    @Override
    public void writeBehavior(ResponseWriter writer, Widget widget, String id) throws UnifyException {
        String origId = widget.getId();
        try {
            widget.setId(id);
            doWriteBehavior(writer, widget);
        } finally {
            widget.setId(origId);
        }
    }

    @Override
    public void writeSectionBehavior(ResponseWriter writer, Widget widget, String sectionId) throws UnifyException {
        doWriteSectionBehavior(writer, widget, sectionId);
    }

    protected abstract void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException;

    protected void doWriteSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionId)
            throws UnifyException {

    }

    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        EventHandler[] eventHandlers = widget.getUplAttribute(EventHandler[].class, "eventHandler");
        if (eventHandlers != null) {
            String id = widget.getId();
            if (widget.isBindEventsToFacade()) {
                id = widget.getFacadeId();
            }

            getRequestContext().setQuickReference(widget.getValueStore());
            for (EventHandler eventHandler : eventHandlers) {
                writer.writeBehavior(eventHandler, id);
            }
        }
    }

    protected void doWriteSectionBehavior(ResponseWriter writer, Widget widget, String sectionId)
            throws UnifyException {

    }

    protected PageManager getPageManager() throws UnifyException {
        return (PageManager) getComponent(WebApplicationComponents.APPLICATION_PAGEMANAGER);
    }

    protected String getCommandURL() throws UnifyException {
        return getContextURL(getRequestContextUtil().getResponsePathParts().getControllerPathId(), "/command");
    }

    protected String getContextURL(String path, String... pathElement) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        RequestContext requestContext = getRequestContext();
        if (getRequestContextUtil().isRemoteViewer()) {
            sb.append(getSessionContext().getUriBase());
        }

        sb.append(requestContext.getContextPath());
        if (requestContext.isWithTenantPath()) {
            sb.append(requestContext.getTenantPath());
        }
        
        sb.append(requestContext.getRequestPath());
        sb.append(path);
        for (String element : pathElement) {
            sb.append(element);
        }
        return sb.toString();
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
}
