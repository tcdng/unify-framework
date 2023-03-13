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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.constants.StandardFormatType;
import com.tcdng.unify.common.util.ParamToken;
import com.tcdng.unify.common.util.StringToken;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.format.FormatHelper;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for parameter generator manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractParamGeneratorManager extends AbstractUnifyComponent implements ParamGeneratorManager {

	@Configurable
	private FormatHelper formarHelper;

	private FactoryMap<ParamToken, ParamGenerator> generators;

	public AbstractParamGeneratorManager() {
		this.generators = new FactoryMap<ParamToken, ParamGenerator>() {
			@Override
			protected ParamGenerator create(ParamToken token, Object... params) throws Exception {
				if ("g".equals(token.getComponent())) {
					return (ParamGenerator) getComponent(token.getParam());
				}

				ParamGenerator generator = resolveParamGenerator(token);
				if (generator == null) {
					throwOperationErrorException(new IllegalArgumentException(
							"Could not resolve generator for [" + token.getToken() + "]."));
				}

				return generator;
			}
		};
	}

	public final void setFormarHelper(FormatHelper formarHelper) {
		this.formarHelper = formarHelper;
	}

	@Override
	public ParameterizedStringGenerator getParameterizedStringGenerator(ValueStoreReader paramReader,
			List<StringToken> tokenList) throws UnifyException {
		return getParameterizedStringGenerator(paramReader, paramReader, tokenList);
	}

	@Override
	public ParameterizedStringGenerator getParameterizedStringGenerator(ValueStoreReader paramReader,
			ValueStoreReader generatorReader, List<StringToken> tokenList) throws UnifyException {
		Map<StringToken, ParamGenerator> _generators = new HashMap<StringToken, ParamGenerator>();
		Map<StandardFormatType, Formatter<?>> _formatters = new HashMap<StandardFormatType, Formatter<?>>();
		if (!DataUtils.isBlank(tokenList)) {
			for (StringToken token : tokenList) {
				if (token.isFormattedParam()) {
					StandardFormatType formatType = ((ParamToken) token).getFormatType();
					if (!_formatters.containsKey(formatType)) {
						_formatters.put(formatType, formarHelper.newFormatter(formatType));
					}
				} else if (token.isGeneratorParam()) {
					ParamGenerator _generator = generators.get((ParamToken) token);
					_generators.put(token, _generator);
				}
			}
		}

		return new ParameterizedStringGenerator(paramReader, generatorReader, tokenList, _generators, _formatters);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract ParamGenerator resolveParamGenerator(ParamToken token) throws UnifyException;
}
