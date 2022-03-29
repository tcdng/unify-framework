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

package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Dialog CRUD object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DialogCrudInfo<T> {

    public enum Mode {
        CREATE,
        MAINTAIN
    }
    
    private Class<? extends T> typeClass;

    private List<T> itemList;

    private T item;
    
    private Mode mode;
    
    private String title;
    
    public DialogCrudInfo(Class<? extends T> typeClass) {
        this.typeClass = typeClass;
    }
    
    public Mode getMode() {
        return mode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void init() throws UnifyException {
        if (mode == null) {
            newItem();
        }
    }

    public void newItem() throws UnifyException {
        mode = Mode.CREATE;
        item = ReflectUtils.newInstance(typeClass);
    }

    public void saveItem() throws UnifyException {
        if (itemList == null) {
            itemList = new ArrayList<T>();
        }

        if (item != null) {
            itemList.add(item);
        }
    }

    public void removeItem() throws UnifyException {
        if (item != null && itemList != null) {
            itemList.remove(item);
        }
    }

    public void selectItem(int index) {
        if (index >= 0 && itemList != null && index < itemList.size()) {
            mode = Mode.MAINTAIN;
            item = itemList.get(index);
        }
    }

    public T getItem() {
        return item;
    }

    public List<T> getItemList() {
        return itemList;
    }

    public void setItemList(List<T> itemList) {
        mode = null;
        item = null;
        this.itemList = itemList;
    }
}
