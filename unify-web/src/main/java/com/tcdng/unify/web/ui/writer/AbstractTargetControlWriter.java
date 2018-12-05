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

package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.TargetControl;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract base class for target control writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTargetControlWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		TargetControl targetControl = (TargetControl) widget;
		writer.write("<input type=\"hidden\"");
		writeTagId(writer, targetControl.getTargetId());
		String value = targetControl.getStaticBindingValue();
		if (value == null) {
			value = targetControl.getStringValue();
		}

		if (value != null) {
			writer.write(" value=\"").writeWithHtmlEscape(value).write("\"");
		} else {
			int index = targetControl.getValueIndex();
			if (index >= 0) {
				writer.write(" value=\"").write(index).write("\"");
			}
		}

		writer.write("/>");
		doWriteTargetControl(writer, targetControl);
	}

	protected abstract void doWriteTargetControl(ResponseWriter writer, TargetControl targetControl)
			throws UnifyException;
}
