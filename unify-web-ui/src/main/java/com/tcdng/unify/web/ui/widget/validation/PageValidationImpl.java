/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.web.ui.widget.validation;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.DataTransfer;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractPageValidation;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.Section;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.container.Form;
import com.tcdng.unify.web.ui.widget.control.DynamicField;
import com.tcdng.unify.web.ui.widget.control.MultiDynamic;
import com.tcdng.unify.web.ui.widget.control.MultiDynamic.ValueStore;
import com.tcdng.unify.web.ui.widget.panel.DynamicPanel;
import com.tcdng.unify.web.ui.widget.panel.StandalonePanel;

/**
 * Default page component validation implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-validation")
public class PageValidationImpl extends AbstractPageValidation {

    @Override
    public boolean validate(List<Widget> widgets, DataTransfer dataTransfer) throws UnifyException {
        boolean pass = true;
        for (Widget widget : widgets) {
            if (widget.isVisible()) {
                if (widget instanceof Form) {
                    Form form = (Form) widget;
                    Section[] sections = form.getUplAttribute(Section[].class, "section");
                    for (Section section : sections) {
                        List<String> longNames = section.getShallowReferencedLongNames("components");
                        for (String longName : longNames) {
                            Widget frmWidget = form.getWidgetByLongName(longName);
                            if (frmWidget.isVisible()) {
                                if (frmWidget instanceof Control) {
                                    pass &= validateWidget((Control) frmWidget, dataTransfer);
                                }
                            }
                        }
                    }
                } else if (widget instanceof MultiDynamic) {
                    MultiDynamic multiDynamic = (MultiDynamic) widget;
                    if (multiDynamic.isContainerVisible() && multiDynamic.isContainerEditable()) {
                        List<ValueStore> list = multiDynamic.getValueList();
                        if (list != null) {
                            DataTransferBlock dataTransferBlock =
                                    dataTransfer.getDataTransferBlock(multiDynamic.getId());
                            DynamicField valueCtrl = (DynamicField) multiDynamic.getValueCtrl();
                            while (dataTransferBlock != null) {
                                boolean localPass = true;
                                DataTransferBlock dynamicCtrlBlock = dataTransferBlock.getChildBlock();
                                MultiDynamic.ValueStore store =
                                        list.get(dynamicCtrlBlock.getChildBlock().getItemIndex());
                                valueCtrl.setValueStore(store.getValueStore());
                                Control control = valueCtrl.getControl();

                                if (store.isRequired()) {
                                    String value = getTransferValue(String.class, dynamicCtrlBlock.getChildBlock());
                                    if (value == null || StringUtils.isBlank(value)) {
                                        addValidationFail(control, "required",
                                                getSessionMessage("validation.required", store.getCaption()));
                                        localPass = false;
                                        pass = false;
                                    }
                                }

                                if (localPass) {
                                    addValidationPass(control, null);
                                }

                                dataTransferBlock = dataTransferBlock.getSiblingBlock();
                            }
                        }
                    }
                } else if (widget instanceof DynamicPanel) {
                    PageManager pageManager = getPageManager();
                    StandalonePanel standalonePanel = ((DynamicPanel) widget).getStandalonePanel();
                    for (String longName : standalonePanel.getPageValidationNames()) {
                        pass &= standalonePanel.getPageWidgetValidator(pageManager, longName).validate(dataTransfer);
                    }
                } else if (widget instanceof Control) {
                    pass &= validateWidget((Control) widget, dataTransfer);
                }
            }
        }

        return pass;
    }

    private boolean validateWidget(Control control, DataTransfer dataTransfer) throws UnifyException {
        DataTransferBlock dataTransferBlock = dataTransfer.getDataTransferBlock(control.getId());
        if (dataTransferBlock != null) {
            String caption = control.getUplAttribute(String.class, "caption");
            String value = getTransferValue(String.class, dataTransferBlock);

            if (control.getRequired().isTrue()) {
                if (value == null || StringUtils.isBlank(value)) {
                    addValidationFail(control, "required", getSessionMessage("validation.required", caption));
                    return false;
                }
            }

            if (control.isUplAttribute("minLen")) {
                int minLen = control.getUplAttribute(int.class, "minLen");
                if (minLen > 0) {
                    if (value == null || value.length() < minLen) {
                        addValidationFail(control, "minLen",
                                getSessionMessage("validation.greaterthanorequal", caption, minLen));
                        return false;
                    }
                }
            }

            if (control.isUplAttribute("maxLen")) {
                int maxLen = control.getUplAttribute(int.class, "maxLen");
                if (maxLen > 0) {
                    if (value != null && value.length() > maxLen) {
                        addValidationFail(control, "maxLen",
                                getSessionMessage("validation.lessthanorequal", caption, maxLen));
                        return false;
                    }
                }
            }

            addValidationPass(control, null);
        }

        return true;
    }
}
