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
package com.tcdng.unify.core.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu set data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class MenuSet {

    private List<Menu> menuList;

    private boolean alwaysSelect;

    public MenuSet() {
        this.menuList = new ArrayList<Menu>();
    }

    public void add(Menu menu) {
        this.menuList.add(menu);
    }

    public Menu getMenu(int index) {
        return this.menuList.get(index);
    }

    public boolean isAlwaysSelect() {
        return alwaysSelect;
    }

    public void setAlwaysSelect(boolean alwaysSelect) {
        this.alwaysSelect = alwaysSelect;
    }

    public boolean isShowSelect() {
        return alwaysSelect || this.menuList.size() > 1;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public int size() {
        return this.menuList.size();
    }

    public boolean isEmpty() {
        return this.menuList.isEmpty();
    }
}
