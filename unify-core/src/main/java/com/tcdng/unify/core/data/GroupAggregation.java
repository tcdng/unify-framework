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

/**
 * A group aggregation.
 * 
 * @author Lateef Ojulari
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
