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
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.data.ButtonGroupInfo;

/**
 * Button group widget.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-buttongroup")
public class ButtonGroup extends AbstractMultiControl {

    private Control buttonCtrl;

    public ButtonGroupInfo getButtonGroupInfo() throws UnifyException {
    	return getValue(ButtonGroupInfo.class);
    }
    
    public Control getButtonCtrl() {
		return buttonCtrl;
	}

    @Override
	public boolean isSupportStretch() {
		return false;
	}

	@Override
    public boolean isLayoutCaption() throws UnifyException {
        return false;
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        buttonCtrl =  (Control) addInternalChildWidget(
                "!ui-button styleClass:$e{btn} captionBinding:label binding:value");
    }

}
