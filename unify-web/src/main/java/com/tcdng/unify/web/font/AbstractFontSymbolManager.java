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

package com.tcdng.unify.web.font;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Convenient abstract base class for font symbol managers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractFontSymbolManager extends AbstractUnifyComponent implements FontSymbolManager {

    private Map<String, String> nameToHexMap;

    public AbstractFontSymbolManager() {
        nameToHexMap = new HashMap<String, String>();

        // Register currencies
        registerSymbol("naira-sign", "&#x20A6;");
        registerSymbol("dollar-sign", "&#x24;");
        registerSymbol("cent-sign", "&#xa2;");
        registerSymbol("pound-sign", "&#xa3;");
        registerSymbol("euro-sign", "&#x20AC;");
        registerSymbol("yen-sign", "&#xa5;");
        registerSymbol("rupee-sign", "&#x20A8;");
        registerSymbol("ruble-sign", "&#x20BD;");
        registerSymbol("peso-sign", "&#x20B1;");
        registerSymbol("baht-sign", "&#x0E3F;");
        
        // Other symbols
        registerSymbol("copyright-sign", "&#xa9;");
        registerSymbol("registered-sign", "&#xae;");
        registerSymbol("trademark-sign", "&#x2122;");
        registerSymbol("celsius-sign", "&#x2103;");
        registerSymbol("fahrenheit-sign", "&#x2109;");
        registerSymbol("baht-sign", "&#x0E3F;");
        
    }

    @Override
    public String resolveSymbolHtmlHexCode(String symbolName) throws UnifyException {
        symbolName = symbolName.toLowerCase();
        String hex = nameToHexMap.get(symbolName);
        if (hex != null) {
            return hex;
        }

        if (isValidHexCode(symbolName)) {
            return symbolName;
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
        if (isValidHexCode(hex)) {
            nameToHexMap.put(symbolName.toLowerCase(), hex);
            return true;
        }

        return false;
    }

    private boolean isValidHexCode(String hex) {
        return hex != null && hex.matches("&#x[0-9a-fA-F]+;");
    }
}
