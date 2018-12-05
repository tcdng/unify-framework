/*
 * Copyright 2014 The Code Department
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
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.data.MessageBox;

/**
 * Represents a message box panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-messageboxpanel")
@UplBinding("web/panels/upl/messageboxpanel.upl")
public class MessageBoxPanel extends AbstractPanel {

	@Override
	public void switchState() throws UnifyException {
		super.switchState();

		this.setVisible("okBtn", false);
		this.setVisible("yesBtn", false);
		this.setVisible("noBtn", false);
		this.setVisible("retryBtn", false);
		this.setVisible("cancelBtn", false);

		MessageBox mbi = this.getValue(MessageBox.class);
		switch (mbi.getMessageMode()) {
		case OK_CANCEL:
			this.setVisible("okBtn", true);
			this.setVisible("cancelBtn", true);
			break;
		case RETRY_CANCEL:
			this.setVisible("retryBtn", true);
			this.setVisible("cancelBtn", true);
			break;
		case YES_NO:
			this.setVisible("yesBtn", true);
			this.setVisible("noBtn", true);
			break;
		case YES_NO_CANCEL:
			this.setVisible("yesBtn", true);
			this.setVisible("noBtn", true);
			this.setVisible("cancelBtn", true);
			break;
		case OK:
		default:
			this.setVisible("okBtn", true);
			break;

		}
	}

}
