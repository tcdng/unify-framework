/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.core.database;

import java.util.Date;
import java.util.List;

/**
 * A grouping aggregation.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class GroupingAggregation {

	private List<Grouping> groupings;

	private List<Aggregation> aggregation;

	public GroupingAggregation(List<Grouping> groupings, List<Aggregation> aggregation) {
		this.groupings = groupings;
		this.aggregation = aggregation;
	}

	public List<Grouping> getGroupings() {
		return groupings;
	}

	public List<Aggregation> getAggregation() {
		return aggregation;
	}

	public int getGroupingCount() {
		return groupings.size();
	}
	
	public String getGroupingAsString(int index) {
		return groupings.get(index).getAsString();
	}

	public Date getGroupingAsDate(int index) {
		return groupings.get(index).getAsDate();
	}

	public boolean isStringGrouping(int index) {
		return groupings.get(index).isString();
	}

	public boolean isDateGrouping(int index) {
		return groupings.get(index).isDate();
	}

	public static class Grouping {

		private Object grouping;

		public Grouping(String grouping) {
			this.grouping = grouping;
		}

		public Grouping(Date grouping) {
			this.grouping = grouping;
		}

		public String getAsString() {
			return String.valueOf(grouping);
		}

		public Date getAsDate() {
			return (Date) grouping;
		}

		public boolean isString() {
			return grouping == null || String.class.equals(grouping.getClass());
		}

		public boolean isDate() {
			return grouping != null && Date.class.equals(grouping.getClass());
		}

	};

}
