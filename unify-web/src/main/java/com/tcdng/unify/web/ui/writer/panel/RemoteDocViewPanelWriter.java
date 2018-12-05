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
package com.tcdng.unify.web.ui.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.RemoteDocViewPanel;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Remote document view panel writer.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
@Writes(RemoteDocViewPanel.class)
@Component("remotedocviewpanel-writer")
public class RemoteDocViewPanelWriter extends AbstractPanelWriter {

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		RemoteDocViewPanel remoteDocViewPanel = (RemoteDocViewPanel) widget;
		writer.write("ux.loadRemoteDocViewPanel({");
		writer.write("\"pId\":\"").write(remoteDocViewPanel.getId()).write('"');
		writer.write(",\"pWinPgNm\":\"").write(getResponseControllerWinId()).write("\"");
		writer.write(",\"pRemoteURL\":\"").write(remoteDocViewPanel.getRemoteDocViewInfo().getRemoteDocUrl())
				.write("\"");

		UserToken userToken = this.getUserToken();
		writer.write(",\"pRemoteLoginId\":\"").write(userToken.getUserLoginId()).write("\"");
		writer.write(",\"pRemoteUserName\":\"").write(userToken.getUserName()).write("\"");
		if (userToken.getRoleCode() != null) {
			writer.write(",\"pRemoteRoleCode\":\"").write(userToken.getRoleCode()).write("\"");
		}

		writer.write("});");
	}

	@Override
	protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
		writer.write("<div id=\"").write(getResponseControllerWinId()).write("\" style=\"width:100%;height:100%;\">");
		writer.write("</div>");
	}

	private String getResponseControllerWinId() throws UnifyException {
		return "win_" + this.getPageManager()
				.getPageName(this.getRequestContextUtil().getResponsePageControllerInfo().getControllerId());
	}
}
