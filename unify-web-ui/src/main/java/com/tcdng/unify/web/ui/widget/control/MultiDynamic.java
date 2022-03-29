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
package com.tcdng.unify.web.ui.widget.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.Input;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractValueListMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * A dynamic control for capturing multiple input values with each value
 * captured by a different rendered child control that is determined at runtime.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-multidynamic")
@UplAttributes({ @UplAttribute(name = "isRequiredSymbol", type = String.class, defaultVal = "*"),
        @UplAttribute(name = "captionSuffix", type = String.class, defaultVal = ":") })
public class MultiDynamic extends AbstractValueListMultiControl<MultiDynamic.ValueStore, Input<?>> {

    private DynamicField valueCtrl;

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        if (transferBlock != null) {
            DataTransferBlock dynamicCtrlBlock = transferBlock.getChildBlock();
            Control control = (Control) getChildWidgetInfo(dynamicCtrlBlock.getId()).getWidget();
            control.setValueStore(getValueList().get(dynamicCtrlBlock.getChildBlock().getItemIndex()).getValueStore());
            control.populate(dynamicCtrlBlock);
        }
    }

    public String getCaptionSuffix() throws UnifyException {
        return getUplAttribute(String.class, "captionSuffix");
    }

    public String getIsRequiredSymbol() throws UnifyException {
        return getUplAttribute(String.class, "isRequiredSymbol");
    }

    public DynamicField getValueCtrl() {
        return valueCtrl;
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        valueCtrl = (DynamicField) addInternalChildWidget("!ui-dynamic binding:value descriptorBinding:editor");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Input<?>> getItemList() throws UnifyException {
        return (List<Input<?>>) getValue();
    }

    @Override
    protected ValueStore newValue(Input<?> item, int index) throws UnifyException {
        return new ValueStore(createValueStore(item, index), item.getDescription(), item.isMandatory());
    }

    @Override
    protected void onCreateValueList(List<ValueStore> valueList) throws UnifyException {

    }

    public static class ValueStore {

        private com.tcdng.unify.core.data.ValueStore valueStore;

        private String caption;

        private boolean required;

        public ValueStore(com.tcdng.unify.core.data.ValueStore valueStore, String caption, boolean required) {
            this.valueStore = valueStore;
            this.caption = caption;
            this.required = required;
        }

        public com.tcdng.unify.core.data.ValueStore getValueStore() {
            return valueStore;
        }

        public String getCaption() {
            return caption;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
