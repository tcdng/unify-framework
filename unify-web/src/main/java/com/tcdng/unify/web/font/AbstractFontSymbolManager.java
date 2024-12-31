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

package com.tcdng.unify.web.font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient abstract base class for font symbol managers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractFontSymbolManager extends AbstractUnifyComponent implements FontSymbolManager {

    private Map<String, String> nameToHexMap;

    private FactoryMap<String, String> unicodeCode;

    private FactoryMap<String, String> htmlHexCode;
    
    public AbstractFontSymbolManager() {
        nameToHexMap = new HashMap<String, String>();

        // Register currencies
        registerSymbol("naira-sign", "20A6");
        registerSymbol("dollar-sign", "0024");
        registerSymbol("cent-sign", "00a2");
        registerSymbol("pound-sign", "00a3");
        registerSymbol("euro-sign", "20AC");
        registerSymbol("yen-sign", "00a5");
        registerSymbol("rupee-sign", "20A8");
        registerSymbol("ruble-sign", "20BD");
        registerSymbol("peso-sign", "20B1");
        registerSymbol("baht-sign", "0E3F");
        
        // Other symbols
        registerSymbol("copyright-sign", "00a9");
        registerSymbol("registered-sign", "00ae");
        registerSymbol("trademark-sign", "2122");
        registerSymbol("celsius-sign", "2103");
        registerSymbol("fahrenheit-sign", "2109");
        registerSymbol("baht-sign", "0E3F");
        
        unicodeCode = new FactoryMap<String, String>() {
            @Override
            protected String create(String key, Object... params) throws Exception {
                return "\\u" + nameToHexMap.get(key);
            }  
        };

        htmlHexCode = new FactoryMap<String, String>() {
            @Override
            protected String create(String key, Object... params) throws Exception {
                return "&#x" + nameToHexMap.get(key) + ";";
            }  
        };
    }

    @Override
	public List<String> getSymbolNames() throws UnifyException {
    	List<String> names = new ArrayList<String>();
    	names.addAll(nameToHexMap.keySet());
    	Collections.sort(names);
		return names;
	}

	@Override
    public String resolveSymbolUnicode(String symbolName) throws UnifyException {
        String hex = nameToHexMap.get(symbolName);
        if (hex != null) {
            return unicodeCode.get(symbolName);
        }

        return null;
    }

    @Override
    public String resolveSymbolHtmlHexCode(String symbolName) throws UnifyException {
        String hex = nameToHexMap.get(symbolName);
        if (hex != null) {
            return htmlHexCode.get(symbolName);
        }

        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected boolean registerSymbol(String symbolName, String hex) {
        if (hex != null && hex.matches("[0-9a-fA-F]+")) {
            nameToHexMap.put(symbolName, StringUtils.padLeft(hex, '0', 4));
            return true;
        }

        return false;
    }
}
