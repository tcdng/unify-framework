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
package com.tcdng.unify.core.data;

import com.tcdng.unify.common.data.Listable;

/**
 * A listable data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ListData implements Listable {

    private String listKey;

    private String listDescription;

    public ListData(String listKey, String listDescription) {
        this.listKey = listKey;
        this.listDescription = listDescription;
    }

    @Override
    public String getListKey() {
        return listKey;
    }

    @Override
    public String getListDescription() {
        return listDescription;
    }

	@Override
	public String toString() {
		return "(" + listKey + ", " + listDescription + ")";
	}
}
