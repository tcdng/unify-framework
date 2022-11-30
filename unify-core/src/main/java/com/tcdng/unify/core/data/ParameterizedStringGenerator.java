/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.data;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.StandardFormatType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Parameterized string generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParameterizedStringGenerator {

	private final ValueStore itemValueStore;

	private final ValueStore parentValueStore;

	private final List<StringToken> tokenList;

	private final Map<StringToken, ParamGenerator> generators;

	private final Map<StandardFormatType, Formatter<?>> formatters;

	public ParameterizedStringGenerator(ValueStore itemValueStore, ValueStore parentValueStore,
			List<StringToken> tokenList, Map<StringToken, ParamGenerator> generators,
			Map<StandardFormatType, Formatter<?>> formatters) {
		this.itemValueStore = itemValueStore;
		this.parentValueStore = parentValueStore;
		this.tokenList = tokenList;
		this.generators = generators;
		this.formatters = formatters;
	}

	public String generate() throws UnifyException {
		if (DataUtils.isBlank(tokenList)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (StringToken stringToken : tokenList) {
			Object val = getParam(stringToken);
			if (val != null) {
				sb.append(val);
			}
		}

		return sb.toString();
	}

	public int getDataIndex() {
		return itemValueStore.getDataIndex();
	}

	public ParameterizedStringGenerator setDataIndex(int dataIndex) {
		itemValueStore.setDataIndex(dataIndex);
		return this;
	}

	@SuppressWarnings("unchecked")
	private Object getParam(StringToken token) throws UnifyException {
		Object val = null;
		if (token.isParam()) {
			if (token.isFormattedParam()) {
				String param = ((ParamToken) token).getParam();
				val = itemValueStore.getTempValue(param);
				if (val == null) {
					val = itemValueStore.retrieve(param);
				}

				Formatter<Object> formatter = (Formatter<Object>) formatters.get(((ParamToken) token).getFormatType());
				val = formatter != null ? formatter.format(val) : val;
			} else if (token.isGeneratorParam()) {
				ParamGenerator generator = generators.get(token);
				val = generator != null ? generator.generate(itemValueStore != null ? itemValueStore.getReader() : null,
						parentValueStore != null ? parentValueStore.getReader() : null, (ParamToken) token) : null;
			} else {
				val = itemValueStore.getTempValue(token.getToken());
				if (val == null) {
					val = itemValueStore.retrieve(token.getToken());
				}
			}
		} else {
			val = token.getToken();
		}

		return val;
	}
}
