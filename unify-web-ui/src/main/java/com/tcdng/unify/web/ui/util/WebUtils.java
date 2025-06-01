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
package com.tcdng.unify.web.ui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.constant.ShortcutFlagConstants;
import com.tcdng.unify.web.ui.UnifyWebUIErrorConstants;
import com.tcdng.unify.web.ui.constant.PageRequestParameterConstants;

/**
 * Web utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class WebUtils {

	private static final Set<String> skipOnPopulateSet = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(PageRequestParameterConstants.DOCUMENT,
					PageRequestParameterConstants.TARGET_VALUE, PageRequestParameterConstants.WINDOW_NAME,
					PageRequestParameterConstants.VALIDATION_ACTION, PageRequestParameterConstants.CONFIRM_MSG,
					PageRequestParameterConstants.CONFIRM_MSGICON, PageRequestParameterConstants.CONFIRM_PARAM,
					PageRequestParameterConstants.NO_TRANSFER, RequestParameterConstants.CLIENT_ID,
					RequestParameterConstants.REMOTE_VIEWER, RequestParameterConstants.REMOTE_ROLECD,
					RequestParameterConstants.REMOTE_SESSION_ID, RequestParameterConstants.REMOTE_USERLOGINID,
					RequestParameterConstants.REMOTE_USERNAME, RequestParameterConstants.REMOTE_BRANCH_CODE,
					RequestParameterConstants.REMOTE_ZONE_CODE, RequestParameterConstants.REMOTE_GLOBAL_ACCESS,
					RequestParameterConstants.REMOTE_COLOR_SCHEME, RequestParameterConstants.REMOTE_TENANT_CODE,
					RequestParameterConstants.EXTERNAL_FORWARD)));

    private WebUtils() {

    }

    public static Set<String> getReservedRequestAttributes() {
    	return skipOnPopulateSet;
    }

	public static String getContextURL(RequestContext requestContext, boolean remoteViewer, String path,
			String... pathElement) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		if (remoteViewer) {
			sb.append(requestContext.getSessionContext().getUriBase());
		}

		sb.append(requestContext.getContextPath());
		if (requestContext.isWithTenantPath()) {
			sb.append(requestContext.getTenantPath());
		}

		sb.append(requestContext.getRequestPath());
		sb.append(path);
		for (String element : pathElement) {
			sb.append(element);
		}
		return sb.toString();
	}
    
	public static String addParameterToPath(String path, String paramName, String paramVal) {
		if (path != null && !StringUtils.isBlank(paramName)) {
			return path + (path.indexOf('?') >= 0 ? "&" : "?") + paramName + "=" + (paramVal != null ? paramVal : "");
		}

		return path;
	}
	
    /**
     * Encodes a shortcut string.
     * 
     * @param shortcut
     *            the shortcut string to encode
     * @return the encodede shortcut
     * @throws UnifyException
     *             if an error occurs
     */
    public static String encodeShortcut(String shortcut) throws UnifyException {
        String encodedShortcut = null;
        if (StringUtils.isNotBlank(shortcut)) {
            int encoded = 0;
            String[] elements = shortcut.toUpperCase().split("\\+");
            boolean validShortcut = false;
            for (int i = 0; i < elements.length; i++) {
                if ("SHIFT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.SHIFT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.SHIFT;
                } else if ("CTRL".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.CTRL) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.CTRL;
                } else if ("ALT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.ALT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.ALT;
                } else {
                    if (!(validShortcut = ((encoded & 0x00FF) == 0) && (elements[i].length() == 1))) {
                        break;
                    }
                    encoded += elements[i].charAt(0);
                }
            }
            if (!validShortcut) {
                throw new UnifyException(UnifyWebUIErrorConstants.KEYCOMBO_IS_INVALID, shortcut);
            }
            encodedShortcut = String.valueOf(encoded);
        }
        return encodedShortcut;
    }
}
