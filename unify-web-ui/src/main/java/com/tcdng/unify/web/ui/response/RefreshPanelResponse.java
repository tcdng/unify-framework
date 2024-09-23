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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a refresh panel response.
 * 
 * @author The Code Department 
 * @since 1.0
 */
@Component("refreshpanelresponse")
@UplAttributes({ @UplAttribute(name = "panels", type = String[].class) })
public class RefreshPanelResponse extends AbstractJsonPageControllerResponse {

    public RefreshPanelResponse() {
        super("refreshPanelHdl", true);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        Panel[] panels = getRequestContextUtil().getResponseRefreshWidgetPanels();
        if (panels == null) {
            panels = resolveRefreshPanels(page);
        }
        
        appendRefreshPanelsJson(writer, page, panels);
        writer.write(",");
        appendRefreshAttributesJson(writer, false);
        appendRegisteredDebounceWidgets(writer, false);
    }

    protected String[] getPanels() throws UnifyException {
        return getUplAttribute(String[].class, "panels");
    }
    
    private void appendRefreshPanelsJson(ResponseWriter writer, Page page, Panel[] panels)
            throws UnifyException {
        logDebug("Preparing refresh panel response: path ID = [{0}]...", page.getPathId());
        writer.write(",\"refreshPanels\":[");
        if (panels != null) {
            boolean appendSym = false;
            for (int i = 0; i < panels.length; i++) {
                if (appendSym) {
                    writer.write(',');
                } else {
                    appendSym = true;
                }

                writer.writeJsonPanel(panels[i], true);
            }
        }
        writer.write("]");

        writeNoPushWidgets(writer);
        writeClientTopic(writer);
    }

	private Panel[] resolveRefreshPanels(Page page) throws UnifyException {
		boolean useLongNames = false;
		String[] refreshList = getPanels();
		if (refreshList == null || refreshList.length == 0) {
			refreshList = DataUtils.toArray(String.class, getRequestContextUtil().getResponseRefreshPanels());
			useLongNames = true;
		}

		if (refreshList != null && refreshList.length > 0) {
			Panel[] panels = new Panel[refreshList.length];
			for (int i = 0; i < refreshList.length; i++) {
				String panelName = refreshList[i];
				if (useLongNames) {
					panels[i] = page.getPanelByLongName(panelName);
				} else {
					panels[i] = page.getPanelByShortName(panelName);
				}
			}

			return panels;
		}

		return null;
	}
}
