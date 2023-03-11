/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.message;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Resource Bundles.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ResourceBundles {

    private FactoryMap<Locale, Map<String, String>> resourceBundles;

    private List<String> messagesBase;

    public ResourceBundles(List<String> messagesBaseIn) {
        messagesBase = messagesBaseIn;
        resourceBundles = new FactoryMap<Locale, Map<String, String>>() {
            @Override
            protected Map<String, String> create(Locale locale, Object... params) throws Exception {
                Map<String, String> map = new HashMap<String, String>();
                if (messagesBase != null) {
                    for (String baseName : messagesBase) {
                        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
                        for (String key : bundle.keySet()) {
                            map.put(key, bundle.getString(key));
                        }
                    }
                    ResourceBundle.clearCache();
                }
                return map;
            }
        };
    }

    public String getMessage(Locale locale, String messageKey) throws UnifyException {
        String message = resourceBundles.get(locale).get(messageKey);
        if (message == null) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_MISSING_RESOURCE, messageKey);
        }
        return message;
    }

    public String getMessage(Locale locale, String messageKey, Object... params) throws UnifyException {
        if (params == null || params.length == 0) {
            return getMessage(locale, messageKey);
        }

        MessageFormat mf = new MessageFormat("");
        mf.setLocale(locale);
        mf.applyPattern(getMessage(locale, messageKey));
        return mf.format(params);
    }
}
