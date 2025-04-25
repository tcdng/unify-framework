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

package com.tcdng.unify.web.ui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Abstract value list container.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractValueListContainer<T, U> extends AbstractContainer {

    private List<U> oldItemList;

    private List<T> valueList;

    public AbstractValueListContainer() {
        this(false);
    }

    public AbstractValueListContainer(boolean useLayoutIfPresent) {
        super(useLayoutIfPresent);
        valueList = Collections.emptyList();
    }

    public List<T> getValueList() throws UnifyException {
        List<U> itemList = getItemList();
        if (oldItemList != itemList || (itemList != null && itemList.size() != valueList.size())) {
            if (DataUtils.isNotBlank(itemList)) {
                int size = itemList.size();
                valueList = new ArrayList<T>(itemList.size());
                for (int i = 0; i < size; i++) {
                    valueList.add(newValue(itemList.get(i), i));
                }
            } else {
                valueList = Collections.emptyList();
            }

            oldItemList = itemList;
            onCreateValueList(valueList);
        }
        return valueList;
    }

    protected void invalidateValueList() {
        oldItemList = null;
        valueList = null;
    }

    protected abstract List<U> getItemList() throws UnifyException;

    protected abstract T newValue(U item, int index) throws UnifyException;

    protected abstract void onCreateValueList(List<T> valueList) throws UnifyException;

}
