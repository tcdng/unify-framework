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
package com.tcdng.unify.web.ui;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.ui.panel.AbstractStandalonePanel;
import com.tcdng.unify.web.ui.panel.StandalonePanel;

/**
 * Serves as the base class for a page component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "type", type = String.class, defaultValue = "ui-page"),
        @UplAttribute(name = "caption", type = String.class, mandatory = true),
        @UplAttribute(name = "remote", type = boolean.class, defaultValue = "false") })
public abstract class AbstractPage extends AbstractStandalonePanel implements Page {

    private Map<String, StandalonePanel> standalonePanels;

    private Map<String, Object> attributes;

    private String sessionId;

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public StandalonePanel getStandalonePanel(String name) throws UnifyException {
        if (standalonePanels != null) {
            StandalonePanel standalonePanel = standalonePanels.get(name);
            if (standalonePanel != null) {
                if (!standalonePanel.isSourceInvalidated()) {
                    return standalonePanel;
                }

                standalonePanels.remove(name);
            }
        }

        return null;
    }

    @Override
    public void addStandalonePanel(String name, StandalonePanel standalonePanel) throws UnifyException {
        if (standalonePanels == null) {
            standalonePanels = new HashMap<String, StandalonePanel>();
        }

        standalonePanels.put(name, standalonePanel);
    }

    @Override
    public Panel getPanelByLongName(String longName) throws UnifyException {
        if (isWidget(longName)) {
            return (Panel) getWidgetByLongName(longName);
        }

        throw new UnifyException(UnifyWebErrorConstants.PAGE_PANEL_WITH_ID_NOT_FOUND, longName);
    }

    @Override
    public Panel getPanelByShortName(String shortName) throws UnifyException {
        try {
            return (Panel) getWidgetByShortName(shortName);
        } catch (Exception e) {
            throw new UnifyException(UnifyWebErrorConstants.PAGE_PANEL_WITH_ID_NOT_FOUND, shortName);
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        if (attributes != null) {
            return attributes.get(name);
        }

        return null;
    }

    @Override
    public Object clearAttribute(String name) {
        if (attributes != null) {
            return attributes.remove(name);
        }

        return null;
    }

    @Override
    public boolean isDocument() {
        return false;
    }

    @Override
    public Widget getWidgetByLongName(String longName) throws UnifyException {
        if (isWidget(longName)) {
            return super.getWidgetByLongName(longName);
        }

        if (standalonePanels != null) {
            for (StandalonePanel standalonePanel : standalonePanels.values()) {
                if (standalonePanel.isWidget(longName)) {
                    return standalonePanel.getWidgetByLongName(longName);
                }
            }
        }

        throw new UnifyException(UnifyWebErrorConstants.WIDGET_WITH_LONGNAME_UNKNOWN, longName, getLongName());
    }

    @Override
    public String getPopupBaseId() throws UnifyException {
        return getPrefixedId("popb_");
    }

    @Override
    public String getPopupWinId() throws UnifyException {
        return getPrefixedId("popw_");
    }

    @Override
    public String getPopupSysId() throws UnifyException {
        return getPrefixedId("pops_");
    }
}
