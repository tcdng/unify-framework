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
package com.tcdng.unify.core.util;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.SessionAttributeValueConstants;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.system.ClusterCommandConstants;

/**
 * System utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class SystemUtils {

    private static final Map<String, String> forcelogoutValueMap = new HashMap<String, String>();

    private static final Map<String, String> forceLogoutErrorMap = new HashMap<String, String>();

    static {
        forcelogoutValueMap.put(ClusterCommandConstants.FORCE_LOGOUT_ADMINISTRATOR,
                SessionAttributeValueConstants.FORCE_LOGOUT_ADMINISTRATOR);
        forcelogoutValueMap.put(ClusterCommandConstants.FORCE_LOGOUT_NO_MULTIPLE_LOGIN,
                SessionAttributeValueConstants.FORCE_LOGOUT_NO_MULTIPLE_LOGIN);
        forcelogoutValueMap.put(ClusterCommandConstants.FORCE_LOGOUT_SYSTEM,
                SessionAttributeValueConstants.FORCE_LOGOUT_SYSTEM);

        forceLogoutErrorMap.put(SessionAttributeValueConstants.FORCE_LOGOUT_ADMINISTRATOR,
                UnifyCoreErrorConstants.FORCELOGOUT_ADMINISTRATOR);
        forceLogoutErrorMap.put(SessionAttributeValueConstants.FORCE_LOGOUT_NO_MULTIPLE_LOGIN,
                UnifyCoreErrorConstants.FORCELOGOUT_MULTIPLE_LOGIN);
        forceLogoutErrorMap.put(SessionAttributeValueConstants.FORCE_LOGOUT_SYSTEM,
                UnifyCoreErrorConstants.FORCELOGOUT_SYSTEM);
    }

    private SystemUtils() {

    }

    public static boolean isForceLogoutClusterCommand(String cmd) {
        return forcelogoutValueMap.containsKey(cmd);
    }

    public static boolean isForceLogoutErrorCode(String errorCode) {
        return forceLogoutErrorMap.containsValue(errorCode);
    }

    public static String getSessionAttributeValue(String cmd) {
        return forcelogoutValueMap.get(cmd);
    }

    public static String getSessionAttributeErrorCode(String value) {
        return forceLogoutErrorMap.get(value);
    }
}
