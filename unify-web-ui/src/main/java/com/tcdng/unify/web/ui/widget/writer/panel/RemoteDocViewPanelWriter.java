/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.RemoteDocViewInfo;
import com.tcdng.unify.web.ui.widget.panel.RemoteDocViewPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Remote document view panel writer.
 * 
 * @author The Code Department
 * @version 1.0
 */
@Writes(RemoteDocViewPanel.class)
@Component("remotedocviewpanel-writer")
public class RemoteDocViewPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers) throws UnifyException {
        RemoteDocViewPanel remoteDocViewPanel = (RemoteDocViewPanel) widget;
        writer.beginFunction("ux.loadRemoteDocViewPanel");
        writer.writeParam("pId", remoteDocViewPanel.getId());
        writer.writeParam("pWinPgNm", getResponseControllerWinId());
        RemoteDocViewInfo remoteDocViewInfo = remoteDocViewPanel.getRemoteDocViewInfo();
        writer.writeParam("pRemoteURL", remoteDocViewInfo.getRemoteDocUrl());

        UserToken userToken = getUserToken();
        writer.writeParam("pLoginId", userToken.getUserLoginId());
        writer.writeParam("pUserName", userToken.getUserName());
        if (StringUtils.isNotBlank(userToken.getRoleCode())) {
            writer.writeParam("pRoleCode", userToken.getRoleCode());
        }

        if (StringUtils.isNotBlank(userToken.getBranchCode())) {
            writer.writeParam("pBranchCode", userToken.getBranchCode());
        }

        if (StringUtils.isNotBlank(remoteDocViewInfo.getColorScheme())) {
            writer.writeParam("pColorScheme", remoteDocViewInfo.getColorScheme());
        }

        writer.writeParam("pGlobalFlag", userToken.isGlobalAccess());
        writer.endFunction();
    }

    @Override
    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        writer.write("<div id=\"").write(getResponseControllerWinId()).write("\" style=\"width:100%;height:100%;\">");
        writer.write("</div>");
    }

    private String getResponseControllerWinId() throws UnifyException {
        return "win_" + getPageManager()
                .getPageName(getRequestContextUtil().getResponsePathParts().getControllerPathId());
    }
}
