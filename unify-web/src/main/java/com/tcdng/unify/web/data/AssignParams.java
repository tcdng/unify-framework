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

package com.tcdng.unify.web.data;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.list.AbstractListParam;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Assignment list parameters.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class AssignParams extends AbstractListParam {

    private final List<String> assignedIdList;

    private final Long assignBaseId;
    
    private final String rule;
    
    private final String filterId1;

    private final String filterId2;

    public AssignParams(Long assignBaseId, List<String> assignedIdList, String rule, String filterId1,
            String filterId2) {
        this.assignBaseId = assignBaseId;
        this.assignedIdList = assignedIdList;
        this.rule = rule;
        this.filterId1 = filterId1;
        this.filterId2 = filterId2;
    }

    public Long getAssignBaseId() {
        return assignBaseId;
    }

    public List<String> getAssignedIdList() {
        return assignedIdList;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAssignedIdList(Class<T> dataType) throws UnifyException {
        return (List<T>) DataUtils.convert(List.class, dataType, assignedIdList);
    }

    public String getRule() {
        return rule;
    }

    public String getFilterId1() {
        return filterId1;
    }

    public <T> T getFilterId1(Class<T> dataType) throws UnifyException {
        return DataUtils.convert(dataType, filterId1);
    }

    public String getFilterId2() {
        return filterId2;
    }

    public <T> T getFilterId2(Class<T> dataType) throws UnifyException {
        return DataUtils.convert(dataType, filterId2);
    }

    public boolean isAssignedIdList() {
        return assignedIdList != null && !assignedIdList.isEmpty();
    }

    public boolean isEmpty() {
        return assignedIdList == null && filterId1 == null && filterId2 == null;
    }

    public boolean isWithRule() {
        return !StringUtils.isBlank(rule);
    }
    
    @Override
    public boolean isPresent() {
        return true;
    }
}
