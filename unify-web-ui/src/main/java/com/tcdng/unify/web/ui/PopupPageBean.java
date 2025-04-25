/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.web.ui;

/**
 * Pop-up page bean.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class PopupPageBean extends AbstractPageBean {

	private String title;

	private String panelName;
	
	private Object panelBean;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	public Object getPanelBean() {
		return panelBean;
	}

	public void setPanelBean(Object panelBean) {
		this.panelBean = panelBean;
	}
}
