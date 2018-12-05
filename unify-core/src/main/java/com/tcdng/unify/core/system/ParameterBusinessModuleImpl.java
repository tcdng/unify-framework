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
package com.tcdng.unify.core.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessModule;
import com.tcdng.unify.core.data.Input;
import com.tcdng.unify.core.data.Inputs;
import com.tcdng.unify.core.system.entities.ParameterDef;
import com.tcdng.unify.core.system.entities.ParameterValue;
import com.tcdng.unify.core.system.entities.ParameterValues;
import com.tcdng.unify.core.system.entities.ParameterValuesQuery;
import com.tcdng.unify.core.system.entities.ParametersDef;
import com.tcdng.unify.core.system.entities.ParametersDefQuery;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation of parameter module.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_PARAMETERBUSINESSMODULE)
public class ParameterBusinessModuleImpl extends AbstractBusinessModule implements ParameterBusinessModule {

	@Override
	public void defineParameters(String name, Class<?> type) throws UnifyException {
		List<ParameterDef> parameterList = new ArrayList<ParameterDef>();
		for (Parameter pa : AnnotationUtils.getParameters(type)) {
			String editor = AnnotationUtils.getAnnotationString(pa.editor());
			if (editor != null) {
				ParameterDef parameterDefData = new ParameterDef();
				parameterDefData.setDescription(pa.description());
				parameterDefData.setEditor(pa.editor());
				parameterDefData.setMandatory(pa.mandatory());
				parameterDefData.setName(pa.name());
				parameterDefData.setType(pa.type().getName());
				parameterList.add(parameterDefData);
			}
		}

		defineParameters(name, parameterList);
	}

	@Override
	public void defineParameters(String name, List<ParameterDef> parameterList) throws UnifyException {
		ParametersDef pdd = db().find(new ParametersDefQuery().typeName(name));
		if (pdd == null) {
			pdd = new ParametersDef();
			pdd.setTypeName(name);
			pdd.setParameterDefs(parameterList);
			db().create(pdd);
		} else {
			pdd.setParameterDefs(parameterList);
			db().updateByIdVersion(pdd);
		}
	}

	@Override
	public Map<String, ParameterDef> findParameterDefinitionsByName(String name) throws UnifyException {
		Map<String, ParameterDef> map = new LinkedHashMap<String, ParameterDef>();
		for (ParameterDef pd : db().find(new ParametersDefQuery().typeName(name)).getParameterDefs()) {
			map.put(pd.getName(), pd);
		}
		return map;
	}

	@Override
	public List<Input> fetchInputList(String name) throws UnifyException {
		ParametersDef parametersDefData = db().find(new ParametersDefQuery().typeName(name));
		if (parametersDefData != null) {
			List<Input> inputList = new ArrayList<Input>();
			for (ParameterDef pdd : parametersDefData.getParameterDefs()) {
				inputList.add(getParameterValue(pdd));
			}

			return inputList;
		}

		return Collections.emptyList();
	}

	@Override
	public Inputs fetchNormalizedInputs(String paramTypeName, String instTypeName, Long instId) throws UnifyException {
		List<Input> inputList = null;
		if (QueryUtils.isValidStringCriteria(paramTypeName)) {
			inputList = new ArrayList<Input>();
			Map<String, ParameterDef> parameterDefMap = findParameterDefinitionsByName(paramTypeName);
			Set<String> usedSet = new HashSet<String>();
			ParameterValues parameterValuesData = db()
					.list(new ParameterValuesQuery().typeName(paramTypeName).instTypeName(instTypeName).instId(instId));
			if (parameterValuesData != null) {
				for (ParameterValue parameterValueData : parameterValuesData.getParameterValues()) {
					String key = parameterValueData.getParamKey();
					ParameterDef parameterDefData = parameterDefMap.get(key);
					if (parameterDefData != null) {
						usedSet.add(key);
						Input parameterValue = getParameterValue(parameterDefData);
						parameterValue.setValue(parameterValueData.getParamValue());
						inputList.add(parameterValue);
					}
				}
			}

			if (usedSet.size() < parameterDefMap.size()) {
				for (Map.Entry<String, ParameterDef> entry : parameterDefMap.entrySet()) {
					if (!usedSet.contains(entry.getKey())) {
						ParameterDef parameterDefData = entry.getValue();
						inputList.add(getParameterValue(parameterDefData));
					}
				}
			}
		} else {
			inputList = Collections.emptyList();
		}

		return new Inputs(inputList);
	}

	@Override
	public Map<String, Object> findParameterTypeValues(String paramTypeName, String instTypeName, Long instId)
			throws UnifyException {
		ParameterValues parameterValuesData = db()
				.list(new ParameterValuesQuery().typeName(paramTypeName).instTypeName(instTypeName).instId(instId));
		if (parameterValuesData != null) {
			Map<String, Object> result = new HashMap<String, Object>();
			Map<String, ParameterDef> parameterDefMap = findParameterDefinitionsByName(paramTypeName);
			for (ParameterValue parameterValueData : parameterValuesData.getParameterValues()) {
				ParameterDef parameterDefData = parameterDefMap.get(parameterValueData.getParamKey());
				if (parameterDefData != null) {
					Class<?> type = ReflectUtils.getClassForName(parameterDefData.getType());
					result.put(parameterDefData.getName(),
							DataUtils.convert(type, parameterValueData.getParamValue(), null));
				}
			}

			return result;
		}

		return Collections.emptyMap();
	}

	@Override
	public void updateParameterValues(String paramTypeName, String instTypeName, Long instId, Inputs parameterValues)
			throws UnifyException {
		ParametersDef pdd = db().find(new ParametersDefQuery().typeName(paramTypeName));
		if (pdd == null) {
			throw new UnifyException(UnifyCoreErrorConstants.PARAMETER_DEFINITION_UNKNOWN, paramTypeName);
		}

		ParameterValues parameterValuesData = db()
				.list(new ParameterValuesQuery().typeName(paramTypeName).instTypeName(instTypeName).instId(instId));
		Long parameterValuesId = null;
		if (parameterValuesData == null) {
			parameterValuesData = new ParameterValues();
			parameterValuesData.setParametersDefId(pdd.getId());
			parameterValuesData.setInstTypeName(instTypeName);
			parameterValuesData.setInstId(instId);
			parameterValuesId = (Long) db().create(parameterValuesData);
		} else {
			parameterValuesId = parameterValuesData.getId();
		}

		List<ParameterValue> parameterValueList = new ArrayList<ParameterValue>();
		for (ParameterDef pddc : pdd.getParameterDefs()) {
			Input paramValue = parameterValues.getInput(pddc.getName());
			String value = null;
			if (paramValue != null) {
				value = paramValue.getValue();
			}

			if (pddc.isMandatory() && !QueryUtils.isValidStringCriteria(value)) {
				throw new UnifyException(UnifyCoreErrorConstants.PARAMETER_VALUE_REQUIRED, pddc.getName(),
						paramTypeName, instTypeName);
			}

			ParameterValue parameterValueData = new ParameterValue();
			parameterValueData.setParameterValuesId(parameterValuesId);
			parameterValueData.setParamKey(pddc.getName());
			parameterValueData.setParamValue(value);
			parameterValueList.add(parameterValueData);
		}

		parameterValuesData.setParameterValues(parameterValueList);
		db().updateByIdVersion(parameterValuesData);
	}

	@Override
	public void deleteParameterValues(String paramTypeName, String instTypeName, Long instId) throws UnifyException {
		db().deleteAll(
				new ParameterValuesQuery().typeName(paramTypeName).instTypeName(instTypeName).instId(instId));
	}

	private Input getParameterValue(ParameterDef parameterDefData) throws UnifyException {
		Class<?> type = ReflectUtils.getClassForName(parameterDefData.getType());
		return new Input(type, parameterDefData.getName(),
				resolveSessionMessage(parameterDefData.getDescription()), parameterDefData.getEditor(),
				parameterDefData.isMandatory());
	}

}
