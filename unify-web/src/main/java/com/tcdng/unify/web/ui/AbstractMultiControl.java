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
package com.tcdng.unify.web.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.DataTransferBlock;
import com.tcdng.unify.web.util.WidgetUtils;

/**
 * Serves as a base class for controls that contain and make use of other
 * controls.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "components", type = UplElementReferences.class) })
public abstract class AbstractMultiControl extends AbstractControl implements MultiControl {

	private Map<String, ChildControlInfo> controlInfoMap;

	private ValueStore thisValueStore;

	public AbstractMultiControl() {
		controlInfoMap = new LinkedHashMap<String, ChildControlInfo>();
	}

	@Override
	public void addChildControl(Control control) throws UnifyException {
		doAddChildControl(control, false, false, false, true);
	}

	@Override
	public void setValueStore(ValueStore valueStore) throws UnifyException {
		super.setValueStore(valueStore);
		for (ChildControlInfo childControlInfo : controlInfoMap.values()) {
			if (childControlInfo.isConforming()) {
				childControlInfo.getControl().setValueStore(valueStore);
			}
		}
	}

	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
		for (ChildControlInfo childControlInfo : controlInfoMap.values()) {
			if (!childControlInfo.isIgnoreParentState()) {
				childControlInfo.getControl().setDisabled(disabled);
			}
		}
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		for (ChildControlInfo childControlInfo : controlInfoMap.values()) {
			if (!childControlInfo.isIgnoreParentState()) {
				childControlInfo.getControl().setEditable(editable);
			}
		}
	}

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (transferBlock != null) {
			DataTransferBlock childBlock = transferBlock.getChildBlock();
			Control control = (Control) getChildControlInfo(childBlock.getId()).getControl();
			control.populate(childBlock);
		}
	}

	@Override
	public ChildControlInfo getChildControlInfo(String childId) {
		return controlInfoMap.get(childId);
	}

	@Override
	public Collection<ChildControlInfo> getChildControlInfos() {
		return controlInfoMap.values();
	}

	@Override
	public int getChildControlCount() {
		return controlInfoMap.size();
	}

	@Override
	public void setId(String id) throws UnifyException {
		super.setId(id);
		if (controlInfoMap.isEmpty()) {
			Map<String, ChildControlInfo> map = new LinkedHashMap<String, ChildControlInfo>();
			for (ChildControlInfo childControlInfo : controlInfoMap.values()) {
				Control control = childControlInfo.getControl();
				String newChildId = WidgetUtils.renameChildId(id, control.getId());
				control.setId(newChildId);
				map.put(newChildId, new ChildControlInfo(control, childControlInfo.isIgnoreParentState(),
						childControlInfo.isExternal()));
			}

			controlInfoMap = map;
		}
	}

	/**
	 * Creates and adds a non-conforming external child control that doesn't ignore
	 * parent state.
	 * 
	 * @param descriptor
	 *            descriptor used to create child control.
	 * @return the added child control
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected Control addExternalChildControl(String descriptor) throws UnifyException {
		Control control = (Control) getUplComponent(getSessionLocale(), descriptor, false);
		doAddChildControl(control, true, false, false, true);
		return control;
	}

	/**
	 * Creates and adds a non-conforming internal child control that doesn't ignore
	 * parent state.
	 * 
	 * @param descriptor
	 *            descriptor used to create child control.
	 * @return the added child control
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected Control addInternalChildControl(String descriptor) throws UnifyException {
		return addInternalChildControl(descriptor, false, false);
	}

	/**
	 * Creates and adds an internal child control.
	 * 
	 * @param descriptor
	 *            descriptor used to create child control.
	 * @param conforming
	 *            indicates if child is conforming
	 * @param ignoreParentState
	 *            set this flag to true if child control ignore parent state.
	 * @return the added child control
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected Control addInternalChildControl(String descriptor, boolean conforming, boolean ignoreParentState)
			throws UnifyException {
		Control control = (Control) getUplComponent(getSessionLocale(), descriptor, false);
		doAddChildControl(control, true, conforming, ignoreParentState, false);
		return control;
	}

	/**
	 * Adds child control id to request context page aliases.
	 * 
	 * @param control
	 *            the child control
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void addPageAlias(Control control) throws UnifyException {
		getRequestContextUtil().addPageAlias(getId(), control.getId());
	}

	/**
	 * Adds id to request context page aliases.
	 * 
	 * @param id
	 *            the to add
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected void addPageAlias(String id) throws UnifyException {
		getRequestContextUtil().addPageAlias(getId(), id);
	}

	private void doAddChildControl(Control control, boolean pageInit, boolean conforming, boolean ignoreParentState,
			boolean external) throws UnifyException {
		int childIndex = controlInfoMap.size();
		String childId = WidgetUtils.getChildId(getId(), control.getId(), childIndex);
		control.setId(childId);
		if (pageInit) {
			control.onPageInitialize();
			control.setContainer(getContainer());
		}

		if (!ignoreParentState) {
			control.setEditable(isEditable());
			control.setDisabled(isDisabled());
		}

		if (conforming) {
			control.setValueStore(getValueStore());
		} else {
			control.setValueStore(getThisValueStore());
		}

		control.setConforming(conforming);
		controlInfoMap.put(childId, new ChildControlInfo(control, ignoreParentState, external));
	}

	private ValueStore getThisValueStore() throws UnifyException {
		if (thisValueStore == null) {
			thisValueStore = createValueStore(this);
		}

		return thisValueStore;
	}

	public static class ChildControlInfo {

		private Control control;

		private boolean external;

		private boolean ignoreParentState;

		public ChildControlInfo(Control control, boolean ignoreParentState, boolean external) {
			this.control = control;
			this.ignoreParentState = ignoreParentState;
			this.external = external;
		}

		public Control getControl() {
			return control;
		}

		public boolean isIgnoreParentState() {
			return ignoreParentState;
		}

		public boolean isExternal() {
			return external;
		}

		public boolean isConforming() {
			return control.isConforming();
		}

		public boolean isPrivilegeVisible() throws UnifyException {
			return control.isVisible();
		}
	}
}
