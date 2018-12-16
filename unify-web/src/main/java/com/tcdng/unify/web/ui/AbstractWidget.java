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
package com.tcdng.unify.web.ui;

import java.util.Collection;

import com.tcdng.unify.core.PrivilegeSettings;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.web.ControllerManager;
import com.tcdng.unify.web.RequestContextUtil;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.util.WidgetUtils;

/**
 * Abstract base class for widgets.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "binding", type = String.class),
        @UplAttribute(name = "styleClass", type = String.class, defaultValue = "$e{}"),
        @UplAttribute(name = "styleClassBinding", type = String.class),
        @UplAttribute(name = "style", type = String.class), @UplAttribute(name = "caption", type = String.class),
        @UplAttribute(name = "captionBinding", type = String.class),
        @UplAttribute(name = "columnStyle", type = String.class), @UplAttribute(name = "hint", type = String.class),
        @UplAttribute(name = "readOnly", type = boolean.class, defaultValue = "false"),
        @UplAttribute(name = "privilege", type = String.class),
        @UplAttribute(name = "fixedConforming", type = boolean.class, defaultValue = "false"),
        @UplAttribute(name = "hidden", type = boolean.class, defaultValue = "false"),
        @UplAttribute(name = "eventHandler", type = EventHandler[].class) })
public abstract class AbstractWidget extends AbstractUplComponent implements Widget {

    private String id;

    private String groupId;

    private Container container;

    private ValueStore valueStore;

    private boolean conforming;

    private boolean disabled;

    private boolean editable;

    private boolean visible;

    public AbstractWidget() {
        this.conforming = true;
        this.disabled = false;
        this.editable = true;
        this.visible = true;
    }

    @Override
    public String getId() throws UnifyException {
        return id;
    }

    @Override
    public void setId(String id) throws UnifyException {
        this.id = id;
    }

    @Override
    public String getGroupId() throws UnifyException {
        return groupId;
    }

    @Override
    public void setGroupId(String groupId) throws UnifyException {
        this.groupId = groupId;
    }

    @Override
    public String getFacadeId() throws UnifyException {
        return this.id;
    }

    @Override
    public String getPrefixedId(String prefix) throws UnifyException {
        return prefix + getId();
    }

    @Override
    public String getNamingIndexedId(int index) throws UnifyException {
        return WidgetUtils.getNamingIndexId(getId(), index);
    }

    @Override
    public String getCaption() throws UnifyException {
        String captionBinding = getUplAttribute(String.class, "captionBinding");
        if (captionBinding != null) {
            return getStringValue(captionBinding);
        }

        return getUplAttribute(String.class, "caption");
    }

    @Override
    public String getBinding() throws UnifyException {
        return getUplAttribute(String.class, "binding");
    }

    @Override
    public String getColumnStyle() throws UnifyException {
        return getUplAttribute(String.class, "columnStyle");
    }

    @Override
    public String getStyleClass() throws UnifyException {
        return getUplAttribute(String.class, "styleClass");
    }

    @Override
    public String getStyleClassBinding() throws UnifyException {
        return getUplAttribute(String.class, "styleClassBinding");
    }

    @Override
    public String getStyleClassValue() throws UnifyException {
        String styleClassBinding = getUplAttribute(String.class, "styleClassBinding");
        if (styleClassBinding != null) {
            return getValue(String.class, styleClassBinding);
        }

        return null;
    }

    @Override
    public String getStyle() throws UnifyException {
        if (isHidden()) {
            return "display:none;";
        }

        return getUplAttribute(String.class, "style");
    }

    @Override
    public String getHint() throws UnifyException {
        return getUplAttribute(String.class, "hint");
    }

    @Override
    public boolean isHidden() throws UnifyException {
        return getUplAttribute(boolean.class, "hidden");
    }

    @Override
    public boolean isMasked() throws UnifyException {
        return false;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void setValueStore(ValueStore valueStore) throws UnifyException {
        this.valueStore = valueStore;
    }

    @Override
    public ValueStore getValueStore() {
        return valueStore;
    }

    @Override
    public int getValueIndex() {
        if (valueStore != null) {
            return valueStore.getDataIndex();
        }
        return -1;
    }

    @Override
    public String getContainerId() throws UnifyException {
        if (container != null) {
            return container.getId();
        }
        return null;
    }

    @Override
    public String getPanelId() throws UnifyException {
        Panel panel = getPanel();
        if (panel != null) {
            return panel.getId();
        }
        return null;
    }

    @Override
    public Widget getRelayWidget() throws UnifyException {
        return null;
    }

    @Override
    public boolean isRelayCommand() {
        return false;
    }

    @Override
    public boolean isConforming() {
        return conforming;
    }

    @Override
    public boolean isValueConforming(Container container) {
        return (container == this.container) && conforming;
    }

    @Override
    public boolean isFixedConforming() throws UnifyException {
        return getUplAttribute(boolean.class, "fixedConforming");
    }

    @Override
    public void setConforming(boolean conforming) {
        this.conforming = conforming;
    }

    @Override
    public boolean isDisabled() throws UnifyException {
        return disabled || getPrivilegeSettings().isDisabled();
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isContainerDisabled() throws UnifyException {
        if (container != null) {
            return container.isContainerDisabled() || isDisabled();
        }
        return isDisabled();
    }

    @Override
    public boolean isEditable() throws UnifyException {
        return editable && !getUplAttribute(boolean.class, "readOnly") && getPrivilegeSettings().isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isContainerEditable() throws UnifyException {
        if (container != null) {
            return container.isContainerEditable() && isEditable();
        }
        return isEditable();
    }

    @Override
    public boolean isVisible() throws UnifyException {
        return visible && getPrivilegeSettings().isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isContainerVisible() throws UnifyException {
        if (container != null) {
            return container.isContainerVisible() && isVisible();
        }
        return isVisible();
    }

    @Override
    public boolean isValidatable() throws UnifyException {
        return isVisible() && !disabled && editable;
    }

    @Override
    public boolean isSupportReadOnly() {
        return true;
    }

    @Override
    public void onPageInitialize() throws UnifyException {

    }

    @Override
    public Object getValue() throws UnifyException {
        return getValue(getUplAttribute(String.class, "binding"));
    }

    @Override
    public <T> T getValue(Class<T> valueClass) throws UnifyException {
        return convert(valueClass, getValue(), null);
    }

    @Override
    public void setValue(Object value) throws UnifyException {
        setValue(getUplAttribute(String.class, "binding"), value);
    }

    @Override
    public String getStringValue() throws UnifyException {
        return convert(String.class, getValue(), null);
    }

    @Override
    public String getStringValue(String attribute) throws UnifyException {
        return convert(String.class, getValue(attribute), null);
    }

    @Override
    public <T> T getValue(Class<T> clazz, String attribute) throws UnifyException {
        return convert(clazz, getValue(attribute), null);
    }

    @Override
    public <T, U extends Collection<T>> U getValue(Class<U> clazz, Class<T> dataClass) throws UnifyException {
        return convert(clazz, dataClass, getValue(), null);
    }

    @Override
    public boolean isLayoutCaption() throws UnifyException {
        return true;
    }

    @Override
    public void addPageAliases() throws UnifyException {

    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public Object getValue(String attribute) throws UnifyException {
        if (attribute != null) {
            if (valueStore != null && valueStore.isGettable(attribute)) {
                return valueStore.retrieve(attribute);
            }

            if (isSessionAttribute(attribute)) {
                return getSessionAttribute(attribute);
            }

            if (isApplicationAttribute(attribute)) {
                return getApplicationAttribute(attribute);
            }

            if (isRequestAttribute(attribute)) {
                return getRequestAttribute(attribute);
            }
        }

        return null;
    }

    @Override
    public Panel getPanel() throws UnifyException {
        Widget container = this.container;
        while (container != null) {
            if (container instanceof Panel) {
                return (Panel) container;
            }
            container = container.getContainer();
        }
        return null;
    }

    protected PrivilegeSettings getPrivilegeSettings() throws UnifyException {
        return getPrivilegeSettings(getUplAttribute(String.class, "privilege"));
    }

    protected <T> T getRequestTarget(Class<T> clazz) throws UnifyException {
        return getRequestContextUtil().getRequestTargetValue(clazz);
    }

    protected void setCommandResultMapping(String resultMappingName) throws UnifyException {
        getRequestContextUtil().setCommandResultMapping(resultMappingName);
    }

    protected ControllerManager getControllerManager() throws UnifyException {
        return (ControllerManager) getComponent(WebApplicationComponents.APPLICATION_CONTROLLERMANAGER);
    }

    protected PageManager getPageManager() throws UnifyException {
        return (PageManager) getComponent(WebApplicationComponents.APPLICATION_PAGEMANAGER);
    }

    protected RequestContextUtil getRequestContextUtil() throws UnifyException {
        return (RequestContextUtil) getComponent(WebApplicationComponents.APPLICATION_REQUESTCONTEXTUTIL);
    }

    /**
     * Sets the value of an attribute in associated value store, if component has
     * one.
     * 
     * @param attribute
     *            the attribute name
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if value store has no value with such attribute name
     */
    protected void setValue(String attribute, Object value) throws UnifyException {
        if (valueStore != null && attribute != null) {
            valueStore.store(attribute, value);
        }
    }

    /**
     * Appends a UPL attribute to a UPL string.
     * 
     * @param sb
     *            string builder that represents the UPL string to append to
     * @param attribute
     *            this component's UPL attribute to append
     * @throws UnifyException
     *             if an error occurs
     */
    protected void appendUplAttribute(StringBuilder sb, String attribute) throws UnifyException {
        Object value = getUplAttribute(Object.class, attribute);
        if (value != null) {
            sb.append(' ').append(attribute).append(':').append(value);
        }
    }
}
