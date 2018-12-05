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
package com.tcdng.unify.web.ui.panel;

import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.ui.MenuItem;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.Panel;

/**
 * Flyout menu state.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface FlyoutMenu extends Panel {

	/**
	 * Returns a list of menu item ids.
	 */
	Set<String> getMenuItemIds();

	/**
	 * Returns a menu item by id.
	 * 
	 * @param id
	 *            the id
	 */
	MenuItem getMenuItem(String id);

	/**
	 * Returns current selection control.
	 */
	Control getCurrentSelCtrl();

	/**
	 * Returns current selection index.
	 */
	int getCurrentSel();

	/**
	 * Returns the selectID
	 */
	String getSelectId() throws UnifyException;

	/**
	 * Returns the menu slider window ID
	 */
	String getSliderWinId() throws UnifyException;

	/**
	 * Returns the menu slider ID
	 */
	String getSliderId() throws UnifyException;

	/**
	 * Returns the menu navigation ID
	 */
	String getNavId() throws UnifyException;

	/**
	 * Returns the menu back button ID
	 */
	String getBackButtonId() throws UnifyException;

	/**
	 * Returns the menu forward button ID
	 */
	String getForwardButtonId() throws UnifyException;

	/**
	 * Returns popup ID
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getPopupId() throws UnifyException;

	/**
	 * Returns popup window ID
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getPopupWinId() throws UnifyException;

	/**
	 * Returns popup content ID
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getPopupContentId() throws UnifyException;

	/**
	 * Returns the slider menu gap in pixels.
	 * 
	 * @throws UnifyException
	 *             if an error occurs.
	 */
	int getSliderGap() throws UnifyException;

	/**
	 * Returns the menu scroll rate.
	 * 
	 * @throws UnifyException
	 *             if an error occurs.
	 */
	int getScrollRate() throws UnifyException;

	/**
	 * Returns the menu scroll step rate.
	 * 
	 * @throws UnifyException
	 *             if an error occurs.
	 */
	int getScrollStepRate() throws UnifyException;

	/**
	 * Indicates if menu is vertical.
	 */
	boolean isVertical();
}
