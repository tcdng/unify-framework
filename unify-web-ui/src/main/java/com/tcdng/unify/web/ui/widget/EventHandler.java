/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyException;

/**
 * User interface event handler component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface EventHandler extends Behavior {

	/**
	 * Gets event.
	 * 
	 * @throws UnifyException if an error occurs
	 */
	String getEvent() throws UnifyException;
	
    /**
     * Sets page action components for this event handler.
     * 
     * @param pageAction
     *            the page actions to set
     */
    void setPageAction(PageAction[] pageAction);

    /**
     * Returns page actions.
     */
    PageAction[] getPageAction();
}
