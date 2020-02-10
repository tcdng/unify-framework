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
package com.tcdng.unify.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * URL utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class UrlUtils {

    private UrlUtils() {

    }

    public static String encodeURLParameter(String parameter) throws UnifyException {
        try {
            return URLEncoder.encode(parameter, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.UTIL_ERROR);
        }
    }

}
