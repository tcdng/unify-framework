/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.ColorScheme;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.Badge;
import com.tcdng.unify.web.ui.data.BadgeInfo;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Badge writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Badge.class)
@Component("badge-writer")
public class BadgeWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Badge badge = (Badge) widget;
        String caption = null;
        ColorScheme scheme = null;
        Object value = badge.getValue();
        if (value instanceof String) {
            caption = resolveSessionMessage((String) value);
        } else if (value instanceof BadgeInfo) {
            BadgeInfo badgeInfo = (BadgeInfo) value;
            scheme = badgeInfo.getColorScheme();
            caption = resolveSessionMessage(badgeInfo.getCaption());
        }

        writer.write("<span");
        String sel = "badgesel";
        if (scheme != null) {
            sel += scheme.code();
        }
        writeTagId(writer, badge);
        writeTagStyleClassWithTrailingExtraStyleClasses(writer, badge, sel);
        writeTagStyle(writer, badge);
        writer.write(">");
        if (StringUtils.isBlank(caption)) {
            caption = badge.getCaption();
        }

        if (caption != null) {
            writer.writeWithHtmlEscape(caption);
        }
        writer.write("</span>");
    }
}
