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
package com.tcdng.unify.core.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.PadDirection;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Batch file read configuration.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class BatchFileReadConfig {

    private String readerName;

    private String readProcessor;

    private Map<String, Object> parameters;

    private List<BatchFileFieldConfig> fieldConfigList;

    private ConstraintAction onConstraint;

    private boolean skipFirstRecord;

    public BatchFileReadConfig(String readerName, String readProcessor, Map<String, Object> parameters,
            List<BatchFileFieldConfig> fieldConfigList, ConstraintAction onConstraint, boolean skipFirstRecord) {
        this.readerName = readerName;
        this.readProcessor = readProcessor;
        this.parameters = parameters;
        this.fieldConfigList = fieldConfigList;
        this.onConstraint = onConstraint;
        this.skipFirstRecord = skipFirstRecord;
    }

    public String getReaderName() {
        return readerName;
    }

    public String getReadProcessor() {
        return readProcessor;
    }

    public <T> T getParameter(Class<T> paramType, String name) throws UnifyException {
        return DataUtils.convert(paramType, parameters.get(name));
    }

    public List<BatchFileFieldConfig> getFieldConfigList() {
        return fieldConfigList;
    }

    public ConstraintAction getOnConstraint() {
        return onConstraint;
    }

    public boolean isSkipFirstRecord() {
        return skipFirstRecord;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String readerName;

        private String readProcessor;

        private Map<String, Object> parameters;

        private List<BatchFileFieldConfig> fieldConfigList;

        private ConstraintAction onConstraint;

        private boolean skipFirstRecord;

        private Builder() {
            onConstraint = ConstraintAction.SKIP;
            skipFirstRecord = false;
        }

        public Builder reader(String readerName) {
            this.readerName = readerName;
            return this;
        }

        public Builder processor(String readProcessor) {
            this.readProcessor = readProcessor;
            return this;
        }

        public Builder skipFirstRecord(boolean skipFirstRecord) {
            this.skipFirstRecord = skipFirstRecord;
            return this;
        }

        public Builder addFieldConfig(String beanFieldName, String fileFieldName, String formatter,
                PadDirection padDirection, int length, boolean trim, boolean pad, boolean updateOnConstraint,
                Character padChar) {
            getFieldConfigList().add(new BatchFileFieldConfig(beanFieldName, fileFieldName, formatter, padDirection,
                    length, trim, pad, updateOnConstraint, padChar));
            return this;
        }

        public Builder addFieldConfig(String beanFieldName, int length, boolean trim) {
            getFieldConfigList()
                    .add(new BatchFileFieldConfig(beanFieldName, null, null, null, length, trim, false, true, ' '));
            return this;
        }

        public Builder addFieldConfig(BatchFileFieldConfig batchFileFieldConfig) {
            getFieldConfigList().add(batchFileFieldConfig);
            return this;
        }

        public Builder addParam(String name, Object val) {
            getParameters().put(name, val);
            return this;
        }

        public Builder addParams(Map<String, Object> params) {
            getParameters().putAll(params);
            return this;
        }

        public Builder onConstraint(ConstraintAction onConstraint) {
            this.onConstraint = onConstraint;
            return this;
        }

        public BatchFileReadConfig build() {
            return new BatchFileReadConfig(readerName, readProcessor, DataUtils.unmodifiableMap(parameters),
                    DataUtils.unmodifiableList(fieldConfigList), onConstraint, skipFirstRecord);
        }

        private Map<String, Object> getParameters() {
            if (parameters == null) {
                parameters = new HashMap<String, Object>();
            }

            return parameters;
        }

        private List<BatchFileFieldConfig> getFieldConfigList() {
            if (fieldConfigList == null) {
                fieldConfigList = new ArrayList<BatchFileFieldConfig>();
            }

            return fieldConfigList;
        }
    }

}
