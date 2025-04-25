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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DynamicField;
import com.tcdng.unify.web.ui.widget.control.MultiDynamic;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Multi-parameter writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(MultiDynamic.class)
@Component("multidynamic-writer")
public class MultiDynamicWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		MultiDynamic multiDynamic = (MultiDynamic) widget;
		writer.write("<div");
		writeTagAttributes(writer, multiDynamic);
		writer.write(">");
		List<MultiDynamic.ValueStore> valueStoreList = multiDynamic.getValueList();
		if (valueStoreList != null) {
			String captionSuffix = multiDynamic.getCaptionSuffix();
			String isRequiredSym = multiDynamic.getIsRequiredSymbol();
			writer.write("<table style:\"width:100%;\">");
			DynamicField valueCtrl = multiDynamic.getValueCtrl();
			int len = valueStoreList.size();
			valueCtrl.setExtraStyle(multiDynamic.getInputStyle());
			for (int i = 0; i < len; i++) {
				MultiDynamic.ValueStore itemValueStore = valueStoreList.get(i);
				writer.write("<tr>");

				writer.write("<td class=\"secLabel\"></span>");
				writer.write(itemValueStore.getValueStore().retrieve("description")).write(captionSuffix);
				writer.write("</span></td>");

				writer.write("<td class=\"secInputReq\">");
				if (itemValueStore.isRequired()) {
					writer.write("<span>").write(isRequiredSym).write("</span>");
				}
				writer.write("</td>");

				writer.write("<td class=\"secInput\"><div>");
				valueCtrl.setValueStore(itemValueStore.getValueStore());
				writer.writeStructureAndContent(valueCtrl);

				writer.write("</div><div><span id=\"").write(valueCtrl.getControl().getNotificationId())
						.write("\" class=\"secInputErr\"></span>");
				writer.write("</div></td>");

				writer.write("</tr>");
			}
			writer.write("</table>");
		}
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		MultiDynamic multiDynamic = (MultiDynamic) widget;
		List<MultiDynamic.ValueStore> valueStoreList = multiDynamic.getValueList();
		if (valueStoreList != null) {
			Control valueCtrl = multiDynamic.getValueCtrl();
			int len = valueStoreList.size();
			for (int i = 0; i < len; i++) {
				valueCtrl.setValueStore(valueStoreList.get(i).getValueStore());
				writer.writeBehavior(valueCtrl);
				if (multiDynamic.isContainerEditable()) {
					addPageAlias(multiDynamic.getId(), valueCtrl);
				}
			}
		}
	}

}
