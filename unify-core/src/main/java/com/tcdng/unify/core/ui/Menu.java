/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.ui;

import java.util.Collections;
import java.util.List;

/**
 * Menu data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Menu {

    private String privilege;

    private String caption;

    private String colorScheme;

    private List<MenuItemSet> menuItemSetList;

    public Menu(String privilege, String caption, String colorScheme, List<MenuItemSet> menuItemSetList) {
        this.privilege = privilege;
        this.caption = caption;
        this.colorScheme = colorScheme;
        this.menuItemSetList = Collections.unmodifiableList(menuItemSetList);
    }

    public String getPrivilege() {
        return privilege;
    }

    public String getCaption() {
        return caption;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public List<MenuItemSet> getMenuItemSetList() {
        return menuItemSetList;
    }
}
