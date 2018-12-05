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
package com.tcdng.unify.web;

/**
 * Data transfer block.
 * 
 * @author Lateef Ojulari
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
		return header.getValue();
	}

	public Object getDebugValue() {
		return header.getDebugValue();
	}

	public DataTransferBlock getChildBlock() {
		return childBlock;
	}

	public String getLongName() {
		return header.getLongName();
	}

	public String getLongProperty() {
		return header.getLongProperty();
	}

	public String getShortProperty() {
		return header.getShortProperty();
	}

	public DataTransferBlock getSiblingBlock() {
		return header.getSiblingBlock();
	}

	public void setSiblingBlock(DataTransferBlock siblingBlock) {
		header.setSiblingBlock(siblingBlock);
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
