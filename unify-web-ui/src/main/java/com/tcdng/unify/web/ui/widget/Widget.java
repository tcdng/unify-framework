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

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.web.ui.widget.panel.StandalonePanel;

/**
 * Interface for all classes that are user interface components. At runtime and
 * based on page descriptors read from source files or annotations, user
 * interface components are automatically created, initialized and added to
 * parent containers. A component is bound to a value source by the invoking the
 * setValueBean() and setProperty() methods. This specifies the value bean and
 * the property of the value bean that the component reads to get, usually, the
 * value it renders.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Widget extends UplComponent {

    /**
     * Returns the widget id
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getId() throws UnifyException;

    /**
     * Sets the widget id.
     * 
     * @param id
     *           the id to set
     * @throws UnifyException
     *                        if an error occurs
     */
    void setId(String id) throws UnifyException;

    /**
     * Returns the control non-indexed Id
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getBaseId() throws UnifyException;

    /**
     * Returns the group id
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getGroupId() throws UnifyException;

    /**
     * Sets the widget group id.
     * 
     * @param id
     *           the id to set
     * @throws UnifyException
     *                        if an error occurs
     */
    void setGroupId(String id) throws UnifyException;

    /**
     * Returns the widget facade Id
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getFacadeId() throws UnifyException;

    /**
     * Returns prefixed form of id
     * 
     * @param prefix
     *               the prefix to set
     * @throws UnifyException
     *                        if an error occurs
     */
    String getPrefixedId(String prefix) throws UnifyException;

    /**
     * Returns naming indexed form of id
     * 
     * @param index
     *              the index to use
     * @throws UnifyException
     *                        if an error occurs
     */
    String getNamingIndexedId(int index) throws UnifyException;

    /**
     * Returns widget data binding.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getBinding() throws UnifyException;

    /**
     * Returns widget caption.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getCaption() throws UnifyException;

    /**
     * Returns widget style class.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getStyleClass() throws UnifyException;

    /**
     * Returns widget style.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getStyle() throws UnifyException;

    /**
     * Returns widget style class binding.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getStyleClassBinding() throws UnifyException;

    /**
     * Returns widget table column style.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getColumnStyle() throws UnifyException;

    /**
     * Check if widget is marked for column summary.
     * 
     * @return a true value if marked otherwise false
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean getColumnSelectSummary() throws UnifyException;

    /**
     * Returns widget hint.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getHint() throws UnifyException;

    /**
     * Returns the ID of this component parent container.
     * 
     * @return the parent container ID, otherwise null if component is not in a
     *         container
     * @throws UnifyException
     *                        if an error occurs
     */
    String getContainerId() throws UnifyException;

    /**
     * Returns the ID of this component parent panel.
     * 
     * @return the parent panel ID, otherwise null if component is not in a
     *         container
     * @throws UnifyException
     *                        if an error occurs
     */
    String getPanelId() throws UnifyException;

    /**
     * Sets the component container.
     * 
     * @param container
     *                  the container to set
     */
    void setContainer(Container container);

    /**
     * Returns the component container.
     */
    Container getContainer();

    /**
     * Sets the component value store. Binds the component to a value store.
     * 
     * @param valueStore
     *                   the value store to set
     * @throws UnifyException
     *                        if an error occurs
     */
    void setValueStore(ValueStore valueStore) throws UnifyException;

    /**
     * Returns the component value store.
     */
    ValueStore getValueStore();

    /**
     * Returns value marker.
     */
    String getValueMarker();

    /**
     * Returns value prefix.
     */
    String getValuePrefix();

    /**
     * Returns value index.
     */
    int getValueIndex();

    /**
     * Returns the component current value.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    Object getValue() throws UnifyException;

    /**
     * Returns the component current value.
     * 
     * @param valueClass
     *                   the expected value type
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T getValue(Class<T> valueClass) throws UnifyException;

    /**
     * Sets the value of the bean attribute this component is binded to.
     * 
     * @param value
     *              the value to set.
     * @throws UnifyException
     *                        if an error occurs
     */
    void setValue(Object value) throws UnifyException;

    /**
     * Returns the component value as a string.
     * 
     * @throws UnifyException
     *                        if an error occurs.
     */
    String getStringValue() throws UnifyException;

    /**
     * Returns the string value of this component value bean attribute.
     * 
     * @param attribute
     *                  the attribute to fetch
     * @throws UnifyException
     *                        if an error occurs.
     */
    String getStringValue(String attribute) throws UnifyException;

    /**
     * Returns the type converted value of this component value bean attribute.
     * 
     * @param attribute
     *                  the attribute to fetch
     * @throws UnifyException
     *                        if an error occurs.
     */
    <T> T getValue(Class<T> clazz, String attribute) throws UnifyException;

    /**
     * Returns the value associated with supplied attribute. If this component is
     * associated with a value store, retrieves value from store using supplied
     * property as value name. Otherwise attempts to fetch attribute value from
     * session context, then application context and finally request context,
     * returning value where it first finds attribute
     * 
     * @param attribute
     *                  the attribute name
     * @return the attribute value
     * @throws UnifyException
     *                        if associated value object or application scopes have
     *                        no such attribute
     */
    Object getValue(String attribute) throws UnifyException;

    /**
     * Returns style class value based on style class binding otherwise a null is
     * returned.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getStyleClassValue() throws UnifyException;

    /**
     * Returns the type converted collection value of this component value bean
     * attribute.
     * 
     * @param clazz
     *                  the collection type
     * @param dataClass
     *                  the collection data type
     * @throws UnifyException
     *                        if an error occurs.
     */
    <T, U extends Collection<T>> U getValue(Class<U> clazz, Class<T> dataClass) throws UnifyException;

    /**
     * Returns this component's containing panel.
     * 
     * @throws UnifyException
     *                        if an error occurs.
     */
    Panel getPanel() throws UnifyException;

    /**
     * Returns this component's containing standalone panel.
     * 
     * @throws UnifyException
     *                        if an error occurs.
     */
    StandalonePanel getStandalonePanel() throws UnifyException;

    /**
     * Returns the relay widget otherwise null.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    Widget getRelayWidget() throws UnifyException;

    /**
     * Indicates that commands should be relayed to internal widget.
     */
    boolean isRelayCommand();

    /**
     * Indicates that component is a field.
     */
    boolean isField();

    /**
     * Indicates that a component is hidden. Hidden components are always rendered
     * but are not visible.
     * 
     * @return a true value if component is hidden
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isHidden() throws UnifyException;

    /**
     * Indicates if container state of widget ignores state of container.
     * 
     * @return a true value if widget ignores parent state otherwise false.
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isIgnoreParentState() throws UnifyException;

    /**
     * Indicates that a component is masked.
     * 
     * @return a true value if component is hidden
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isMasked() throws UnifyException;

    /**
     * Sets the conforming flag. A conforming component's
     * {@link #setValueStore(ValueStore)} is always invoked during a cascade
     * 
     * @param conforming
     *                   the flag to set
     */
    void setConforming(boolean conforming);

    /**
     * Returns the conforming flag.
     */
    boolean isConforming();

    /**
     * Tests if widget is value conforming.
     */
    boolean isValueConforming(Container container);

    /**
     * Tests if widget is fixed conforming.
     */
    boolean isFixedConforming() throws UnifyException;

    /**
     * Tests if widget is in facade mode.
     */
    boolean isUseFacade() throws UnifyException;

    /**
     * Tests if widget is facade focus mode.
     */
    boolean isUseFacadeFocus() throws UnifyException;

    /**
     * Tests if widget events are to be bound to facade.
     */
    boolean isBindEventsToFacade() throws UnifyException;

    /**
     * Sets the component editable state.
     * 
     * @param editable
     *                 the editable state to set
     */
    void setEditable(boolean editable);

    /**
     * Tests if component is editable.
     * 
     * @return true value if component is editable
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isEditable() throws UnifyException;

    /**
     * Tests if component is editable also considering the component's container
     * editable state.
     * 
     * @return true value if component is editable
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isContainerEditable() throws UnifyException;

    /**
     * Sets the component disabled state.
     * 
     * @param disabled
     *                 the disabled state to set
     */
    void setDisabled(boolean disabled);

    /**
     * Tests if component is disabled.
     * 
     * @return true value if component is disabled
     */
    boolean isDisabled() throws UnifyException;

    /**
     * Tests if component is disabled also considering the component's container
     * disabled state.
     * 
     * @return true value if component is disabled
     */
    boolean isContainerDisabled() throws UnifyException;

    /**
     * Tests if the component is visible and privilege exists in the current user
     * role.
     * 
     * @return true if component is visible and user role has component privilege
     */
    boolean isVisible() throws UnifyException;

    /**
     * Sets the component visible state.
     * 
     * @param visible
     *                the visible state to set
     */
    void setVisible(boolean visible);

    /**
     * Tests if component is visible also considering the component's container
     * visible state.
     * 
     * @return true value if component is visible
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isContainerVisible() throws UnifyException;

    /**
     * Tests if widget is visual active
     * 
     * @return true if component is active
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isActive() throws UnifyException;

    /**
     * Tests if the components behavior is always written irrespective of
     * visibility.
     * 
     * @return true if component is visible and user role has component privilege
     */
    boolean isBehaviorAlways() throws UnifyException;

    /**
     * Tests if component is validatable.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isValidatable() throws UnifyException;

    /**
     * Returns true if component's rendered element supports HTML readonly attribute
     * otherwise false. Subclasses should override this if necessary.
     */
    boolean isSupportReadOnly();

    /**
     * Returns true if component's rendered element supports HTML disabled attribute
     * otherwise false. Subclasses should override this if necessary.
     */
    boolean isSupportDisabled();

    /**
     * Returns true if this components caption is layout based.
     */
    boolean isLayoutCaption() throws UnifyException;

    /**
     * Sets widget alternate mode.
     * 
     * @param alternateMode
     *                      if an error occurs
     */
    void setAlternateMode(boolean alternateMode);

    /**
     * Sets the widget's tab index
     * 
     * @param tabIndex
     *                 the tab index to set
     */
    void setTabIndex(int tabIndex);

    /**
     * Gets this widget's tab index
     */
    int getTabIndex();

    /**
     * Expects widget to add page name aliases to current request context.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void addPageAliases() throws UnifyException;

    /**
     * Executed on page construction. Called by framework after all component
     * properties have been set and containing page is constructed.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void onPageConstruct() throws UnifyException;

    /**
     * Updates widget internal state. Widget is expected to synchronize internal
     * state with binded value.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void updateInternalState() throws UnifyException;

    /**
     * Gets the widget writer work object in current request context.
     * 
     * @return the writer work object
     * @throws UnifyException
     *                        if an error occurs
     */
    WriteWork getWriteWork() throws UnifyException;

    /**
     * Returns true if widget is a control.
     */
    boolean isControl();

    /**
     * Returns true if widget is a panel.
     */
    boolean isPanel();

    /**
     * Returns true if widget supports value store memory.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean supportsValueStoreMemory() throws UnifyException;

    /**
     * Initializes value store memory.
     * 
     * @param size
     *             the memory size
     * @return true if supports value store
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean initValueStoreMemory(int size) throws UnifyException;
    
    /**
     * Recalls value store at memory index.
     * 
     * @param memoryIndex
     *                    memory index
     * @return the value store if found otherwise false.
     * @throws UnifyException
     *                        if an error occurs
     */
    ValueStore recallValueStore(int memoryIndex) throws UnifyException;
}
