/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.web.ui.widget.writer.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DownloadLinkGrid;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Download link grid writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DownloadLinkGrid.class)
@Component("downloadlinkgrid-writer")
public class DownloadLinkGridWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DownloadLinkGrid downloadLinkGrid = (DownloadLinkGrid) widget;
        writer.write("<div");
        writeTagAttributes(writer, downloadLinkGrid);
        writer.write(">");
        writer.write("<div><span class=\"dlgcatcap\">");
        writer.writeWithHtmlEscape(downloadLinkGrid.getCaption());
        writer.write("</span></div>");
        List<String> resourceList = downloadLinkGrid.getResourceList();
        if (DataUtils.isNotBlank(resourceList)) {
            writer.write("<div><table style=\"width:100%;\">");
            int columns = downloadLinkGrid.getUplAttribute(int.class, "columns");
            if (columns <= 0) {
                columns = 1;
            }

            Integer colWidth = 100 / columns; // Column with in percentage
            int len = resourceList.size();
            for (int i = 0; i < len;) {
                writer.write("<tr>");
                for (int k = 0; k < columns; k++, i++) {
                    writer.write("<td style=\"width:").write(colWidth).write("%;\">");
                    if (i < len) {
                        String resourceName = resourceList.get(i);
                        writer.write("<a class=\"dlglink\" href=\"");
                        writer.writeContextResourceURL("/resource/downloadpath",
                                MimeType.APPLICATION_OCTETSTREAM.template(), resourceName, null, true, false);
                        writer.write("\">");
                        writer.writeWithHtmlEscape(resourceName);
                        writer.write("</a>");
                    }
                    writer.write("</td>");
                }
                writer.write("</tr>");
            }
            writer.write("</table></div>");
        }
        writer.write("</div>");
    }
}
