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

package com.tcdng.unify.core.data;

import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * A group aggregation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class GroupAggregation {

    private List<Grouping> groupingList;

    private List<Aggregation> aggregationList;

    public GroupAggregation(List<Grouping> groupingList, List<Aggregation> aggregationList) {
        this.groupingList = groupingList;
        this.aggregationList = aggregationList;
    }

    public List<Grouping> getGroupingList() {
        return groupingList;
    }

    public List<Aggregation> getAggregationList() {
        return aggregationList;
    }

    public <T> T getGroupingValue(Class<T> targetClazz, String fieldName) throws UnifyException {
        for (Grouping grouping : groupingList) {
            if (grouping.getFieldName().equals(fieldName)) {
                return DataUtils.convert(targetClazz, grouping.getValue());
            }
        }

        throw new UnifyException(UnifyCoreErrorConstants.AGGREGATION_GROUPING_FIELD_UNKNOWN, fieldName);
    }

    public <T> T getGroupingValue(Class<T> targetClazz, int index) throws UnifyException {
        return DataUtils.convert(targetClazz, groupingList.get(index).getValue());
    }

    public <T> T getAggregationValue(Class<T> targetClazz, int index) throws UnifyException {
        return aggregationList.get(index).getValue(targetClazz);
    }

    public int getGroupingSize() {
        return groupingList.size();
    }

    public int getAggregationSize() {
        return aggregationList.size();
    }
    
    public static class Grouping {

        private String fieldName;

        private Object value;

        public Grouping(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getValue() {
            return value;
        }
    }
}
