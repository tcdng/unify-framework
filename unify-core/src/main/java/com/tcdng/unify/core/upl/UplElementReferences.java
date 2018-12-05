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
package com.tcdng.unify.core.upl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.CycleDetector;

/**
 * UPL element references.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplElementReferences {

	private List<String> idList;

	private List<String> longNameList;

	public UplElementReferences() {
		this.idList = new ArrayList<String>();
		this.longNameList = new ArrayList<String>();
	}

	public UplElementReferences(String[] ids) {
		this();
		for (String id : ids) {
			this.idList.add(id);
		}
	}

	public void add(UplElementReferences uplElementReferences) {
		this.idList.addAll(uplElementReferences.getIds());
		this.longNameList.addAll(uplElementReferences.getLongNames());
	}

	public List<String> getIds() {
		return Collections.unmodifiableList(this.idList);
	}

	public void setLongNames(UplElement ownerUplElement, CycleDetector<String> cycleDetector) throws UnifyException {
		this.longNameList.clear();
		for (String id : this.idList) {
			String longName = ownerUplElement.getReferenceLongName(id);
			this.longNameList.add(longName);
			if (cycleDetector != null) {
				cycleDetector.addReference(ownerUplElement.getLongName(), longName);
			}
		}
	}

	public List<String> getLongNames() {
		return Collections.unmodifiableList(this.longNameList);
	}

	public int length() {
		return this.idList.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("references:(idList = ").append(this.idList).append(';');
		sb.append("longNameList = ").append(this.longNameList).append(";)\n");
		return sb.toString();
	}
}
