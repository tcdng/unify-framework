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
package com.tcdng.unify.web.ui.widget.validation;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.DataTransfer;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractPageValidation;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * UPL descriptor page validation.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-upldescriptorvalidation")
public class UplDescriptorValidation extends AbstractPageValidation {

    @Override
    public boolean validate(List<Widget> widgets, DataTransfer dataTransfer) throws UnifyException {
        boolean pass = true;
        for (Widget widget : widgets) {
            boolean localPass = true;
            DataTransferBlock transferBlock = dataTransfer.getDataTransferBlock(widget.getId());
            if (transferBlock != null) {
                try {
                    String descriptor = getTransferValue(String.class, transferBlock);
                    getUplComponent(getSessionLocale(), descriptor, false);
                } catch (Exception e) {
                    String caption = widget.getUplAttribute(String.class, "caption");
                    String message = getSessionMessage("validation.invalidupldescriptor", caption);
                    addValidationFail((Control) widget, "upldescriptor", message);
                    pass = localPass = false;
                }
            }

            if (localPass) {
                addValidationPass((Control) widget, "upldescriptor");
            }
        }
        return pass;
    }

}
