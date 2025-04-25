/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract user interface document.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplAttributes({
        @UplAttribute(name = "layout", type = DocumentLayout.class, defaultVal = "$d{!ui-desktoptype0}"),
        @UplAttribute(name = "headerPanel", type = String.class),
        @UplAttribute(name = "footerPanel", type = String.class),
        @UplAttribute(name = "menuPanel", type = String.class),
        @UplAttribute(name = "contentPanel", type = String.class),
        @UplAttribute(name = "pushUpdate", type = boolean.class),
        @UplAttribute(name = "pushUpdateBinding", type = String.class)})
public abstract class AbstractDocument extends AbstractHtmlPage implements Document {

    @Override
    public String getLatencyPanelId() throws UnifyException {
        return getPrefixedId("latency_");
    }

    public boolean isPushUpdate() throws UnifyException{
    	final String pushUpdateBinding  = getUplAttribute(String.class, "pushUpdateBinding");
    	if (!StringUtils.isBlank(pushUpdateBinding)) {
    		return getValue(boolean.class, pushUpdateBinding);
    	}

    	return getUplAttribute(boolean.class, "pushUpdate");
    }
    
    @Override
    public Panel getHeaderPanel() throws UnifyException {
        String headerPanelId = getUplAttribute(String.class, "headerPanel");
        if (headerPanelId != null) {
            return getPanelByLongName(headerPanelId);
        }
        return null;
    }

    @Override
    public Panel getMenuPanel() throws UnifyException {
        String menuPanelId = getUplAttribute(String.class, "menuPanel");
        if (menuPanelId != null) {
            return getPanelByLongName(menuPanelId);
        }
        return null;
    }

    @Override
    public ContentPanel getContentPanel() throws UnifyException {
        String contentPanelId = getUplAttribute(String.class, "contentPanel");
        if (contentPanelId != null) {
            return (ContentPanel) getPanelByLongName(contentPanelId);
        }
        return null;
    }

    @Override
    public Panel getFooterPanel() throws UnifyException {
        String footerPanelId = getUplAttribute(String.class, "footerPanel");
        if (footerPanelId != null) {
            return getPanelByLongName(footerPanelId);
        }
        return null;
    }

}
