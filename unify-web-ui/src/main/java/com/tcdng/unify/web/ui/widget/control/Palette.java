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

package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.ui.widget.AbstractControl;
import com.tcdng.unify.web.ui.widget.data.PaletteInfo;

/**
 * A palette widget.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-palette")
@UplAttributes({
	@UplAttribute(name = "paletteBinding", type = String.class, mandatory = true) ,
	@UplAttribute(name = "showSelected", type = boolean.class, defaultVal = "true") ,
	@UplAttribute(name = "columns", type = int.class) })
public class Palette extends AbstractControl {

	public int getColumns() throws UnifyException {
		final int columns = getUplAttribute(int.class, "columns");
		return columns <= 0 ? 1 : columns;
	}

	public boolean isShowSelected() throws UnifyException {
		return getUplAttribute(boolean.class, "showSelected");
	}
	
	public PaletteInfo getPaletteInfo() throws UnifyException {
		final String paletteBinding = getUplAttribute(String.class, "paletteBinding");
		return getValue(PaletteInfo.class, paletteBinding);
	}
}
