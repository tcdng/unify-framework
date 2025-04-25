/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.widget.data;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Search box data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SearchBox {

    private Object resultBean;

    private String resultPath;

    private List<Mapping> mappings;

    public SearchBox(Object resultBean, String resultPath) {
        this.resultBean = resultBean;
        this.resultPath = resultPath;
        this.mappings = new ArrayList<Mapping>();
    }

    public Object getResultBean() {
        return resultBean;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void addMapping(String resultFieldName, String selectFieldName) {
        this.mappings.add(new Mapping(resultFieldName, selectFieldName));
    }

    public void addMapping(Mapping mapping) {
        this.mappings.add(mapping);
    }

    public Mapping[] getMappings() {
        return DataUtils.toArray(Mapping.class, mappings);
    }

    public static class Mapping {

        private String resultFieldName;

        private String selectFieldName;

        public Mapping(String resultFieldName, String selectFieldName) {
            this.resultFieldName = resultFieldName;
            this.selectFieldName = selectFieldName;
        }

        public String getResultFieldName() {
            return resultFieldName;
        }

        public String getSelectFieldName() {
            return selectFieldName;
        }
    }
}
