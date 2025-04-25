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

package com.tcdng.unify.web.util;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.UnifyWebErrorConstants;

/**
 * Real path utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class RealPathUtils {

    private static final String[] RESTRICTED_FOLDERS = {"CONF", "\\CONF", "/CONF"};

    private RealPathUtils() {
        
    }
    
    public static void checkAccessibleRealPath(String resourceName) throws UnifyException {
        String uResourceName = resourceName.toUpperCase();
        for(String restrictedFolder: RESTRICTED_FOLDERS) {
            if (uResourceName.startsWith(restrictedFolder)) {
                throw new UnifyException(UnifyWebErrorConstants.RESOURCE_ACCESS_DENIED);
            }
        }            
    }
}
