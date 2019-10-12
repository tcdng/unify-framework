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
package com.tcdng.unify.web.ui.control;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;

/**
 * An input control whose field type varies at runtime.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-dynamic")
@UplAttributes({ @UplAttribute(name = "descriptorBinding", type = String.class, mandatory = true) })
public class DynamicField extends AbstractMultiControl {

    private Map<String, String> descriptorToIdMap;

    public DynamicField() {
        descriptorToIdMap = new HashMap<String, String>();
    }

    @Override
    public String getStyleClass() throws UnifyException {
        return getControl().getStyleClass();
    }

    @Override
    public String getStyle() throws UnifyException {
        return getControl().getStyle();
    }

    @Override
    public String getHint() throws UnifyException {
        return getControl().getHint();
    }

    @Override
    public void setGroupId(String groupId) throws UnifyException {
        super.setGroupId(groupId);
        for (String id : descriptorToIdMap.values()) {
            getChildControlInfo(id).getControl().setGroupId(groupId);
        }
    }

    public Control getControl() throws UnifyException {
        Control control = null;
        String descriptorBinding = getUplAttribute(String.class, "descriptorBinding");
        String descriptor = (String) getValue(descriptorBinding);
        if (StringUtils.isNotBlank(descriptor)) {
            String id = descriptorToIdMap.get(descriptor);
            if (id == null) {
                StringBuilder sb = new StringBuilder(descriptor);
                appendUplAttribute(sb, "binding");
                control = addInternalChildControl(sb.toString(), true, false);
                descriptorToIdMap.put(descriptor, control.getBaseId());
            } else {
                control = getChildControlInfo(id).getControl();
            }

            addPageAlias(control);
        }

        return control;
    }

}
