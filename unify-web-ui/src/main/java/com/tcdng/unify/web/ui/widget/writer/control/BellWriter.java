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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.Bell;
import com.tcdng.unify.web.ui.widget.writer.AbstractWidgetWriter;

/**
 * Bell writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(Bell.class)
@Component("bell-writer")
public class BellWriter extends AbstractWidgetWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Bell bell = (Bell) widget;
        writer.write("<div ");
        writeTagAttributes(writer, bell);
        writer.write(">");
        int notificationCount = bell.getValue(int.class);
        if (notificationCount > 0) {
            writer.write("<span>").write(notificationCount).write("</span>");
        }
        writer.write("<img src=\"").writeFileImageContextURL(bell.getImageSrc()).write("\">");
        writer.write("</div>");
    }

}
