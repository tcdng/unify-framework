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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.TextCase;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractFormattedControl;

/**
 * Represents a text field.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-text")
@UplAttributes({
		@UplAttribute(name = "size", type = int.class),
		@UplAttribute(name = "minLen", type = int.class),
        @UplAttribute(name = "maxLen", type = int.class),
        @UplAttribute(name = "case", type = TextCase.class),
        @UplAttribute(name = "placeholder", type = String.class),
        @UplAttribute(name = "placeholderBinding", type = String.class),
        @UplAttribute(name = "trim", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "spellCheck", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "extStyleClass", type = String.class, defaultVal = "tread"),
        @UplAttribute(name = "extReadOnly", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "clientFormat", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "autocomplete", type = boolean.class)})
public class TextField extends AbstractFormattedControl {

    @Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (isTrim()) {
			transferBlock.trimValue();
		}
		
		super.populate(transferBlock);
	}

	public TextCase getCase() throws UnifyException {
        return getUplAttribute(TextCase.class, "case");
    }
    
    public boolean isTrim() throws UnifyException {
        return getUplAttribute(boolean.class, "trim");
    }
    
    public boolean isSpellCheck() throws UnifyException {
        return getUplAttribute(boolean.class, "spellCheck");
    }

    public boolean isClientFormat() throws UnifyException {
        return getUplAttribute(boolean.class, "clientFormat");
    }

    public String getExtStyleClass() throws UnifyException {
        return getUplAttribute(String.class, "extStyleClass");
    }

    public String getPlaceholder() throws UnifyException {
    	final String placeholderBinding = getUplAttribute(String.class, "placeholderBinding");
    	if (!StringUtils.isBlank(placeholderBinding)) {
    		return getStringValue(placeholderBinding);
    	}
    	
        return getUplAttribute(String.class, "placeholder");
    }

    public boolean getExtReadOnly() throws UnifyException {
        return getUplAttribute(boolean.class, "extReadOnly");
    }

    public boolean isAutoComplete() throws UnifyException {
        return getUplAttribute(boolean.class, "autocomplete");
    }

    @Override
    public boolean isUseFacade() throws UnifyException {
        return !getExtReadOnly();
    }
    
	public ExtensionType getExtensionType() throws UnifyException {
        return ExtensionType.NONE;
    }
    
	public boolean isHiddenMimic() throws UnifyException {
        return false;
    }

    @Override
    public boolean setFocus() throws UnifyException {
        if (isUseFacade()) {
            return getRequestContextUtil().setFocusOnWidgetId(getFacadeId());
        }

        return super.setFocus();
    }

}
