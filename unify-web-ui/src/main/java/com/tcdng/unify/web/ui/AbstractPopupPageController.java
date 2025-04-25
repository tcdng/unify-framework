/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.constant.ReadOnly;
import com.tcdng.unify.web.constant.ResetOnWrite;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.ui.widget.data.Popup;

/**
 * Convenient base for pop-up page controller implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplBinding("web/reserved/upl/popuppage.upl")
@ResultMappings({
        @ResultMapping(name = "showpagepopup", response = { "!showpopupresponse popup:$s{pagePopup}" }) })
public abstract class AbstractPopupPageController<T> extends AbstractPageController<PopupPageBean> {

	private final String title;
	
	private final String panelName;
	
	private final int widthInPixels;
	
	public AbstractPopupPageController(String title, String panelName, int widthInPixels, Secured secure) {
		super(PopupPageBean.class, secure, ReadOnly.FALSE, ResetOnWrite.FALSE);
		this.title = title;
		this.panelName = panelName;
		this.widthInPixels = widthInPixels;
	}

	@Override
	protected final boolean isContentSupport() throws UnifyException {
		return false;
	}

	@Override
	protected final void onOpenPage() throws UnifyException {
		final PopupPageBean pageBean = getPageBean();
		pageBean.setTitle(resolveSessionMessage(title));
		pageBean.setPanelName(panelName);

		T popupBean = getPopupBean();
		pageBean.setPanelBean(popupBean);

		setRequestAttribute(UnifyWebRequestAttributeConstants.POPUP,
				new Popup("showpagepopup", pageBean, widthInPixels, 0));
		setResultMapping("showpagepopup");
	}

	protected abstract T getPopupBean() throws UnifyException;
}
