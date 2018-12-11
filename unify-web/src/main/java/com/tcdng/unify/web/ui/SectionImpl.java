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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * Default section implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-section")
@UplAttributes({ @UplAttribute(name = "tag", type = String.class), @UplAttribute(name = "caption", type = String.class),
		@UplAttribute(name = "binding", type = String.class),
		@UplAttribute(name = "components", type = UplElementReferences.class, mandatory = true),
		@UplAttribute(name = "privilege", type = String.class),
		@UplAttribute(name = "hidden", type = boolean.class, defaultValue = "false") })
public class SectionImpl extends AbstractUplComponent implements Section {

	@Override
	public String getTag() throws UnifyException {
		return getUplAttribute(String.class, "tag");
	}

	@Override
	public String getCaption() throws UnifyException {
		return getUplAttribute(String.class, "caption");
	}

	@Override
	public String getPrivilege() throws UnifyException {
		return getUplAttribute(String.class, "privilege");
	}

	@Override
	public String getBinding() throws UnifyException {
		return getUplAttribute(String.class, "binding");
	}

	@Override
	public boolean isBinding() throws UnifyException {
		return getUplAttribute(String.class, "binding") != null;
	}

	@Override
	public List<String> getReferences() throws UnifyException {
		return getUplAttribute(UplElementReferences.class, "components").getLongNames();
	}

	@Override
	public boolean isHidden() throws UnifyException {
		return getUplAttribute(boolean.class, "hidden");
	}

}
