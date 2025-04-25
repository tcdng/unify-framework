/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core;

import com.tcdng.unify.core.constant.TriState;

/**
 * View directive.
 * 
 * @author The Code Department
 * @version 1.0
 */
public class ViewDirective {

	public static final ViewDirective ALLOW_VIEW_DIRECTIVE = new ViewDirective(true, true, false, TriState.CONFORMING);

	public static final ViewDirective DISALLOW_VIEW_DIRECTIVE = new ViewDirective(false, false, true, TriState.TRUE);

	private boolean visible;

	private boolean editable;

	private boolean disabled;

	private TriState required;

	public ViewDirective(boolean visible, boolean editable, boolean disabled, TriState required) {
		this.visible = visible;
		this.editable = editable;
		this.disabled = disabled;
		this.required = required;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public TriState getRequired() {
		return required;
	}
}
