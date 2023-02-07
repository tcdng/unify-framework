/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.web.ui.widget.panel;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.ui.widget.AbstractPanel;
import com.tcdng.unify.web.ui.widget.Layout;

/**
 * Repeat panel.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-repeatpanel")
@UplAttributes({ @UplAttribute(name = "layout", type = Layout.class, mandatory = true),
		@UplAttribute(name = "components", type = UplElementReferences.class, mandatory = true) })
public class RepeatPanel extends AbstractPanel {

	private List<ValueStore> valueStoreList;

	@Override
	public void cascadeValueStore() throws UnifyException {
		List<?> value = getValue(List.class);
		if (value != null) {
			valueStoreList = new ArrayList<ValueStore>();
			final int len = value.size();
			for (int i = 0; i < len; i++) {
				valueStoreList.add(createValueStore(value.get(i), i));
			}
		} else {
			valueStoreList = null;
		}
	}

	@Override
	public List<ValueStore> getRepeatValueStores() throws UnifyException {
		return valueStoreList;
	}

	@Override
	public boolean isRepeater() {
		return true;
	}

}
