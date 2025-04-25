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
 * @since 4.1
 */
@UplAttributes({
		@UplAttribute(name = "styleSheet", type = String[].class),
	    @UplAttribute(name = "script", type = String[].class),
	    @UplAttribute(name = "font", type = String[].class),
		@UplAttribute(name = "excludeStyleSheet", type = String[].class),
	    @UplAttribute(name = "excludeScript", type = String[].class),
        @UplAttribute(name = "fontFamily", type = String.class, defaultVal = "arial, Open Sans"),
        @UplAttribute(name = "favicon", type = String.class, defaultVal = "web/images/favicon.png") })
public abstract class AbstractHtmlPage extends AbstractPage {

    @Override
    public boolean isDocument() {
        return true;
    }
	
    public String getFavicon() throws UnifyException {
		return getUplAttribute(String.class, "favicon");
	}
	
    public String getFontFamily() throws UnifyException {
		return getUplAttribute(String.class, "fontFamily");
	}
    
    public String[] getStyleSheet() throws UnifyException {
    	return getUplAttribute(String[].class, "styleSheet");
    }
    
    public String[] getScript() throws UnifyException {
    	return getUplAttribute(String[].class, "script");
    }
    
    public String[] getFont() throws UnifyException {
    	return getUplAttribute(String[].class, "font");
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
