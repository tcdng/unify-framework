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

/**
 * Popup.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Popup {
    
    private String resultMapping;
    
    private Object backingBean;

    private int width;
    
    private int height;

	public Popup(String resultMapping, Object backingBean, int width, int height) {
		this.resultMapping = resultMapping;
		this.backingBean = backingBean;
		this.width = width;
		this.height = height;
	}

	public Popup(String resultMapping, Object backingBean) {
		this.resultMapping = resultMapping;
		this.backingBean = backingBean;
	}

	public String getResultMapping() {
		return resultMapping;
	}

	public Object getBackingBean() {
		return backingBean;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
