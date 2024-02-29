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
package com.tcdng.unify.web.ui.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Abstract user interface document.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({
		@UplAttribute(name = "styleSheet", type = String[].class),
	    @UplAttribute(name = "script", type = String[].class),
		@UplAttribute(name = "excludeStyleSheet", type = String[].class),
	    @UplAttribute(name = "excludeScript", type = String[].class),
        @UplAttribute(name = "layout", type = DocumentLayout.class, defaultVal = "$d{!ui-desktoptype0}"),
        @UplAttribute(name = "headerPanel", type = String.class),
        @UplAttribute(name = "footerPanel", type = String.class),
        @UplAttribute(name = "menuPanel", type = String.class),
        @UplAttribute(name = "contentPanel", type = String.class),
        @UplAttribute(name = "caption", type = String.class),
        @UplAttribute(name = "fontFamily", type = String.class, defaultVal = "arial, Open Sans"),
        @UplAttribute(name = "favicon", type = String.class, defaultVal = "web/images/favicon.png") })
public abstract class AbstractDocument extends AbstractPage implements Document {

    @Override
    public String getLatencyPanelId() throws UnifyException {
        return getPrefixedId("latency_");
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

    @Override
    public boolean isDocument() {
        return true;
    }
    
    public String[] getStyleSheet() throws UnifyException {
    	return getUplAttribute(String[].class, "styleSheet");
    }
    
    public String[] getScript() throws UnifyException {
    	return getUplAttribute(String[].class, "script");
    }
    
    public Set<String> getExcludeStyleSheet() throws UnifyException {
    	String[] _sheets = getUplAttribute(String[].class, "excludeStyleSheet");
    	if (_sheets != null && _sheets.length > 0) {
    		return new HashSet<String>(Arrays.asList(_sheets));
    	}
    	
    	return Collections.emptySet();
    }
    
    public Set<String> getExcludeScript() throws UnifyException {
    	String[] _scripts = getUplAttribute(String[].class, "excludeScript");
    	if (_scripts != null && _scripts.length > 0) {
    		return new HashSet<String>(Arrays.asList(_scripts));
    	}
    	
    	return Collections.emptySet();
    }
}
