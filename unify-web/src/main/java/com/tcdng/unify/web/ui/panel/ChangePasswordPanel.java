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
package com.tcdng.unify.web.ui.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.Widget;

/**
 * A basic change password panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-changepasswordpanel")
@UplBinding("web/panels/upl/changepasswordpanel.upl")
public class ChangePasswordPanel extends AbstractPanel {

    @Action
    @Override
    public void switchState() throws UnifyException {
        super.switchState();
        Widget messageLabel = getWidgetByShortName("pwdChangeMsg");
        messageLabel.setVisible(messageLabel.getValue(String.class) != null);
    }
}
