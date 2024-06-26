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
package com.tcdng.unify.web.ui.widget.container;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.widget.AbstractHtmlPage;
import com.tcdng.unify.web.ui.widget.PlainHtml;

/**
 * Plain HTML
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-plainhtml")
public class BasicPlainHtml extends AbstractHtmlPage implements PlainHtml {

	private String bodyContent;
	
	private String scripts;

	@Override
	public String getBodyContent() {
		return bodyContent;
	}

	@Override
	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}

	@Override
	public String getScripts() {
		return scripts;
	}

	@Override
	public void setScripts(String scripts) {
		this.scripts = scripts;
	}
}
