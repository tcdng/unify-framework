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
package com.tcdng.unify.jasperreports.server;

import net.sf.jasperreports.engine.design.JasperDesign;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.report.Report;

/**
 * UIComponent used to manage the layout of a report by manipulating a jasper
 * design object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface JasperReportsLayoutManager extends UnifyComponent {

	/**
	 * Applies a layout to a jasper report design object.
	 * 
	 * @param jasperDesign
	 *            the design to apply layout to
	 * @param report
	 *            the report definition object
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void applyLayout(JasperDesign jasperDesign, Report report) throws UnifyException;
}
