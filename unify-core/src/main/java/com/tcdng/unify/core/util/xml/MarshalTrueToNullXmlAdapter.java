/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Marshal true to NULL XML adapter class.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MarshalTrueToNullXmlAdapter extends XmlAdapter<String, Boolean> {

	@Override
	public String marshal(Boolean bound) throws Exception {
		return Boolean.TRUE.equals(bound) ? null : (bound != null ? String.valueOf(bound) : null);
	}

	@Override
	public Boolean unmarshal(String val) throws Exception {
		return val != null ? Boolean.valueOf(val) : null;
	}

}
