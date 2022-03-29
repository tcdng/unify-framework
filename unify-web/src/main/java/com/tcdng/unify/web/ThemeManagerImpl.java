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

package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TokenUtils;

/**
 * Default theme manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_THEMEMANAGER)
public class ThemeManagerImpl extends AbstractUnifyComponent implements ThemeManager {

    private static final String DEFAULT_THEME_PATH = "/web/themes/farko";

    @Override
    public String expandThemeTag(String resouceName) throws UnifyException {
        if (TokenUtils.isThemeTag(resouceName)) {
            resouceName = TokenUtils.extractTokenValue(resouceName);
            String themePath = null;
            // Check for user theme first
            if (getSessionContext().isUserLoggedIn()) {
                themePath = getSessionContext().getUserToken().getThemePath();
            }

            // Else use container setting
            if (StringUtils.isBlank(themePath)) {
                themePath = getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_THEME,
                        DEFAULT_THEME_PATH);
            }

            if (themePath.endsWith("/")) {
                if (resouceName.startsWith("/")) {
                    return themePath + resouceName.substring("/".length());
                }

                return themePath + resouceName;
            }

            if (resouceName.startsWith("/")) {
                return themePath + resouceName;
            }

            return themePath + "/" + resouceName;
        }

        return resouceName;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
