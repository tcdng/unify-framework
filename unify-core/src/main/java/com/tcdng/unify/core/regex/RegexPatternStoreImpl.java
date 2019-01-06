/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core.regex;

import java.util.Locale;
import java.util.regex.Pattern;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.LocaleFactoryMaps;

/**
 * Default implementation of a regex pattern store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_REGEXPATTERNSTORE)
public class RegexPatternStoreImpl extends AbstractUnifyComponent implements RegexPatternStore {

    private LocaleFactoryMaps<String, Pattern> patterns;

    public RegexPatternStoreImpl() {
        patterns = new LocaleFactoryMaps<String, Pattern>() {

            @Override
            protected Pattern createObject(Locale locale, String regexMessageKey, Object... params) throws Exception {
                String regex = getMessage(locale, regexMessageKey);
                return Pattern.compile(regex);
            }
        };
    }

    @Override
    public Pattern getPattern(Locale locale, String regexMessageKey) throws UnifyException {
        return patterns.get(locale, regexMessageKey);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
