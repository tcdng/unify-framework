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

package com.tcdng.unify.web;

/**
 * Page path information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PagePathInfo {

    private String id;

    private String colorScheme;

    private String openPagePath;

    private String savePagePath;

    private String closePagePath;

    private boolean remoteSave;

    public PagePathInfo(String id, String colorScheme, String openPagePath, String savePagePath,
            String closePagePath, boolean remoteSave) {
        this.id = id;
        this.colorScheme = colorScheme;
        this.openPagePath = openPagePath;
        this.savePagePath = savePagePath;
        this.closePagePath = closePagePath;
        this.remoteSave = remoteSave;
    }

    public String getId() {
        return id;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public String getOpenPagePath() {
        return openPagePath;
    }

    public String getSavePagePath() {
        return savePagePath;
    }

    public String getClosePagePath() {
        return closePagePath;
    }

    public boolean isRemoteSave() {
        return remoteSave;
    }

}
