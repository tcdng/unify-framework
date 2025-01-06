/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.control.ListReadOnlyTextField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * List read-only text field writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(ListReadOnlyTextField.class)
@Component("listreadonlytextfield-writer")
public class ListReadOnlyTextFieldWriter extends TextFieldWriter {

    @Override
    protected String getFacadeStringValue(TextField textField) throws UnifyException {
        ListReadOnlyTextField listReadOnlyTextField = (ListReadOnlyTextField) textField;
        String list = listReadOnlyTextField.getList();
        String itemKey = listReadOnlyTextField.getStringValue();
        if (!StringUtils.isBlank(itemKey)) {
            Listable listable = getListItemByKey(LocaleType.SESSION, list, itemKey);
            if (listable != null) {
                return listable.getListDescription();
            }
        }
        return null;
    }

}
