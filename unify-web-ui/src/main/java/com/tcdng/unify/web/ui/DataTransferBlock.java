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

/**
 * Data transfer block.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DataTransferBlock {

    private DataTransferHeader header;

    private String id;

    private int itemIndex;

    private DataTransferBlock childBlock;

    public DataTransferBlock(DataTransferHeader header, String id, int itemIndex) {
        this(header, id, itemIndex, null);
    }

    public DataTransferBlock(DataTransferHeader header, String id, int itemIndex, DataTransferBlock childBlock) {
        this.id = id;
        this.itemIndex = itemIndex;
        this.header = header;
        this.childBlock = childBlock;
    }

    public String getId() {
        return id;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public Object getValue() {
        if (header != null) {
            return header.getValue();
        }

        return null;
    }

    public void trimValue() {
        if (header != null) {
            header.trimValue();
        }
    }

    public Object getDebugValue() {
        if (header != null) {
            return header.getDebugValue();
        }

        return null;
    }

    public DataTransferBlock getChildBlock() {
        return childBlock;
    }

    public String getLongName() {
        if (header != null) {
            return header.getLongName();
        }

        return null;
    }

    public String getLongProperty() {
        if (header != null) {
            return header.getLongProperty();
        }

        return null;
    }

    public String getShortProperty() {
        if (header != null) {
            return header.getShortProperty();
        }

        return null;
    }

    public DataTransferBlock getSiblingBlock() {
        if (header != null) {
            return header.getSiblingBlock();
        }

        return null;
    }

    public void setSiblingBlock(DataTransferBlock siblingBlock) {
        if (header != null) {
            header.setSiblingBlock(siblingBlock);
        }
    }

    public String getShortId(String parentId) {
        if (id.startsWith(parentId)) {
            int fromIndex = id.indexOf('p', parentId.length());
            if (fromIndex > 0) {
                int endIndex = id.indexOf('.', fromIndex);
                return endIndex > 0 ? id.substring(fromIndex, endIndex) : id.substring(fromIndex);
            }
        }

        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{id=").append(id).append(", itemIndex = ").append(itemIndex).append('}');
        if (childBlock != null) {
            sb.append("->").append(childBlock);
        }
        return sb.toString();
    }
}
