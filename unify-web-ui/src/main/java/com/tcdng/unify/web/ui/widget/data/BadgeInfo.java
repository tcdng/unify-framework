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

package com.tcdng.unify.web.ui.widget.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tcdng.unify.core.constant.ColorScheme;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Badge information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class BadgeInfo {

    private Map<String, BadgeItem> items;

    private Map<String, String> nexts;

    private String preferredField;
    
    private BadgeInfo(Map<String, BadgeItem> items, String preferredField) {
        this.items = items;
        this.preferredField = preferredField;
    }

    public BadgeItem getItem(String code) {
    	if (!items.containsKey(code)) {
    		throw new IllegalArgumentException("Unknown badge item with code [" + code + "].");
    	}
    	
        return items.get(code);
    }
    
    public String getPreferredField() {
		return preferredField;
	}

	public boolean isWithgPrefferedField() {
		return !StringUtils.isBlank(preferredField);
	}
    
	public String nextCode(String code) {
		if (nexts == null) {
			synchronized(this) {
				if (nexts == null) {
					nexts = new HashMap<String, String>();
					String first = null;
					String last = null;
					for(String _code: items.keySet()) {
						if (first == null) {
							first = _code;
						} else {
							nexts.put(last, _code);
						}
						
						last = _code;
					}
					
					nexts.put(last, first);
					nexts = Collections.unmodifiableMap(nexts);
				}
			}
		}
		
		String _next = nexts.get(code);
		if (StringUtils.isBlank(_next)) {
    		throw new IllegalArgumentException("Unknown badge item with code [" + code + "].");
		}
		
		return _next;
	}
	
	public static Builder newBuilder() {
    	return new Builder();
    }
    
    public static class Builder {

        private Map<String, BadgeItem> items;

        private String preferredField;
    	
        public Builder() {
        	this.items = new LinkedHashMap<String, BadgeItem>();
        }
        
        public Builder preferred(String preferredField) {
        	this.preferredField = preferredField;
        	return this;
        }
        
        public Builder addItem(String code, String caption, ColorScheme colorScheme) {
        	if (items.containsKey(code)) {
        		throw new IllegalArgumentException("Item with code [" + code + "] is already defined.");
        	}
        	
        	this.items.put(code, new BadgeItem(colorScheme, caption));
        	return this;
        }
        
        public BadgeInfo build() {
        	if (items.isEmpty()) {
        		throw new IllegalArgumentException("No badge item defined.");
        	}
        	
        	return new BadgeInfo(items, preferredField);
        }
    }
}
