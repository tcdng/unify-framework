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
package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract base class for UI panel writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPanelWriter extends AbstractContainerWriter implements PanelWriter {

    @Override
    public void writeInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        panel.cascadeValueStore();
        doWriteInnerStructureAndContent(writer, panel);
    }

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Panel panel = (Panel) widget;
        writer.write("<div");
        writePanelTagAttributes(writer, panel);
        writer.write(">");
        writeInnerStructureAndContent(writer, panel);
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        Panel panel = (Panel) widget;
        if (panel.isAllowRefresh()) {
            int refreshEvery = panel.getRefreshEvery();
            if (refreshEvery > 0) {
                // Append delayed post
                String path = panel.getRefreshPath();
                writer.write("ux.setDelayedPanelPost({");
                writer.write("\"pId\":\"").write(panel.getId()).write('"');
                writer.write(",\"pURL\":\"");
                if (path == null) {
                    writer.writeCommandURL();
                } else {
                    writer.writeContextURL(path);
                }
                writer.write('"');
                writer.write(",\"pPeriodMilliSec\":").write(refreshEvery);
                writer.write("});");
            }
        }
    }

    protected void writePanelTagAttributes(ResponseWriter writer, Panel panel) throws UnifyException {
        String backImageSrc = panel.getBackImageSrc();
        if (!StringUtils.isBlank(backImageSrc)) {
            writeTagId(writer, panel);
            writeTagStyleClass(writer, panel);
            writer.write(" style=\"background: url('");
            writer.writeFileImageContextURL(backImageSrc);
            writer.write("') no-repeat;background-size:100% 100%;");
            String style = panel.getStyle();
            if (style != null) {
                writer.write(style);
            }
            writer.write("\"");
        } else {
            writeTagAttributes(writer, panel);
        }
    }

    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        String legend = panel.getLegend();

        boolean isLegend = !StringUtils.isBlank(legend);
        if (isLegend) {
            writer.write("<fieldset><legend>");
            writeAttributeWithEscape(writer, panel, "legend");
            writer.write("</legend>");
        }

        if (!getRequestContextUtil().isPanelSwitched(panel)) {
            panel.switchState();
        }

        writeLayoutContent(writer, panel);

        if (isLegend) {
            writer.write("</fieldset>");
        }
    }
}
