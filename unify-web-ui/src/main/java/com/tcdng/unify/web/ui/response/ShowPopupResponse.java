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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.data.Popup;

/**
 * Used for generating a show popup response.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("showpopupresponse")
@UplAttributes({
	@UplAttribute(name = "popup", type = String.class),
	@UplAttribute(name = "width", type = int.class),
    @UplAttribute(name = "systemInfo", type = boolean.class, defaultVal = "false") })
public class ShowPopupResponse extends AbstractJsonPageControllerResponse {

    public ShowPopupResponse() {
        super("showPopupHdl", true);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing show popup response: path ID = [{0}]", page.getPathId());
        appendPopupPanelsJSON(writer, page);
        writer.write(",");
        appendRefreshAttributesJson(writer, false);
    }

	private void appendPopupPanelsJSON(ResponseWriter writer, Page page) throws UnifyException {
		Panel panel = null;
		String popupShortName = getUplAttribute(String.class, "popup");
		if (popupShortName != null) {
			panel = page.getPanelByShortName(popupShortName);
		} else {
			String reqPopupName = getRequestContextUtil().getRequestPopupName();
			if (reqPopupName != null) {
				if (page.isWidget(reqPopupName)) {
					panel = page.getPanelByLongName(reqPopupName);
				} else {
					panel = page.getPanelByShortName(reqPopupName);
				}
			} else {
				panel = getRequestContextUtil().getRequestPopupPanel();
			}
		}

		if (panel != null) {
			final int widthInPixels = getUplAttribute(int.class, "width");
			if (widthInPixels > 0) {
				writer.write(",\"popupWidth\":").write(widthInPixels);
			} else {
				Popup popup = getRequestAttribute(Popup.class, UnifyWebRequestAttributeConstants.POPUP);
				if (popup == null) {
					popup = getSessionAttribute(Popup.class, UnifyWebSessionAttributeConstants.POPUP);
				}

				if (popup != null) {
					if (popup.getWidth() > 0) {
						writer.write(",\"popupWidth\":").write(popup.getWidth());
					}

					if (popup.getHeight() > 0) {
						writer.write(",\"popupHeight\":").write(popup.getHeight());
					}
				}
			}

			if (getUplAttribute(boolean.class, "systemInfo")) {
				writer.write(",\"showSysInfoPopup\":");
			} else {
				writer.write(",\"showPopup\":");
			}

			writer.writeJsonPanel(panel, false);
		}
	}
}
