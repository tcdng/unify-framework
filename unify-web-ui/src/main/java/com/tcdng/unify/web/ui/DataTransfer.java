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

package com.tcdng.unify.web.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Data transfer.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DataTransfer {

    private Class<?> validationClass;

    private Class<?> validationIdClass;

    private String actionId;

    private Map<String, DataTransferBlock> transferBlocks;

    public DataTransfer(Class<?> validationClass, Class<?> validationIdClass, String actionId,
            Map<String, DataTransferBlock> transferBlocks) {
        this.validationClass = validationClass;
        this.validationIdClass = validationIdClass;
        this.actionId = actionId;
        if (transferBlocks != null) {
            this.transferBlocks = transferBlocks;
        } else {
            this.transferBlocks = Collections.emptyMap();
        }
    }

    public Class<?> getValidationClass() {
        return validationClass;
    }

    public Class<?> getValidationIdClass() {
        return validationIdClass;
    }

    public String getActionId() {
        return actionId;
    }

    public DataTransferBlock getDataTransferBlock(String id) {
        return transferBlocks.get(id);
    }

    public Collection<DataTransferBlock> getDataTransferBlocks() {
        return transferBlocks.values();
    }
}
