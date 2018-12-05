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
package com.tcdng.unify.core.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used for setting the parameters for a report to be generated.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ReportParameters {

	private Map<String, Object> parameters;

	public ReportParameters() {
		parameters = new HashMap<String, Object>();
	}

	public ReportParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Set<String> getNames() {
		return parameters.keySet();
	}

	public Object getParameter(String name) {
		return parameters.get(name);
	}

	public void setParameter(String name, Object parameter) {
		parameters.put(name, parameter);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
}
