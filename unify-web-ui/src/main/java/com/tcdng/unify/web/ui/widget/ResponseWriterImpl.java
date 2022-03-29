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
package com.tcdng.unify.web.ui.widget;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.json.JsonUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ThemeManager;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.data.WebStringWriter;
import com.tcdng.unify.web.ui.PageRequestContextUtil;
import com.tcdng.unify.web.ui.UnifyWebUIErrorConstants;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;
import com.tcdng.unify.web.ui.util.UrlUtils;
import com.tcdng.unify.web.ui.widget.writer.BehaviorWriter;
import com.tcdng.unify.web.ui.widget.writer.DocumentLayoutWriter;
import com.tcdng.unify.web.ui.widget.writer.LayoutWriter;
import com.tcdng.unify.web.ui.widget.writer.PanelWriter;
import com.tcdng.unify.web.ui.widget.writer.WidgetWriter;

/**
 * Default implementation of a response writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
@Component(WebUIApplicationComponents.APPLICATION_RESPONSEWRITER)
public class ResponseWriterImpl extends AbstractUnifyComponent implements ResponseWriter {

	@Configurable
	private ThemeManager themeManager;

	@Configurable
	private PageRequestContextUtil pageRequestContextUtil;

	@Configurable("8192")
	private int initialBufferCapacity;

	private WebStringWriter buf;

	private List<WebStringWriter> secordaryList;

	private Map<Class<? extends UplComponent>, UplComponentWriter> writers;

	private boolean tableMode;

    private boolean openFunction;

    private boolean paramAppendSym;
	
	public ResponseWriterImpl() {
        secordaryList = new ArrayList<WebStringWriter>();
	}
	
	public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setPageRequestContextUtil(PageRequestContextUtil pageRequestContextUtil) {
        this.pageRequestContextUtil = pageRequestContextUtil;
    }

    public void setInitialBufferCapacity(int initialBufferCapacity) {
        this.initialBufferCapacity = initialBufferCapacity;
    }

    @Override
    public ResponseWriter writeResolvedApplicationMessage(String message, Object... params) throws UnifyException {
        String msg = super.resolveApplicationMessage(message, params);
        writeWithHtmlEscape(msg);
        return this;
    }

    @Override
    public ResponseWriter writeResolvedSessionMessage(String message, Object... params) throws UnifyException {
        String msg = super.resolveSessionMessage(message, params);
        writeWithHtmlEscape(msg);
        return this;
    }

    @Override
	public ResponseWriter writeStructureAndContent(Widget component) throws UnifyException {
		((WidgetWriter) getWriter(component)).writeStructureAndContent(this, component);
		return this;
	}

	@Override
	public ResponseWriter writeStructureAndContent(Widget component, String id) throws UnifyException {
		((WidgetWriter) getWriter(component)).writeStructureAndContent(this, component, id);
		return this;
	}

	@Override
	public ResponseWriter writeStructureAndContent(DocumentLayout documentLayout, Document document)
			throws UnifyException {
		((DocumentLayoutWriter) getWriter(documentLayout)).writeStructureAndContent(this, documentLayout, document);
		return this;
	}

	@Override
	public ResponseWriter writeStructureAndContent(Layout layout, Container container) throws UnifyException {
		((LayoutWriter) getWriter(layout)).writeStructureAndContent(this, layout, container);
		return this;
	}

	@Override
	public ResponseWriter writeInnerStructureAndContent(Panel panel) throws UnifyException {
		((PanelWriter) getWriter(panel)).writeInnerStructureAndContent(this, panel);
		return this;
	}

	@Override
	public ResponseWriter writeBehavior(DocumentLayout documentLayout, Document document) throws UnifyException {
		((DocumentLayoutWriter) getWriter(documentLayout)).writeBehaviour(this, documentLayout, document);
		return this;
	}

	@Override
	public ResponseWriter writeBehavior(Widget component) throws UnifyException {
		((WidgetWriter) getWriter(component)).writeBehavior(this, component);
		return this;
	}

	@Override
	public ResponseWriter writeBehavior(Widget component, String id) throws UnifyException {
		((WidgetWriter) getWriter(component)).writeBehavior(this, component, id);
		return this;
	}

	@Override
    public ResponseWriter writeBehavior(Behavior behavior, String id, String cmdTag) throws UnifyException {
        ((BehaviorWriter) getWriter(behavior)).writeBehavior(this, behavior, id, cmdTag);
        return this;
    }

	@Override
	public ResponseWriter write(Object object) {
		buf.append(object);
		return this;
	}

	@Override
	public ResponseWriter writeNotNull(Object object) {
		if (object != null) {
			buf.append(object);
		}
		return this;
	}

	@Override
	public ResponseWriter writeHtmlFixedSpace() {
		buf.append("&nbsp;");
		return this;
	}

	@Override
	public ResponseWriter writeWithHtmlEscape(String string) {
		buf.appendHtmlEscaped(string);
		return this;
	}

	@Override
	public ResponseWriter writeJsonArray(String... stringArr) throws UnifyException {
		return writeJsonArray((Object[]) stringArr, true);
	}

	@Override
	public ResponseWriter writeJsonArray(Integer... intArr) throws UnifyException {
		return writeJsonArray((Object[]) intArr, false);
	}

	@Override
	public ResponseWriter writeJsonArray(Long... longArr) throws UnifyException {
		return writeJsonArray((Object[]) longArr, false);
	}

	@Override
	public ResponseWriter writeJsonArray(BigDecimal... bigArr) throws UnifyException {
		return writeJsonArray((Object[]) bigArr, false);
	}

	@Override
	public ResponseWriter writeJsonArray(Double... doubleArr) throws UnifyException {
		return writeJsonArray((Object[]) doubleArr, false);
	}

	@Override
	public ResponseWriter writeJsonArray(Boolean... boolArr) throws UnifyException {
		return writeJsonArray((Object[]) boolArr, false);
	}

	@Override
	public ResponseWriter writeJsonArray(Collection<?> col) throws UnifyException {
		buf.append('[');
		if (col != null) {
			boolean appendSym = false;
			for (Object val : col) {
				if (appendSym) {
					buf.append(',');
				} else {
					appendSym = true;
				}

				if (val == null) {
					buf.append(val);
				} else {
					if (val instanceof Number || val instanceof Boolean) {
						buf.append(val);
					} else {
						writeJsonQuote(String.valueOf(val));
					}
				}
			}
		}
		buf.append(']');
		return this;
	}

    @Override
    public ResponseWriter writeParam(String paramName, Pattern[] pa) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":");
        writeJsonPatternObject(pa);
        return this;
    }
	
	@Override
	public ResponseWriter writeJsonPatternObject(Pattern[] pa) throws UnifyException {
		buf.append("[");
		boolean appendSym = false;
		for (Pattern p : pa) {
			if (appendSym) {
				buf.append(',');
			} else {
				appendSym = true;
			}
			buf.append("{\"flag\":").append(p.isFiller()).append(",\"length\":").append(p.getPattern().length())
					.append(",\"target\":");
			if (p.isFiller()) {
				writeJsonQuote(p.getPattern());
			} else {
				buf.append('"').append(p.getTarget()).append('"');
			}
			buf.append("}");
		}
		buf.append("]");
		return this;
	}

    @Override
    public ResponseWriter writeParam(String paramName, DateTimeFormat[] dateTimeFormat) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":");
        writeJsonDateTimeFormatObject(dateTimeFormat);
        return this;
    }

	@Override
	public ResponseWriter writeJsonDateTimeFormatObject(DateTimeFormat[] dateTimeFormat) throws UnifyException {
		buf.append("[");
		boolean appendSym = false;
		for (DateTimeFormat dtf : dateTimeFormat) {
			if (appendSym) {
				buf.append(',');
			} else {
				appendSym = true;
			}

			if (dtf == null) {
				buf.append("null");
			} else {
				buf.append("{\"flag\":");
				List<? extends Listable> listableList = dtf.getList();
				if (listableList != null) {
					buf.append(true).append(",\"min\":").append(0).append(",\"max\":").append(listableList.size() - 1)
							.append(",\"list\":[");
					boolean appendSym1 = false;
					for (Listable listable : listableList) {
						if (appendSym1) {
							buf.append(',');
						} else {
							appendSym1 = true;
						}
						writeJsonQuote(listable.getListKey());
					}
					buf.append(']');
				} else {
					int[] range = dtf.getRange();
					buf.append(false).append(",\"min\":").append(range[0]).append(",\"max\":").append(range[1]);
				}
				buf.append("}");
			}
		}
		buf.append("]");
		return this;
	}

	@Override
	public ResponseWriter writeJsonQuote(String string) throws UnifyException {
		buf.appendJsonQuoted(string);
		return this;
	}

	@Override
	public ResponseWriter writeJsonQuote(WebStringWriter lsw) throws UnifyException {
		buf.appendJsonQuoted(lsw);
		return this;
	}

	@Override
	public ResponseWriter writeJsonPageNameAliasesArray() throws UnifyException {
		buf.append('[');
		Map<String, Set<String>> childAliasesMap = getRequestContextUtil().getRequestPageNameAliases();
		if (childAliasesMap != null) {
			boolean appendSym = false;
			for (Map.Entry<String, Set<String>> entry : childAliasesMap.entrySet()) {
				if (appendSym) {
					buf.append(',');
				} else {
					appendSym = true;
				}
				buf.append("{\"pn\":\"").append(entry.getKey()).append("\",");
				buf.append("\"aliases\":");
				writeJsonArray(entry.getValue());
				buf.append('}');
			}
		}
		buf.append(']');
		return this;
	}

	@Override
	public ResponseWriter writeJsonPathVariable(String name, String path) throws UnifyException {
		useSecondary(128);
		writeContextURL(path);
		WebStringWriter pathLsw = discardSecondary();

		buf.append("\"").append(name).append("\":");
		buf.appendJsonQuoted(pathLsw);
		return this;
	}

	@Override
	public ResponseWriter writeJsonPanel(Panel panel, boolean innerOnly) throws UnifyException {
		useSecondary();
		if (innerOnly) {
			writeInnerStructureAndContent(panel);
		} else {
			writeStructureAndContent(panel);
		}
		WebStringWriter htmlLsw = discardSecondary();

		useSecondary();
		writeBehavior(panel);
		WebStringWriter scriptLsw = discardSecondary();

		buf.append("{\"target\":\"").append(panel.getId()).append('"');
		buf.append(",\"html\":");
		buf.appendJsonQuoted(htmlLsw);
		buf.append(",\"script\":");
		buf.appendJsonQuoted(scriptLsw);
		buf.append('}');
		return this;
	}

	@Override
	public ResponseWriter writeJsonSection(Widget widget, String sectionPageName) throws UnifyException {
		WidgetWriter widgetWriter = (WidgetWriter) getWriter(widget);
		useSecondary();
		widgetWriter.writeSectionStructureAndContent(this, widget, sectionPageName);
		WebStringWriter htmlLsw = discardSecondary();

		useSecondary();
		widgetWriter.writeSectionBehavior(this, widget, sectionPageName);
		WebStringWriter scriptLsw = discardSecondary();

		buf.append("{\"target\":\"").append(sectionPageName).append('"');
		buf.append(",\"html\":");
		buf.appendJsonQuoted(htmlLsw);
		buf.append(",\"script\":");
		buf.appendJsonQuoted(scriptLsw);
		buf.append('}');
		return this;
	}

	@Override
	public ResponseWriter writeContextURL(String path, String... pathElement) throws UnifyException {
		RequestContext requestContext = getRequestContext();
		if (pageRequestContextUtil.isRemoteViewer()) {
			buf.append(getSessionContext().getUriBase());
		}

		buf.append(requestContext.getContextPath());
		if (requestContext.isWithTenantPath()) {
			buf.append(requestContext.getTenantPath());
		}

		buf.append(path);
		for (String element : pathElement) {
			buf.append(element);
		}
		return this;
	}
	
	@Override
	public ResponseWriter writeContextURL(StringBuilder sb, String path, String... pathElement) throws UnifyException {
		RequestContext requestContext = getRequestContext();
		if (pageRequestContextUtil.isRemoteViewer()) {
			sb.append(getSessionContext().getUriBase());
		}

		sb.append(requestContext.getContextPath());
		if (requestContext.isWithTenantPath()) {
			sb.append(requestContext.getTenantPath());
		}

		sb.append(path);
		for (String element : pathElement) {
			sb.append(element);
		}
		return this;
	}

	@Override
	public ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName)
			throws UnifyException {
		writeContextResourceURL(path, contentType, resourceName, null, false, false);
		return this;
	}

	@Override
	public ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName, String scope,
			boolean attachment, boolean clearOnRead) throws UnifyException {
		writeContextURL(path);

		PageManager pageManager = getPageManager();
		buf.append('?').append(pageManager.getPageName("resourceName")).append("=")
				.append(UrlUtils.encodeURLParameter(themeManager.expandThemeTag(resourceName)));
		if (StringUtils.isNotBlank(contentType)) {
			buf.append('&').append(pageManager.getPageName("contentType")).append("=")
					.append(UrlUtils.encodeURLParameter(contentType));
		}

		if (StringUtils.isNotBlank(scope)) {
			buf.append('&').append(pageManager.getPageName("scope")).append("=")
					.append(UrlUtils.encodeURLParameter(scope));
		}

		if (attachment) {
			buf.append('&').append(pageManager.getPageName("attachment")).append("=").append(attachment);
		}

		if (clearOnRead) {
			buf.append('&').append(pageManager.getPageName("clearOnRead")).append("=").append(clearOnRead);
		}

		if (pageRequestContextUtil.isRemoteViewer()) {
			buf.append('&').append(RequestParameterConstants.REMOTE_VIEWER).append("=")
					.append(pageRequestContextUtil.getRemoteViewer());
			buf.append('&').append(RequestParameterConstants.REMOTE_SESSION_ID).append("=")
					.append(getRequestContext().getAttribute(RequestParameterConstants.REMOTE_SESSION_ID));
		}
		return this;
	}

	@Override
	public ResponseWriter writeURLParameter(String name, String value) throws UnifyException {
		buf.append('&').append(getPageManager().getPageName(name)).append("=")
				.append(UrlUtils.encodeURLParameter(value));
		return this;
	}

	@Override
	public ResponseWriter writeFileImageContextURL(String src) throws UnifyException {
		writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), src);
		return this;
	}

	@Override
	public ResponseWriter writeScopeImageContextURL(String imageName) throws UnifyException {
		writeContextResourceURL("/resource/scope", MimeType.IMAGE.template(), imageName);
		return this;
	}

	@Override
	public ResponseWriter writeCommandURL() throws UnifyException {
		writeContextURL(getRequestContextUtil().getResponsePathParts().getControllerPathId(), "/command");
		return this;
	}

	@Override
	public ResponseWriter writeCommandURL(String pageControllerName) throws UnifyException {
		if (QueryUtils.isValidStringCriteria(pageControllerName)) {
			writeContextURL(pageControllerName, "/command");
		} else {
			writeCommandURL();
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		return buf.length() == 0;
	}

	@Override
    public ResponseWriter beginFunction(String functionName) throws UnifyException {
        if (openFunction) {
            try {
                throw new RuntimeException("Function write already started.");
            } catch (Exception e) {
                throwOperationErrorException(e);
            }
        }

        buf.append(functionName).append("({");
        openFunction = true;
        paramAppendSym =  false;
        return this;
    }

    @Override
    public ResponseWriter endFunction() throws UnifyException {
        if (!openFunction) {
            try {
                throw new RuntimeException("Function write is not started");
            } catch (Exception e) {
                throwOperationErrorException(e);
            }
        }
        
        buf.append("});");
        openFunction = false;
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, String[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, String val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, Number[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, Number val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, Boolean[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, Boolean val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, char[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, char val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, int[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, int val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, long[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, long val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, short[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, short val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, float[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, float val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, double[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, double val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, boolean[] val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }

    @Override
    public ResponseWriter writeParam(String paramName, boolean val) throws UnifyException {
        preParamWrite();
        JsonUtils.writeField(buf, paramName, val);
        return this;
    }
    
    @Override
    public ResponseWriter writeParam(String paramName, JsonWriter val) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":").append(val.toString());
        return this;
    }

    @Override
    public ResponseWriter writeContextURLParam(String paramName, String path, String... pathElement)
            throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":\"");
        writeContextURL(path, pathElement);
        buf.append('"');
        return this;
    }

    @Override
    public ResponseWriter writeCommandURLParam(String paramName) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":\"");
        writeCommandURL();
        buf.append('"');
        return this;
    }

    @Override
    public ResponseWriter writeCommandURLParam(String paramName, String pageControllerName) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":\"");
        writeCommandURL(pageControllerName);
        buf.append('"');
        return this;
    }

    @Override
    public ResponseWriter writeResolvedParam(String paramName, String val) throws UnifyException {
        preParamWrite();
        buf.append('"').append(paramName).append("\":").append(val);
        return this;
    }

    @Override
	public WebStringWriter getStringWriter() {
		return buf;
	}

	@Override
	public void writeTo(Writer writer) throws UnifyException {
		try {
			buf.writeTo(writer);
		} catch (IOException e) {
			throwOperationErrorException(e);
		}
	}

	@Override
	public void useSecondary() {
		useSecondary(initialBufferCapacity);
	}

	@Override
	public void useSecondary(int initialCapacity) {
		secordaryList.add(buf);
		buf = new WebStringWriter(initialCapacity);
	}

	@Override
	public WebStringWriter discardSecondary() {
		WebStringWriter discLsw = buf;
		buf = secordaryList.remove(secordaryList.size() - 1);
		return discLsw;
	}

	@Override
	public void reset(Map<Class<? extends UplComponent>, UplComponentWriter> writers) {
		this.writers = writers;
		if (buf == null || !buf.isEmpty() || !secordaryList.isEmpty()) {
			buf = new WebStringWriter(initialBufferCapacity);
			secordaryList.clear();
			openFunction = false;
	        paramAppendSym =  false;
		}
	}

	@Override
	public boolean isTableMode() {
		return tableMode;
	}

	@Override
	public void setTableMode(boolean parentStyleMode) {
		this.tableMode = parentStyleMode;
	}

	@Override
	public String toString() {
		return buf.toString();
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

    private void preParamWrite() throws UnifyException {
        if (!openFunction) {
            try {
                throw new RuntimeException("Function write is not started");
            } catch (Exception e) {
                throwOperationErrorException(e);
            }
        }
        
        if (paramAppendSym) {
            buf.append(',');
        } else {
            paramAppendSym = true;
        }
    }

	private UplComponentWriter getWriter(UplComponent component) throws UnifyException {
		UplComponentWriter writer = writers.get(component.getClass());
		if (writer == null) {
			throw new UnifyException(UnifyWebUIErrorConstants.UPLCOMPONENT_NO_WRITER, component.getName());
		}
		return writer;
	}

	private PageManager getPageManager() throws UnifyException {
		return (PageManager) getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
	}

	private PageRequestContextUtil getRequestContextUtil() throws UnifyException {
		return (PageRequestContextUtil) getComponent(WebUIApplicationComponents.APPLICATION_PAGEREQUESTCONTEXTUTIL);
	}

	private ResponseWriter writeJsonArray(Object[] arr, boolean quote) throws UnifyException {
		buf.append('[');
		if (arr != null) {
			if (quote) {
				boolean appendSym = false;
				for (Object val : arr) {
					if (appendSym) {
						buf.append(',');
					} else {
						appendSym = true;
					}
					if (val == null) {
						buf.append(val);
					} else {
						writeJsonQuote(String.valueOf(val));
					}
				}
			} else {
				boolean appendSym = false;
				for (Object val : arr) {
					if (appendSym) {
						buf.append(',');
					} else {
						appendSym = true;
					}

					buf.append(val);
				}
			}
		}
		buf.append(']');
		return this;
	}

}
