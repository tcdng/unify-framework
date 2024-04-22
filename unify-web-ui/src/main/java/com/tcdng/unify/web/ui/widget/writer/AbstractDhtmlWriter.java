/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.ThemeManager;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.AbstractUplComponentWriter;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.ui.PageRequestContextUtil;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.util.WebUtils;
import com.tcdng.unify.web.ui.util.WriterUtils;
import com.tcdng.unify.web.ui.widget.PageAction;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Abstract base class for DHTML writers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDhtmlWriter extends AbstractUplComponentWriter {

	@Configurable
	private ThemeManager themeManager;

	@Configurable
	private PageManager pageManager;

	/**
	 * Writes tag attributes id, name, class, style and title.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose attributes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
		writeTagAttributesUsingStyleClass(writer, widget, widget.getStyleClass());
	}

	/**
	 * Writes tag attributes id, name, class, style and title with leading extra
	 * style class
	 * 
	 * @param writer          the writer to use to write
	 * @param widget          the widget whose attributes to write
	 * @param extraStyleClass the extra style class
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagAttributesWithLeadingExtraStyleClass(ResponseWriter writer, Widget widget,
			String extraStyleClass) throws UnifyException {
		writeTagAttributesUsingStyleClass(writer, widget, extraStyleClass + " " + widget.getStyleClass());
	}

	/**
	 * Writes tag attributes id, name, class, style and title with trailing extra
	 * style class
	 * 
	 * @param writer          the writer to use to write
	 * @param widget          the widget whose attributes to write
	 * @param extraStyleClass the extra style class
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagAttributesWithTrailingExtraStyleClass(ResponseWriter writer, Widget widget,
			String extraStyleClass) throws UnifyException {
		writeTagAttributesUsingStyleClass(writer, widget, widget.getStyleClass() + " " + extraStyleClass);
	}

	/**
	 * Writes tag attributes id, name, class, style and title.
	 * 
	 * @param writer     the writer to use to write
	 * @param widget     the widget whose attributes to write
	 * @param styleClass the style class to use
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagAttributesUsingStyleClass(ResponseWriter writer, Widget widget, String styleClass)
			throws UnifyException {
		writer.write(" id=\"").write(widget.getId()).write("\"");

		String groupId = widget.getGroupId();
		if (groupId != null) {
			writer.write(" name=\"").write(groupId).write("\"");
		}

		writer.write(" class=\"").write(styleClass);
		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");

		String style = widget.getStyle();
		if (style != null) {
			writer.write(" style=\"").write(style).write("\"");
		}

		String title = widget.getHint();
		if (title != null) {
			writer.write(" title=\"").write(title).write("\"");
		}

		if (widget.isSupportDisabled()) {
			if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
				writer.write(" disabled");
			}
		}

		if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
			writer.write(" readonly");
		}
	}

	/**
	 * Writes tag identification attributes id and name.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose attributes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagIdentificationAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
		writer.write(" id=\"").write(widget.getId()).write("\"");

		String groupId = widget.getGroupId();
		if (groupId != null) {
			writer.write(" name=\"").write(groupId).write("\"");
		}
	}

	/**
	 * Writes tag visual attributes class, style and title.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose attributes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
		writeTagVisualAttributesUsingStyleClass(writer, widget, widget.getStyleClass());
	}

	/**
	 * Writes tag visual attributes class, style and title with leading extra style
	 * class.
	 * 
	 * @param writer          the writer to use to write
	 * @param widget          the widget whose attributes to write
	 * @param extraStyleClass the extra style class
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesWithLeadingExtraStyleClass(ResponseWriter writer, Widget widget,
			String extraStyleClass) throws UnifyException {
		writeTagVisualAttributesUsingStyleClass(writer, widget, extraStyleClass + " " + widget.getStyleClass());
	}

	/**
	 * Writes tag visual attributes class, style and title with trailing extra style
	 * class.
	 * 
	 * @param writer          the writer to use to write
	 * @param widget          the widget whose attributes to write
	 * @param extraStyleClass the extra style class
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesWithTrailingExtraStyleClass(ResponseWriter writer, Widget widget,
			String extraStyleClass) throws UnifyException {
		writeTagVisualAttributesUsingStyleClass(writer, widget, widget.getStyleClass() + " " + extraStyleClass);
	}

	/**
	 * Writes tag visual attributes class, style and title.
	 * 
	 * @param writer     the writer to use to write
	 * @param widget     the widget whose attributes to write
	 * @param styleClass the style class to use
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesUsingStyleClass(ResponseWriter writer, Widget widget,
			String styleClass) throws UnifyException {
		writer.write(" class=\"").write(styleClass);
		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");

		String style = widget.getStyle();
		if (style != null) {
			writer.write(" style=\"").write(style).write("\"");
		}

		String title = widget.getHint();
		if (title != null) {
			writer.write(" title=\"").write(title).write("\"");
		}

		if (widget.isSupportDisabled()) {
			if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
				writer.write(" disabled");
			}
		}

		if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
			writer.write(" readonly");
		}
	}

	/**
	 * Writes tag visual attributes class, style and title with leading extra style
	 * class.
	 * 
	 * @param writer     the writer to use to write
	 * @param widget     the widget whose attributes to write
	 * @param extraStyle the extra style
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesWithLeadingExtraStyle(ResponseWriter writer, Widget widget,
			String extraStyle) throws UnifyException {
		String style = !StringUtils.isBlank(widget.getStyle()) ? extraStyle + widget.getStyle() : extraStyle;
		writeTagVisualAttributesUsingStyle(writer, widget, style);
	}

	/**
	 * Writes tag visual attributes class, style and title with trailing extra style
	 * class.
	 * 
	 * @param writer     the writer to use to write
	 * @param widget     the widget whose attributes to write
	 * @param extraStyle the extra style
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesWithTrailingExtraStyle(ResponseWriter writer, Widget widget,
			String extraStyle) throws UnifyException {
		String style = !StringUtils.isBlank(widget.getStyle()) ? widget.getStyle() + extraStyle : extraStyle;
		writeTagVisualAttributesUsingStyle(writer, widget, style);
	}

	/**
	 * Writes tag visual attributes class, style and title.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose attributes to write
	 * @param style  the style to use
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagVisualAttributesUsingStyle(ResponseWriter writer, Widget widget, String style)
			throws UnifyException {
		writer.write(" class=\"").write(widget.getStyleClass());
		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");

		if (style != null) {
			writer.write(" style=\"").write(style).write("\"");
		}

		String title = widget.getHint();
		if (title != null) {
			writer.write(" title=\"").write(title).write("\"");
		}

		if (widget.isSupportDisabled()) {
			if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
				writer.write(" disabled");
			}
		}

		if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
			writer.write(" readonly");
		}
	}

	/**
	 * Writes tag edit attributes
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose attributes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagEditAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
		if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
			writer.write(" disabled");
		}

		if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
			writer.write(" readonly");
		}
	}

	/**
	 * Writes tag id attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose id to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagId(ResponseWriter writer, Widget widget) throws UnifyException {
		writer.write(" id=\"").write(widget.getId()).write("\"");
	}

	/**
	 * Writes tag id attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param id     the id to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagId(ResponseWriter writer, String id) throws UnifyException {
		writer.write(" id=\"").write(id).write("\"");
	}

	/**
	 * Writes tag name attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose name to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagName(ResponseWriter writer, Widget widget) throws UnifyException {
		String name = widget.getGroupId();
		if (StringUtils.isNotBlank(name)) {
			writer.write(" name=\"").write(name).write("\"");
		}
	}

	/**
	 * Writes tag name attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param name   the name to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagName(ResponseWriter writer, String name) throws UnifyException {
		writer.write(" name=\"").write(name).write("\"");
	}

	/**
	 * Writes tag class attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose style class to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyleClass(ResponseWriter writer, Widget widget) throws UnifyException {
		writer.write(" class=\"").write(widget.getStyleClass());
		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");
	}

	/**
	 * Writes tag class attribute with extra classes.
	 * 
	 * @param writer       the writer to use to write
	 * @param widget       the widget whose style class to write
	 * @param extraLeading indicates extra classes should be leading
	 * @param extraClasses the extra classes to write
	 * @throws UnifyException if an error occurs
	 */
	@Deprecated
	protected final void writeTagStyleClass(ResponseWriter writer, Widget widget, boolean extraLeading,
			String... extraClasses) throws UnifyException {
		writer.write(" class=\"");
		if (extraLeading) {
			for (String extraClass : extraClasses) {
				if (extraClass != null) {
					writer.write(extraClass).write(" ");
				}
			}
			writer.write(widget.getStyleClass());
		} else {
			writer.write(widget.getStyleClass());
			for (String extraClass : extraClasses) {
				if (extraClass != null) {
					writer.write(" ").write(extraClass);
				}
			}
		}

		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");
	}

	/**
	 * Writes tag class attribute with leading extra classes.
	 * 
	 * @param writer       the writer to use to write
	 * @param widget       the widget whose style class to write
	 * @param extraClasses the extra classes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyleClassWithLeadingExtraStyleClasses(ResponseWriter writer, Widget widget,
			String... extraClasses) throws UnifyException {
		writer.write(" class=\"");
		for (String extraClass : extraClasses) {
			if (extraClass != null) {
				writer.write(extraClass).write(" ");
			}
		}
		writer.write(widget.getStyleClass());

		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");
	}

	/**
	 * Writes tag class attribute with trailing extra classes.
	 * 
	 * @param writer       the writer to use to write
	 * @param widget       the widget whose style class to write
	 * @param extraClasses the extra classes to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyleClassWithTrailingExtraStyleClasses(ResponseWriter writer, Widget widget,
			String... extraClasses) throws UnifyException {
		writer.write(" class=\"");
		writer.write(widget.getStyleClass());
		for (String extraClass : extraClasses) {
			if (extraClass != null) {
				writer.write(" ").write(extraClass);
			}
		}

		String valStyleClass = widget.getStyleClassValue();
		if (valStyleClass != null) {
			writer.write(" ").write(valStyleClass);
		}
		writer.write("\"");
	}

	/**
	 * Writes tag class attribute.
	 * 
	 * @param writer     the writer to use to write
	 * @param styleClass the styleClass to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyleClass(ResponseWriter writer, String styleClass) throws UnifyException {
		writer.write(" class=\"").write(styleClass).write("\"");
	}

	/**
	 * Writes tag style attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose style to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyle(ResponseWriter writer, Widget widget) throws UnifyException {
		if (StringUtils.isNotBlank(widget.getStyle())) {
			writer.write(" style=\"").write(widget.getStyle()).write("\"");
		}
	}

	/**
	 * Writes tag style attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param style  the style to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyle(ResponseWriter writer, String style) throws UnifyException {
		if (StringUtils.isNotBlank(style)) {
			writer.write(" style=\"").write(style).write("\"");
		}
	}

	/**
	 * Writes combined tag style attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose style to write
	 * @param style  the additional style to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyle(ResponseWriter writer, Widget widget, String style) throws UnifyException {
		if (StringUtils.isNotBlank(style) || StringUtils.isNotBlank(widget.getStyle())) {
			writer.write(" style=\"");
			if (StringUtils.isNotBlank(widget.getStyle())) {
				writer.write(widget.getStyle());
			}

			if (StringUtils.isNotBlank(style)) {
				writer.write(style);
			}
			writer.write("\"");
		}
	}

	/**
	 * Writes tag title attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose title to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagTitle(ResponseWriter writer, Widget widget) throws UnifyException {
		if (StringUtils.isNotBlank(widget.getHint())) {
			writer.write(" title=\"").write(widget.getHint()).write("\"");
		}
	}

	/**
	 * Writes tag title attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param title  the title to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagTitle(ResponseWriter writer, String title) throws UnifyException {
		if (StringUtils.isNotBlank(title)) {
			writer.write(" title=\"").write(title).write("\"");
		}
	}

	/**
	 * Writes tag value attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param widget the widget whose value to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagValue(ResponseWriter writer, Widget widget) throws UnifyException {
		String value = widget.getStringValue();
		if (value != null) {
			writer.write(" value=\"").writeWithHtmlEscape(value).write("\"");
		}
	}

	/**
	 * Writes tag value attribute.
	 * 
	 * @param writer the writer to use to write
	 * @param value  the value to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagValue(ResponseWriter writer, Object value) throws UnifyException {
		if (value != null) {
			writer.write(" value=\"").writeWithHtmlEscape(String.valueOf(value)).write("\"");
		}
	}

	/**
	 * Writes tag readonly attribute
	 * 
	 * @param writer the writer to use
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagReadOnly(ResponseWriter writer) throws UnifyException {
		writer.write(" readonly");
	}

	/**
	 * Writes tag readonly attribute
	 * 
	 * @param writer the writer to use
	 * @param widget the widget
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagReadOnly(ResponseWriter writer, Widget widget) throws UnifyException {
		if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
			writer.write(" readonly");
		}
	}

	/**
	 * Writes tag disabled attribute
	 * 
	 * @param writer the writer to use
	 * @param widget the widget
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagDisabled(ResponseWriter writer, Widget widget) throws UnifyException {
		if (widget.isContainerDisabled()) {
			writer.write(" disabled");
		}
	}

	/**
	 * Writes tag id attribute.
	 * 
	 * @param sb the sting builder to use to write
	 * @param id the id to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagId(StringBuilder sb, String id) throws UnifyException {
		sb.append(" id=\"").append(id).append("\"");
	}

	/**
	 * Writes tag name attribute.
	 * 
	 * @param sb   the sting builder to use to write
	 * @param name the name to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagName(StringBuilder sb, String name) throws UnifyException {
		sb.append(" name=\"").append(name).append("\"");
	}

	/**
	 * Writes tag class attribute.
	 * 
	 * @param sb         the sting builder to use to write
	 * @param styleClass the styleClass to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyleClass(StringBuilder sb, String styleClass) throws UnifyException {
		sb.append(" class=\"").append(styleClass).append("\"");
	}

	/**
	 * Writes tag style attribute.
	 * 
	 * @param sb    the sting builder to use to write
	 * @param style the style to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagStyle(StringBuilder sb, String style) throws UnifyException {
		sb.append(" style=\"").append(style).append("\"");
	}

	/**
	 * Writes tag title attribute.
	 * 
	 * @param sb    the sting builder to use to write
	 * @param title the title to write
	 * @throws UnifyException if an error occurs
	 */
	protected final void writeTagTitle(StringBuilder sb, String title) throws UnifyException {
		sb.append(" title=\"").append(title).append("\"");
	}

	/**
	 * Writes file image HTML element.
	 * 
	 * @param writer     the writer to use
	 * @param src        the image source file
	 * @param id         the image id
	 * @param styleClass the style class
	 * @param style      the style
	 * @throws UnifyException if an error occurs
	 */
	protected void writeFileImageHtmlElement(ResponseWriter writer, String src, String id, String styleClass,
			String style) throws UnifyException {
		writer.write("<img");
		if (StringUtils.isNotBlank(id)) {
			writer.write(" id=\"").write(id).write("\"");
		}

		if (StringUtils.isNotBlank(styleClass)) {
			writer.write(" class=\"").write(styleClass).write("\"");
		}

		if (StringUtils.isNotBlank(style)) {
			writer.write(" style=\"").write(style).write("\"");
		}

		writer.write(" src=\"");
		writer.writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), themeManager.expandThemeTag(src));
		writer.write("\">");
	}

	protected boolean writeCaption(ResponseWriter writer, Widget widget) throws UnifyException {
		String caption = widget.getCaption();
		if (caption != null) {
			writer.writeWithHtmlEscape(caption);
			return true;
		}

		return false;
	}

	protected boolean writeAttributeWithEscape(ResponseWriter writer, Widget widget, String attribute)
			throws UnifyException {
		String cattributeValue = widget.getUplAttribute(String.class, attribute);
		if (cattributeValue != null) {
			writer.writeWithHtmlEscape(cattributeValue);
			return true;
		}

		return false;
	}

	protected void writePostToPathJs(ResponseWriter writer, String path) throws UnifyException {
		String function = WriterUtils.getActionJSFunction("posttopath");
		writer.beginFunction(function);
		writer.write("\"uPath\":\"");
		writer.writeContextURL(path);
		writer.write("\"");
		writer.endFunction();
	}

	protected void writeShortcutHandlerJs(ResponseWriter writer, String pageControllerName, String id, String cmdTag,
			PageAction pageAction) throws UnifyException {
		writer.beginFunction("ux.setShortcut");
		String function = WriterUtils.getActionJSFunction(pageAction.getAction().toLowerCase());
		writeActionParamsJS(writer, null, function, id, cmdTag, pageAction, null, null, null);
		writer.endFunction();
	}

	protected void writeEventJs(ResponseWriter writer, String event, String action, String pageName, String cmdTag,
			String... targetPageNames) throws UnifyException {
		writer.beginFunction("ux.setOnEvent");
		event = WriterUtils.getEventJS(event.toLowerCase());
		String function = WriterUtils.getActionJSFunction(action);
		writeActionParamsJS(writer, event, function, pageName, cmdTag, null, targetPageNames, null, null);
		writer.endFunction();
	}

	protected void writePathEventHandlerJS(ResponseWriter writer, String id, String cmdTag, String eventType,
			String action, String path) throws UnifyException {
		writer.beginFunction("ux.setOnEvent");
		String event = WriterUtils.getEventJS(eventType.toLowerCase());
		String function = WriterUtils.getActionJSFunction(action.toLowerCase());
		writeActionParamsJS(writer, event, function, id, cmdTag, null, null, null, path);
		writer.endFunction();
	}

	protected void writeOpenPopupJS(ResponseWriter writer, Widget widget, String event, String pageName, String cmdTag,
			String frameId, String popupId, long stayOpenForMillSec, String onShowAction, String onShowParamObject,
			String onHideAction, String onHideParamObject) throws UnifyException {
		StringBuilder psb = new StringBuilder();
		psb.append("{\"popupId\":\"").append(popupId).append("\"");
		if (frameId != null) {
			psb.append(",\"frameId\":\"").append(frameId).append("\"");
		}

		psb.append(",\"stayOpenForMillSec\":").append(stayOpenForMillSec);
		if (onShowAction != null) {
			psb.append(",\"showHandler\":\"").append(WriterUtils.getActionJSAlias(onShowAction)).append("\"");
			if (onShowParamObject != null) {
				psb.append(",\"showParam\":").append(onShowParamObject);
			}
		}
		if (onHideAction != null) {
			psb.append(",\"hideHandler\":\"").append(WriterUtils.getActionJSAlias(onHideAction)).append("\"");
			if (onHideParamObject != null) {
				psb.append(",\"hideParam\":").append(onHideParamObject);
			}
		}
		psb.append("}");
		writeRefObjectEventHandlerJS(writer, pageName, cmdTag, event, "openpopup", psb.toString());
	}

	protected void writeActionParamsJS(ResponseWriter writer, String event, String function, String id, String cmdTag,
			PageAction pageAction, String[] refPageNames, String refObject, String path) throws UnifyException {
		final ControllerPathParts parts = getRequestContextUtil().getResponsePathParts();		
		final String pathId = parts.getControllerPathId();
		PageManager pageManager = getPageManager();
		if (StringUtils.isNotBlank(event)) {
			writer.write("\"uEvnt\":\"").write(event).write("\",");
		}
		if (StringUtils.isNotBlank(function)) {
			String alias = WriterUtils.getActionJSAlias(function);
			writer.write("\"uFunc\":\"").write(alias).write("\",");
		}
		writer.write("\"uId\":\"").write(id).write("\"");

		if (getRequestContextUtil().isRemoteViewer()) {
			writer.write(",\"uViewer\":\"").write(getRequestContextUtil().getRemoteViewer()).write("\"");
		}

		if (pageAction != null) {
			if ("ux.disable".equals(function)) {
				writer.write(",\"uFire\":true");
			}

			if (pageAction.isUplAttribute("validations")) {
				if (pageAction.getUplAttribute(Object.class, "validations") != null) {
					writer.write(",\"uValidateAct\":\"").write(pageAction.getId()).write("\"");
				}
			}

			if (pageAction.isUplAttribute("shortcut")) {
				String shortcut = pageAction.getUplAttribute(String.class, "shortcut");
				if (shortcut != null) {
					shortcut = WebUtils.encodeShortcut(shortcut);
					writer.write(",\"uShortcut\":\"").write(shortcut).write("\"");
				}
			}

			if (pageAction.isUplAttribute("command")) {
				String cmd = pageAction.getUplAttribute(String.class, "command");
				if (cmd != null) {
					writer.write(",\"uCmdURL\":\"");
					writer.writeCommandURL(pathId);
					writer.write('"');
					writer.write(",\"uTrgCmd\":\"").write(cmd).write("\"");
					if (!StringUtils.isBlank(cmdTag)) {
						writer.write(",\"uCmdTag\":\"").write(cmdTag).write("\"");
					}
				}

				String targetCmdPgNm = null;
				String commandTarget = pageAction.getUplAttribute(String.class, "target");
				if (commandTarget != null) {
					if ("PARENT".equals(commandTarget)) {
						targetCmdPgNm = pageManager.getPageName(pageAction.getParentLongName().substring(0,
								pageAction.getParentLongName().lastIndexOf('.')));
					} else {
						targetCmdPgNm = pageManager.getPageName(commandTarget);
					}
				}

				if (targetCmdPgNm == null) {
					targetCmdPgNm = pageManager.getPageName(pageAction.getParentLongName());
				}

				writer.write(",\"uTrgPnl\":\"").write(targetCmdPgNm).write("\"");

				UplElementReferences uer = pageAction.getUplAttribute(UplElementReferences.class, "refresh");
				if (uer != null) {
					writer.write(",\"uRefreshPnls\":").writeJsonArray(pageManager.getPageNames(uer.getLongNames()));
				} else {
					String targetRefreshPgNm = getRequestContextUtil().getDynamicPanelParentPageName();
					if (targetRefreshPgNm == null) {
						targetRefreshPgNm = pageManager.getPageName(pageAction.getParentLongName());
					}

					writer.write(",\"uRefreshPnls\":[\"").write(targetRefreshPgNm).write("\"]");
				}
			}

			if (pageAction.getUplAttribute(boolean.class, "pushSrc")) {
				writer.write(",\"uPushSrc\":true");
			}

			List<String> componentList = getActionRefComponentList(writer, id, pageAction);
			writer.write(",\"uRef\":").writeJsonArray(componentList);

			List<String> valueComponentList = pageManager.getValueReferences(pageAction.getId());
			writer.write(",\"uVRef\":").writeJsonArray(valueComponentList);

			if (pageAction.isUplAttribute("valueList")) {
				String[] valueList = pageAction.getUplAttribute(String[].class, "valueList");
				if (valueList != null) {
					writer.write(",\"uVal\":").writeJsonArray(valueList);
				}
			}

			if (pageAction.isUplAttribute("path")) {
				String actionPath = pageAction.getUplAttribute(String.class, "path");
				if (actionPath != null) {
					if (TokenUtils.isNameTag(actionPath) || TokenUtils.isPathTag(actionPath)) {
						actionPath = pathId + TokenUtils.extractTokenValue(actionPath);
					} else if (TokenUtils.isRequestAttributeTag(actionPath)) {
						actionPath = pathId + getRequestAttribute(TokenUtils.extractTokenValue(actionPath));
					} else if (TokenUtils.isQuickReferenceTag(actionPath)) {
						actionPath = (String) ((ValueStore) getRequestContext().getQuickReference())
								.retrieve(TokenUtils.extractTokenValue(actionPath));
					}

					if (!pageAction.isStrictPath() && !actionPath.startsWith(pathId)) {
						int index = actionPath.lastIndexOf('/');
						actionPath = pathId + actionPath.substring(index);
					}

					writer.write(",\"uURL\":\"");
					writer.writeContextURL(actionPath);
					writer.write('"');
				}
			}

			if ("ux.download".equals(function)) {
				writer.write(",\"uURL\":\"");
				writer.writeContextResourceURL("/resource/downloadpath", MimeType.APPLICATION_OCTETSTREAM.template(),
						pageAction.getUplAttribute(String.class, "resource"), null, true, false);
				writer.write('"');
			}

			if (pageAction.isUplAttribute("debounce")) {
				if (pageAction.getUplAttribute(boolean.class, "debounce")) {
					writer.write(",\"uIsDebounce\":true");
				}
			}

			if (pageAction.isUplAttribute("confirm")) {
				String confirm = pageAction.getUplAttribute(String.class, "confirm");
				if (StringUtils.isNotBlank(confirm)) {
					writer.write(",\"uConf\":");
					writeStringParameter(writer, confirm);
					writer.write(",\"uIconIndex\":");
					writer.write(pageAction.getUplAttribute(int.class, "iconIndex"));
					writer.write(",\"uConfURL\":\"");
					writer.writeContextURL(pathId, "/confirm");
					writer.write('"');
				}
			}
		} else if (refPageNames != null) {
			writer.write(",\"uRef\":").writeJsonArray(refPageNames);
		} else if (StringUtils.isNotBlank(refObject)) {
			writer.write(",\"uRef\":").write(refObject);
		} else if (path != null) {
			if (TokenUtils.isNameTag(path) || TokenUtils.isPathTag(path)) {
				path = pathId + TokenUtils.extractTokenValue(path);
			} else if (TokenUtils.isRequestAttributeTag(path)) {
				path = pathId + getRequestAttribute(TokenUtils.extractTokenValue(path));
			}

			writer.write(",\"uURL\":\"");
			writer.writeContextURL(path);
			writer.write('"');
		}
	}

	protected void keepPostCommandRefs(ResponseWriter writer, String id, PageAction pageAction) throws UnifyException {
		if (pageAction.isPostCommand()) {
			List<String> componentList = getActionRefComponentList(writer, id, pageAction);
			writer.keepPostCommandRefs(componentList);
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getActionRefComponentList(ResponseWriter writer, String id, PageAction pageAction)
			throws UnifyException {
		List<String> resultList = new ArrayList<String>();
		PageManager pageManager = getPageManager();
		List<String> componentList = pageManager.getExpandedReferences(pageAction.getId());
		resultList.addAll(componentList);
		int dataIndex = writer.getDataIndex();
		if (dataIndex >= 0 && !componentList.isEmpty()) {
			final String dataSuffix = "d" + dataIndex;
			resultList.add(id);
			for (String component : componentList) {
				resultList.add(component + dataSuffix);
			}
		}

		if (pageAction.isUplAttribute("pushComponents")) {
			String components = pageAction.getUplAttribute(String.class, "pushComponents");
			if (components != null) {
				if (TokenUtils.isRequestAttributeTag(components)) {
					List<String> pushList = (List<String>) getRequestAttribute(
							TokenUtils.extractTokenValue(components));
					if (!DataUtils.isBlank(pushList)) {
						resultList.addAll(pushList);
					}
				}
			}
		}

		return DataUtils.removeDuplicatesUnordered(resultList);
	}

	protected PageRequestContextUtil getRequestContextUtil() throws UnifyException {
		return (PageRequestContextUtil) getComponent(WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL);
	}

	protected PageManager getPageManager() throws UnifyException {
		return pageManager;
	}

	protected void writeStringParameter(ResponseWriter writer, String string) {
		if (string != null && !string.trim().isEmpty()) {
			writer.write("\"").write(string).write("\"");
		} else {
			writer.write("null");
		}
	}

	private void writeRefObjectEventHandlerJS(ResponseWriter writer, String pageName, String cmdTag, String eventType,
			String action, String refObject) throws UnifyException {
		writer.beginFunction("ux.setOnEvent");
		String event = WriterUtils.getEventJS(eventType.toLowerCase());
		String function = WriterUtils.getActionJSFunction(action.toLowerCase());
		writeActionParamsJS(writer, event, function, pageName, cmdTag, null, null, refObject, null);
		writer.endFunction();
	}

}
