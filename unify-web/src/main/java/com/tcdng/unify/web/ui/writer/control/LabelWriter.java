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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.Label;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Label writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Label.class)
@Component("label-writer")
public class LabelWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		Label label = (Label) widget;
		writer.write("<span");
		this.writeTagAttributes(writer, label);
		writer.write(">");
		String value = label.getStringValue();
		if (value != null) {
			if (label.isHtmlEscape()) {
				writer.writeWithHtmlEscape(value);
			} else {
				writer.write(value);
			}
		} else {
			if (!StringUtils.isBlank(label.getBinding())) {
				writer.writeHtmlFixedSpace();
			} else if (!label.isLayoutCaption()) {
				String caption = label.getCaption();
				if (caption != null) {
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
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		super.doWriteBehavior(writer, widget);
		Label label = (Label) widget;
		if (label.getUplAttribute(boolean.class, "draggable")) {
			writer.write("ux.rigDragAndDropPopup({");
			writer.write("\"pId\":\"").write(label.getId()).write("\"});");
		}
	}

}
