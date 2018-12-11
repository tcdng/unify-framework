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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.DataTransferBlock;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.util.WidgetUtils;

/**
 * Abstract user interface container.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "layout", type = Layout.class),
		@UplAttribute(name = "components", type = UplElementReferences.class),
		@UplAttribute(name = "arrayCascade", type = boolean.class, defaultValue = "false"),
		@UplAttribute(name = "space", type = boolean.class, defaultValue = "false") })
public abstract class AbstractContainer extends AbstractWidget implements Container {

	private WidgetRepository widgetRepository;

	private Map<String, Control> internalControls;

	private Object oldValue;

	private ValueStore thisValueStore;

	private ValueStore bindingValueStore;

	private boolean useLayoutIfPresent;

	public AbstractContainer() {
		this(true);
	}

	public AbstractContainer(boolean useLayoutIfPresent) {
		this.useLayoutIfPresent = useLayoutIfPresent;
	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public void setWidgetRepository(WidgetRepository widgetRepository) throws UnifyException {
		this.widgetRepository = widgetRepository;
	}

	@Override
	public boolean hasWidgetRepository() {
		return widgetRepository != null;
	}

	@Override
	public void setValueStore(ValueStore valueStore) throws UnifyException {
		super.setValueStore(valueStore);
		cascadeValueStore();
	}

	@Override
	public Set<String> getWidgetLongNames() throws UnifyException {
		return widgetRepository.getWidgetInfo(getLongName()).getDeepNames();
	}

	protected WidgetNameInfo getWidgetInfo() throws UnifyException {
		return widgetRepository.getWidgetInfo(getLongName());
	}

	@Override
	public Widget getWidgetByLongName(String longName) throws UnifyException {
		Widget widget = widgetRepository.getWidget(longName);
		if (widget == null) {
			throw new UnifyException(UnifyWebErrorConstants.WIDGET_WITH_LONGNAME_UNKNOWN, longName, getLongName());
		}

		return widget;
	}

	@Override
	public boolean isWidget(String longName) throws UnifyException {
		return widgetRepository.isWidget(longName);
	}

	@Override
	public Widget getWidgetByShortName(String shortName) throws UnifyException {
		Widget widget = widgetRepository.getWidget(getLongName(), shortName);
		if (widget != null) {
			return widget;
		}

		widget = widgetRepository.getWidget(getParentLongName(), shortName);
		if (widget != null) {
			return widget;
		}

		throw new UnifyException(UnifyWebErrorConstants.WIDGET_WITH_SHORTNAME_UNKNOWN, shortName, getLongName());
	}

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (transferBlock != null) {
			DataTransferBlock childBlock = transferBlock.getChildBlock();
			Control childWidget = (Control) internalControls.get(childBlock.getId());
			childWidget.populate(childBlock);
		}
	}

	@Override
	public List<String> getLayoutWidgetLongNames() throws UnifyException {
		return getShallowReferencedLongNames("components");
	}

	@Override
	public boolean isUseLayoutIfPresent() {
		return useLayoutIfPresent;
	}

	@Override
	public List<ValueStore> getRepeatValueStores() throws UnifyException {
		return null;
	}

	@Override
	public Layout getLayout() throws UnifyException {
		return getUplAttribute(Layout.class, "layout");
	}

	@Override
	public boolean isSpace() throws UnifyException {
		return getUplAttribute(boolean.class, "space");
	}

	@Override
	public boolean isRepeater() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void cascadeValueStore() throws UnifyException {
		String binding = getBinding();
		ValueStore valueStore = getValueStore();
		if (binding == null) {
			for (String longName : getShallowWidgetLongNames()) {
				Widget widget = widgetRepository.getWidget(longName);
				if (widget.isValueConforming(this)) {
					widget.setValueStore(valueStore);
				}
			}
		} else {
			Object value = getValue(binding);
			if (oldValue != value) {
				bindingValueStore = null;
				if (value == null) {
					for (String longName : getShallowWidgetLongNames()) {
						Widget widget = widgetRepository.getWidget(longName);
						if (widget.isValueConforming(this)) {
							if (widget.isFixedConforming()) {
								widget.setValueStore(valueStore);
							} else {
								widget.setValueStore(null);
							}
						}
					}
				} else {
					if (getUplAttribute(boolean.class, "arrayCascade")) {
						List<Object> values = (List<Object>) value;
						int i = 0;
						for (String longName : getShallowWidgetLongNames()) {
							Widget widget = widgetRepository.getWidget(longName);
							if (widget.isValueConforming(this)) {
								if (widget.isFixedConforming()) {
									widget.setValueStore(valueStore);
								} else {
									widget.setValueStore(createValueStore(values.get(i), i++));
								}
							}
						}
					} else {
						bindingValueStore = createValueStore(value);
						for (String longName : getShallowWidgetLongNames()) {
							Widget widget = widgetRepository.getWidget(longName);
							if (widget.isValueConforming(this)) {
								if (widget.isFixedConforming()) {
									widget.setValueStore(valueStore);
								} else {
									widget.setValueStore(bindingValueStore);
								}
							}
						}
					}
				}
				oldValue = value;
			}
		}
	}

	/**
	 * Adds an internal child control to this container.
	 * 
	 * @param descriptor
	 *            the internal control descriptor
	 * @return the child control
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected Control addInternalControl(String descriptor) throws UnifyException {
		Control control = (Control) getUplComponent(getSessionLocale(), descriptor, false);
		if (internalControls == null) {
			internalControls = new HashMap<String, Control>();
		}

		int childIndex = internalControls.size();
		String childId = WidgetUtils.getChildId(getId(), control.getId(), childIndex);
		control.setId(childId);
		control.onPageInitialize();
		control.setContainer(getContainer());
		setWidgetValueBeanToThis(control);
		internalControls.put(childId, control);
		return control;
	}

	/**
	 * Sets the source of the value store of widget to
	 * 
	 * @param shortName
	 *            the component short name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void setComponentValueBeanToThis(String shortName) throws UnifyException {
		Widget widget = getWidgetByShortName(shortName);
		setWidgetValueBeanToThis(widget);
	}

	/**
	 * Sets the visibility of a widget in this container by short name.
	 * 
	 * @param shortName
	 *            the component short name
	 * @param visible
	 *            the visible flag
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void setVisible(String shortName, boolean visible) throws UnifyException {
		getWidgetByShortName(shortName).setVisible(visible);
	}

	/**
	 * Sets the editability of a widget in this container by short name.
	 * 
	 * @param shortName
	 *            the component short name
	 * @param editable
	 *            the editable flag
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void setEditable(String shortName, boolean editable) throws UnifyException {
		getWidgetByShortName(shortName).setEditable(editable);
	}

	/**
	 * Sets if a widget is disabled in this container by short name.
	 * 
	 * @param shortName
	 *            the component short name
	 * @param disabled
	 *            the disabled flag
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void setDisabled(String shortName, boolean disabled) throws UnifyException {
		getWidgetByShortName(shortName).setDisabled(disabled);
	}

	/**
	 * Returns true if container has no referenced components.
	 */
	protected boolean isNoReferencedComponents() throws UnifyException {
		return getWidgetLongNames().isEmpty();
	}

	protected ValueStore getChildBindingValueStore() throws UnifyException {
		if (bindingValueStore != null) {
			return bindingValueStore;
		}

		return getValueStore();
	}

	private List<String> getShallowWidgetLongNames() throws UnifyException {
		return widgetRepository.getWidgetInfo(getLongName()).getShallowNames();
	}

	private void setWidgetValueBeanToThis(Widget widget) throws UnifyException {
		if (thisValueStore == null) {
			thisValueStore = createValueStore(this);
		}

		widget.setValueStore(thisValueStore);
		widget.setConforming(false);
	}

}
