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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.ListParamType;
import com.tcdng.unify.web.ui.data.RefreshSection;

/**
 * A search input control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-search")
@UplAttributes({ @UplAttribute(name = "filterLabel", type = String.class, defaultValue = "$m{search.filter}"),
		@UplAttribute(name = "buttonImgSrc", type = String.class, defaultValue = "$t{images/search.png}"),
		@UplAttribute(name = "listParamType", type = ListParamType.class, defaultValue = "immediate") })
public class SearchField extends AbstractListPopupTextField {

	private String filter;

	private boolean keyOnly;

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.FACADE_HIDDEN;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	public String[] getListParams() throws UnifyException {
		String[] params = new String[4];
		if (keyOnly) {
			params[0] = this.getStringValue();
		}

		params[1] = this.filter;
		return params;
	}

	@Override
	public String getFacadeStringValue() throws UnifyException {
		String key = this.getStringValue();
		if (key != null) {
			return this.getListMap().get(key);
		}

		return null;
	}

	@Action
	public void search() throws UnifyException {
		this.filter = this.getRequestTarget(String.class);
		this.setKeyOnly(false);

		this.setRequestAttribute(UnifyWebRequestAttributeConstants.REFRESH_SECTION,
				new RefreshSection(this, this.getResultPanelId()));
		this.setCommandResultMapping(ResultMappingConstants.REFRESH_SECTION);
	}

	public String getFilter() {
		return filter;
	}

	public boolean isKeyOnly() {
		return keyOnly;
	}

	public void setKeyOnly(boolean keyOnly) {
		this.keyOnly = keyOnly;
	}

	public String getFilterLabel() throws UnifyException {
		return this.resolveSessionMessage(this.getUplAttribute(String.class, "filterLabel"));
	}

	public String getFilterId() throws UnifyException {
		return this.getPrefixedId("fil_");
	}

	public String getSearchPanelId() throws UnifyException {
		return this.getPrefixedId("sch_");
	}

	public String getResultPanelId() throws UnifyException {
		return this.getPrefixedId("rlt_");
	}

	public String getClearButtonId() throws UnifyException {
		return this.getPrefixedId("clr_");
	}

	public String getCancelButtonId() throws UnifyException {
		return this.getPrefixedId("can_");
	}
}
