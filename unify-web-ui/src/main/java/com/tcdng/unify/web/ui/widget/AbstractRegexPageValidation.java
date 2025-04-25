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

import java.util.List;
import java.util.regex.Matcher;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.regex.RegexPatternStore;
import com.tcdng.unify.web.ui.DataTransfer;
import com.tcdng.unify.web.ui.DataTransferBlock;

/**
 * Abstract regex page validation.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractRegexPageValidation extends AbstractPageValidation {

    private String validationCode;

    private String regexKey;

    private String errorKey;

    public AbstractRegexPageValidation(String validationCode, String regexKey, String errorKey) {
        this.validationCode = validationCode;
        this.regexKey = regexKey;
        this.errorKey = errorKey;
    }

    @Override
    public boolean validate(List<Widget> widgets, DataTransfer dataTransfer) throws UnifyException {
        boolean pass = true;
        for (Widget widget : widgets) {
            if (widget.isVisible()) {
                boolean localPass = true;
                DataTransferBlock dataTransferBlock = dataTransfer.getDataTransferBlock(widget.getId());
                if (dataTransferBlock != null) {
                    String value = getTransferValue(String.class, dataTransferBlock);
                    if (value != null) {
                        Matcher matcher =
                                ((RegexPatternStore) getComponent(ApplicationComponents.APPLICATION_REGEXPATTERNSTORE))
                                        .getPattern(getSessionLocale(), regexKey).matcher(value);
                        if (!matcher.matches()) {
                            String caption = widget.getUplAttribute(String.class, "caption");
                            String message = getSessionMessage(errorKey, caption);
                            addValidationFail((Control) widget, validationCode, message);
                            pass = localPass = false;
                        }
                    }
                }

                if (localPass) {
                    addValidationPass((Control) widget, validationCode);
                }
            }
        }

        return pass;
    }
}
