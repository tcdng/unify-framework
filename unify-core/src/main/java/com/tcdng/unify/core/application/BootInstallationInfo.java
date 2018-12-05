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

package com.tcdng.unify.core.application;

import java.util.List;

/**
 * Boot installation information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BootInstallationInfo<T extends FeatureDefinition> {

	private List<String> featureInstallerNames;

	private List<T> features;

	public BootInstallationInfo(List<String> featureInstallerNames, List<T> features) {
		this.featureInstallerNames = featureInstallerNames;
		this.features = features;
	}

	public BootInstallationInfo() {

	}

	public List<String> getFeatureInstallerNames() {
		return featureInstallerNames;
	}

	public List<T> getFeatures() {
		return features;
	}

	public boolean isInstallers() {
		return featureInstallerNames != null && !featureInstallerNames.isEmpty();
	}

	public boolean isFeatures() {
		return features != null && !features.isEmpty();
	}
}
