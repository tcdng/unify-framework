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
package com.tcdng.unify.core.util.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * CData XML adapter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class CDataXmlAdapter extends XmlAdapter<String, String> {

	private static final String CDATA_START = "<![CDATA[";

	private static final String CDATA_END = "]]>";

	private static final String CDATA_BLANK = CDATA_START + CDATA_END;

	@Override
	public String marshal(String val) throws Exception {
		if (val != null) {
			return CDATA_START + val + CDATA_END;
		}

		return CDATA_BLANK;
	}

	@Override
	public String unmarshal(String val) throws Exception {
		if (val != null && val.startsWith(CDATA_START) && val.endsWith(CDATA_END)) {
			return val.substring(CDATA_START.length(), val.length() - CDATA_END.length());

		}

		return val;
	}

}
