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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.constant.MessageType;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.Label;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Label writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(Label.class)
@Component("label-writer")
public class LabelWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Label label = (Label) widget;
        writer.write("<span");
        if (writer.isTableMode()) {
            writeTagStyle(writer, label);
        } else {
         	MessageType type = label.getType();
            if (type == null) {
            	writeTagAttributes(writer, label);
            } else {
            	writeTagAttributesWithTrailingExtraStyleClass(writer, label, type.styleClass());
            }
        }
        writer.write(">");
        String value = label.getStringValue();
        if (value != null) {
        	value = label.isTextUppercase() ? value.toUpperCase() : value;
            if (label.isHtmlEscape()) {
                writer.writeWithHtmlEscape(value);
            } else {
                writer.write(value);
            }
        } else {
            if (!label.isBindingOptional() && StringUtils.isNotBlank(label.getBinding())) {
                writer.writeHtmlFixedSpace();
            } else if (!label.isLayoutCaption()) {
                String caption = label.getCaption();
                if (caption != null) {
                	caption = label.isTextUppercase() ? caption.toUpperCase() : caption;
                    if (label.isHtmlEscape()) {
                        writer.writeWithHtmlEscape(caption);
                    } else {
                        writer.write(caption);
                    }
                }
            }
        }
        writer.write("</span>");
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		Label label = (Label) widget;
		if (label.getUplAttribute(boolean.class, "draggable")) {
			writer.beginFunction("ux.rigDragAndDropPopup");
			writer.writeParam("pId", label.getId());
			writer.endFunction();
		}
	}

}
