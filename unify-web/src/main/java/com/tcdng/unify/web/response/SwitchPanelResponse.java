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
package com.tcdng.unify.web.response;

import java.util.Arrays;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.panel.SwitchPanel;

/**
 * Used for generating a switch panel response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("switchpanelresponse")
public class SwitchPanelResponse extends RefreshPanelResponse {

	private String[] targets;

	private String[] panels;

	@Override
	protected void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException {
		String[] panels = getPanels();
		logDebug("Preparing switch panel response: controller = [{0}], panelCount = [{1}]", pageController.getName(),
				panels.length);
		for (int i = 0; i < panels.length; i++) {
			SwitchPanel switchPanel = (SwitchPanel) pageController.getPanelByShortName(panels[i]);
			switchPanel.switchContent(targets[i]);
		}
		super.doGenerate(writer, pageController);
	}

	@Override
	protected void onInitialize() throws UnifyException {
		panels = super.getPanels();
		panels = Arrays.copyOf(panels, panels.length);
		targets = new String[panels.length];

		for (int i = 0; i < panels.length; i++) {
			int index = panels[i].lastIndexOf('.');
			targets[i] = panels[i].substring(index + 1);
			panels[i] = panels[i].substring(0, index);
		}
	}

	@Override
	protected String[] getPanels() throws UnifyException {
		return panels;
	}
}
