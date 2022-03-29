/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Component for storing regex patterns.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface RegexPatternStore extends UnifyComponent {

    /**
     * Returns a compiled pattern for specified locale and regex message key.
     * 
     * @param locale
     *            the locale
     * @param regexMessageKey
     *            the regex message key
     * @return
     * @throws UnifyException
     */
    Pattern getPattern(Locale locale, String regexMessageKey) throws UnifyException;
}
