/*
 * Copyright 2018 The Code Department
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplUtils;
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Widget;

/**
 * Represents a dynamic panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-dynamicpanel")
@UplAttributes({ @UplAttribute(name = "panelNameBinding", type = String.class, mandatory = true),
		@UplAttribute(name = "panelValueBinding", type = String.class),
		@UplAttribute(name = "hideOnNoComponents", type = boolean.class, defaultValue = "false") })
public class DynamicPanel extends AbstractPanel {

	private Set<String> panelNames;

	public DynamicPanel() {
		panelNames = new HashSet<String>();
	}

	@Override
	public void resetState() throws UnifyException {
		getStandalonePanel().resetState();
	}

	@Override
	public Widget getRelayWidget() throws UnifyException {
		return getStandalonePanel();
	}

	@Override
	public boolean isRelayCommand() {
		return true;
	}

	public String getHiddenId() throws UnifyException {
		return getPrefixedId("hid_");
	}

	public StandalonePanel getStandalonePanel() throws UnifyException {
		String panelName = (String) getValue(getUplAttribute(String.class, "panelNameBinding"));
		String uniqueName = UplUtils.generateUplComponentCloneName(panelName,
				getPageManager().getPageName(getLongName()));
		Page page = getRequestContextUtil().getRequestPage();
		getPageManager().invalidateStaleDocument(uniqueName);
		StandalonePanel standalonePanel = page.getStandalonePanel(uniqueName);
		if (standalonePanel == null) {
			standalonePanel = getPageManager().createStandalonePanel(getSessionLocale(), uniqueName);
			page.addStandalonePanel(uniqueName, standalonePanel);
			getControllerManager().updatePageControllerInfo(
					getRequestContextUtil().getResponsePageControllerInfo().getControllerId(), uniqueName);
			panelNames.add(uniqueName);
		}
		setValueStore(standalonePanel);
		return standalonePanel;
	}

	@Override
	public void addPageAliases() throws UnifyException {
		List<String> aliases = getPageManager().getExpandedReferences(getStandalonePanel().getId());
		getRequestContextUtil().addPageAlias(getId(), aliases.toArray(new String[aliases.size()]));
	}

	private void setValueStore(StandalonePanel standalonePanel) throws UnifyException {
		ValueStore valueStore = getValueStore();
		String valueBinding = getUplAttribute(String.class, "panelValueBinding");
		if (valueBinding != null) {
			ValueStore oldValueStore = standalonePanel.getValueStore();
			Object valueObject = getValue(valueBinding);
			if (oldValueStore == null || oldValueStore.getValueObject() != valueObject) {
				standalonePanel.setValueStore(createValueStore(valueObject));
			}
		} else {
			standalonePanel.setValueStore(valueStore);
		}
	}
}
