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
package com.tcdng.unify.web.ui.widget.data;

/**
 * Link information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class LinkInfo {

    private String code;

    private String caption;

    private int layoutIndex;
    
    public LinkInfo(String code, String caption) {
        this.code = code;
        this.caption = caption;
    }

    public String getCode() {
        return code;
    }

    public String getCaption() {
        return caption;
    }

	public int getLayoutIndex() {
		return layoutIndex;
	}

	public void setLayoutIndex(int layoutIndex) {
		this.layoutIndex = layoutIndex;
	}
}
