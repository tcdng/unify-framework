/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.web.ui.widget.AbstractTargetControl;

/**
 * A button widget.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-button")
@UplAttributes({ @UplAttribute(name = "imageSrc", type = String.class),
        @UplAttribute(name = "symbol", type = String.class),
        @UplAttribute(name = "symbolBinding", type = String.class),
        @UplAttribute(name = "debounce", type = boolean.class, defaultVal = "true") })
public class Button extends AbstractTargetControl {

    public String getSymbol() throws UnifyException {
        String symbol = null;
        String symbolBinding = getUplAttribute(String.class, "symbolBinding");
        if (symbolBinding != null && !symbolBinding.isEmpty()) {
            symbol = getStringValue(symbolBinding);
        }
        
        return symbol != null ? symbol : getUplAttribute(String.class, "symbol");
    }

    @Override
	public boolean isSupportStretch() {
		return false;
	}

    @Override
    public boolean isLayoutCaption() {
        return false;
    }
}
