/*
 * Copyright 2018-2024 The Code Department.
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
 * Zero integer to NULL XML adapter class.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MarshalZeroIntToNullXmlAdapter extends XmlAdapter<String, Integer> {

	@Override
	public String marshal(Integer bound) throws Exception {
		return bound != null ? (bound == 0 ? null : bound.toString()) : null;
	}

	@Override
	public Integer unmarshal(String val) throws Exception {
		return val != null ? Integer.valueOf(val) : null;
	}

}
