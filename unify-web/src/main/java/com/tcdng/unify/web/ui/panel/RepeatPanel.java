/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.web.ui.panel;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.Layout;

/**
 * Repeat panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-repeatpanel")
@UplAttributes({ @UplAttribute(name = "layout", type = Layout.class, mandatory = true),
        @UplAttribute(name = "components", type = UplElementReferences.class, mandatory = true) })
public class RepeatPanel extends AbstractPanel {

    private List<ValueStore> valueStoreList;

    private Object oldValue;

    @Override
    public void cascadeValueStore() throws UnifyException {
        Object value = getValue();
        if (oldValue != value) {
            if (value != null) {
                valueStoreList = new ArrayList<ValueStore>();
                int i = 0;
                for (Object valueObject : (List<?>) value) {// Support lists only!
                    valueStoreList.add(createValueStore(valueObject, i++));
                }
            } else {
                valueStoreList = null;
            }

            oldValue = value;
        }
    }

    @Override
    public List<ValueStore> getRepeatValueStores() throws UnifyException {
        return valueStoreList;
    }

    @Override
    public boolean isRepeater() {
        return true;
    }

}
