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
package com.tcdng.unify.web.ui.writer.container;

import java.util.List;

import com.tcdng.unify.core.PrivilegeSettings;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.RequestContextUtil;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Section;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.container.Form;
import com.tcdng.unify.web.ui.container.Form.FormSection;
import com.tcdng.unify.web.ui.writer.AbstractContainerWriter;

/**
 * Form writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Form.class)
@Component("form-writer")
public class FormWriter extends AbstractContainerWriter {

	@Override
	protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
		Form form = (Form) container;
		form.cascadeValueStore();
		String groupId = null;
		if (form.isContainerEditable()) {
			groupId = form.getDataGroupId();
		}

		boolean isFormDisabled = form.isDisabled();
		boolean isFormEditable = form.isEditable();
		for (FormSection formSection : form.getSections()) {
			PrivilegeSettings ps = getPrivilegeSettings(formSection.getPrivilege());
			if (ps.isVisible() && formSection.isVisible()) {
				form.setDisabled(isFormDisabled || ps.isDisabled() || formSection.isDisabled());
				form.setEditable(isFormEditable && ps.isEditable() && formSection.isEditable());

				if (formSection.isBinding()) {
					if (formSection.isBindingValueList()) {
						for (ValueStore valueStore : formSection.getValueStoreList()) {
							writeSectionStructureAndContent(writer, form, formSection, valueStore, groupId);
						}
					} else {
						writeSectionStructureAndContent(writer, form, formSection, formSection.getValueStore(), null);
					}
				} else {
					writeSectionStructureAndContent(writer, form, formSection, null, null);
				}

				form.setEditable(isFormEditable);
				form.setDisabled(isFormDisabled);
			}
		}

		if (form.isContainerEditable()) {
			writeHiddenPush(writer, form.getDataGroupId(), PushType.GROUP);
		}
	}

	@Override
	protected void writeContainedWidgetsBehavior(ResponseWriter writer, Container container) throws UnifyException {
		Form form = (Form) container;
		for (FormSection formSection : form.getSections()) {
			PrivilegeSettings ps = getPrivilegeSettings(formSection.getPrivilege());
			if (ps.isVisible() && formSection.isVisible()) {
				if (formSection.isBinding()) {
					if (formSection.isBindingValueList()) {
						for (ValueStore valueStore : formSection.getValueStoreList()) {
							writeSectionBehavior(writer, form, formSection, valueStore);
						}
					} else {
						writeSectionBehavior(writer, form, formSection, formSection.getValueStore());
					}
				} else {
					writeSectionBehavior(writer, form, formSection, null);
				}
			}
		}
	}

	private void writeSectionBehavior(ResponseWriter writer, Form form, FormSection formSection, ValueStore valueStore)
			throws UnifyException {
		for (String longName : formSection.getSection().getReferences()) {
			Widget widget = form.getWidgetByLongName(longName);
			if (widget.isVisible() || widget.isHidden()) {
				if (valueStore != null) {
					widget.setValueStore(valueStore);
				}

				writer.writeBehaviour(widget);
			}
		}
	}

	private void writeSectionStructureAndContent(ResponseWriter writer, Form form, FormSection formSection,
			ValueStore valueStore, String groupId) throws UnifyException {
		Section section = formSection.getSection();
		if (section.isHidden()) {
			for (String longName : section.getReferences()) {
				Widget widget = form.getWidgetByLongName(longName);
				if (widget.isHidden()) {
					if (valueStore != null) {
						widget.setValueStore(valueStore);
					}

					if (groupId != null) {
						widget.setGroupId(groupId);
					}
					writer.writeStructureAndContent(widget);
				}
			}
			return;
		}

		String captionSuffix = form.getUplAttribute(String.class, "captionSuffix");
		String caption = section.getCaption();
		if (caption != null) {
			writer.write("<div><span class=\"secCaption\">");
			writer.writeWithHtmlEscape(caption);
			writer.write("</span></div>");
			writer.write("<div><table class=\"secCapBase\" style=\"width:100%;\"><tr>");
		} else {
			writer.write("<div><table class=\"sec\" style=\"width:100%;\"><tr>");
		}

		int columns = form.getUplAttribute(int.class, "columns");
		if (columns < 1) {
			columns = 1;
		}

		List<String> refList = section.getReferences();
		int itemCount = refList.size();
		int rows = itemCount / columns;
		if (itemCount % columns > 0) {
			rows++;
		}

		RequestContextUtil requestContextUtil = getRequestContextUtil();
		int columnWidth = 100 / columns;
		for (int i = 0; i < itemCount;) {
			writer.write("<td class=\"secColumn\" style=\"width:");
			writer.write(columnWidth);
			writer.write("%\"><table style:\"width:100%;\">");
			int row = 0;
			while (row < rows && i < itemCount) {
				Widget widget = form.getWidgetByLongName(refList.get(i));
				if (valueStore != null) {
					widget.setValueStore(valueStore);
				}

				if (groupId != null) {
					widget.setGroupId(groupId);
				}

				if (widget.isVisible()) {
					writer.write("<tr>");
					caption = null;
					if (widget.isLayoutCaption()) {
						caption = widget.getUplAttribute(String.class, "caption");
					}

					writer.write("<td class=\"secLabel\">");
					if (caption != null) {
						writer.writeWithHtmlEscape(caption);
						if (captionSuffix != null) {
							writer.write(captionSuffix);
						}
					}
					writer.write("</td>");

					writer.write("<td class=\"secInputReq\">");
					if (widget instanceof Control) {
						if (((Control) widget).getRequired().isTrue()) {
							writer.write("<span>").write(form.getUplAttribute(String.class, "requiredSymbol"))
									.write("</span>");
						}

						// Add to save list
						requestContextUtil.addOnSaveContentWidget(widget.getId());
					}
					writer.write("</td>");

					writer.write("<td class=\"secInput\"><div>");
					writer.writeStructureAndContent(widget);
					writer.write("</div>");
					if (widget instanceof Control) {
						writer.write("<div><span id=\"").write(((Control) widget).getNotificationId())
								.write("\" class=\"secInputErr\"></span>");
						writer.write("</div>");
					}
					writer.write("</td>");

					writer.write("</tr>");
					row++;
				} else if (widget.isHidden()) {
					writer.writeStructureAndContent(widget);
				}
				i++;
			}
			writer.write("</table></td>");
		}
		writer.write("</tr></table></div>");
	}
}
