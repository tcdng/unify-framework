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

package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.BeanValueListStore;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.data.ValueStoreReader;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.WidgetStatePolicy;
import com.tcdng.unify.web.ui.widget.control.ButtonGroup;
import com.tcdng.unify.web.ui.widget.data.ButtonGroupInfo;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Button group writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(ButtonGroup.class)
@Component("buttongroup-writer")
public class ButtonGroupWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		ButtonGroup buttonGroup = (ButtonGroup) widget;
		writer.write("<div");
		writeTagAttributes(writer, buttonGroup);
		writer.write(">");
		ButtonGroupInfo buttonGroupInfo = buttonGroup.getButtonGroupInfo();
		if (buttonGroupInfo != null) {
			final ValueStore valueStore = new BeanValueListStore(buttonGroupInfo.getInfoList());
			final Control buttonCtrl = buttonGroup.getButtonCtrl();
			final boolean groupDisabled = buttonGroup.isDisabled();
			final boolean isParentReader = buttonGroupInfo.isWithParentReader();
			final int len = valueStore.size();
			for (int i = 0; i < len; i++) {
				valueStore.setDataIndex(i);
				buttonCtrl.setDisabled(groupDisabled
						|| (isParentReader && resolveDisabled(valueStore, buttonGroupInfo.getParentReader())));
				buttonCtrl.setValueStore(valueStore);
				writer.writeStructureAndContent(buttonCtrl);
			}
		}

		writer.write("</div>");
	}

	private boolean resolveDisabled(ValueStore buttonValueStore, ValueStoreReader parentReader) throws UnifyException {
		final String policy = buttonValueStore.retrieve(String.class, "value");
		if (policy != null && isComponent(policy)) {
			UnifyComponent _policy = getComponent( policy);
			if (_policy instanceof WidgetStatePolicy) {
				return ((WidgetStatePolicy) _policy).isWidgetDisabled(parentReader);
			}
		}

		return false;
	}
}
