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

package com.tcdng.unify.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Unify component settings.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyComponentSettings {

	public static final UnifyComponentSettings EMPTY_SETTINGS = new UnifyComponentSettings.Builder().build();

	private Map<String, Setting> settings;

	public UnifyComponentSettings(Setting[] settings) {
		this.settings = new HashMap<String, Setting>();
		for (Setting setting : settings) {
			this.settings.put(setting.getName(), setting);
		}
	}

	private UnifyComponentSettings(Map<String, Setting> settings) {
		this.settings = settings;
	}

	public Set<String> getPropertyNames() {
		return settings.keySet();
	}

	public Setting getSetting(String property) {
		return settings.get(property);
	}

	public boolean isProperty(String property) {
		return settings.containsKey(property);
	}

	public Object getSettingValue(String property) {
		Setting setting = settings.get(property);
		if (setting != null) {
			return setting.getValue();
		}

		return null;
	}

	public boolean isConcealed(String property) {
		Setting setting = settings.get(property);
		if (setting != null) {
			return setting.isHidden();
		}

		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tsettings = {\n");
		boolean isAppendSymbol = false;
		for (Map.Entry<String, Setting> entry : settings.entrySet()) {
			if (isAppendSymbol) {
				sb.append(",\n");
			} else {
				isAppendSymbol = true;
			}
			sb.append("\t\t").append(entry.getKey()).append(": value = ").append(entry.getValue().getValue())
					.append(", concealed = ").append(entry.getValue().isHidden());
		}
		sb.append("}");
		return sb.toString();
	}

	public static class Builder {

		private Map<String, Setting> settings;

		public Builder() {
			settings = new HashMap<String, Setting>();
		}

		public Builder(UnifyComponentSettings annotationSettings) {
			settings = new HashMap<String, Setting>();
			settings.putAll(annotationSettings.settings);
		}

		public Builder setProperty(String name, Object value) {
			return setProperty(name, value, false);
		}

		public Builder setProperty(String name, Object value, boolean concealed) {
			settings.put(name, new Setting(name, value, concealed));
			return this;
		}

		public UnifyComponentSettings build() {
			return new UnifyComponentSettings(settings);
		}

	}

}
