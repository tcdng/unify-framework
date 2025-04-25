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

import com.tcdng.unify.common.constants.StandardFormatType;
import com.tcdng.unify.common.util.ParamToken;
import com.tcdng.unify.common.util.ProcessVariableUtils;
import com.tcdng.unify.common.util.StringToken;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Parameterized string generator.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ParameterizedStringGenerator {

	private final ValueStoreReader itemReader;

	private final ValueStoreReader parentReader;

	private final List<StringToken> tokenList;

	private final Map<String, ParamGenerator> generators;

	private final Map<StandardFormatType, Formatter<?>> formatters;

	public ParameterizedStringGenerator(ValueStoreReader itemReader, ValueStoreReader parentReader,
			List<StringToken> tokenList, Map<String, ParamGenerator> generators,
			Map<StandardFormatType, Formatter<?>> formatters) {
		this.itemReader = itemReader;
		this.parentReader = parentReader;
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
		return itemReader.getDataIndex();
	}

	public ParameterizedStringGenerator setDataIndex(int dataIndex) {
		itemReader.setDataIndex(dataIndex);
		return this;
	}

	@SuppressWarnings("unchecked")
	private Object getParam(StringToken token) throws UnifyException {
		Object val = null;
		if (token.isParam()) {
			if (token.isFormattedParam()) {
				String param = ((ParamToken) token).getParam();
				val = itemReader.getTempValue(param);
				if (val == null) {
					val = itemReader.read(param);
				}

				Formatter<Object> formatter = (Formatter<Object>) formatters.get(((ParamToken) token).getFormatType());
				val = formatter != null ? formatter.format(val) : val;
			} else if (token.isGeneratorParam()) {
				val = itemReader.getTempValue(token.getToken());
				if (val == null && !ProcessVariableUtils.isProcessVariable(token.getToken())) {
					ParamGenerator generator = generators.get(token.getToken());
					val = generator != null ? generator.generate(itemReader != null ? itemReader : null,
							parentReader != null ? parentReader : null, (ParamToken) token) : null;
				}
			} else {
				val = itemReader.getTempValue(token.getToken());
				if (val == null) {
					val = itemReader.read(token.getToken());
				}
			}
		} else {
			val = token.getToken();
		}

		return val;
	}
}
