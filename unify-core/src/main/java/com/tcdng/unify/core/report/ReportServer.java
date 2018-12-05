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
package com.tcdng.unify.core.report;

import java.io.File;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Report server component used for generating reports.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ReportServer extends UnifyComponent {

	/**
	 * Generates a report to file with specified file name.
	 * 
	 * @param report
	 *            the report to generate
	 * @param filename
	 *            the output filename
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void generateReport(Report report, String filename) throws UnifyException;

	/**
	 * Generates a report to a specified file.
	 * 
	 * @param report
	 *            the report to generate
	 * @param file
	 *            The output file
	 * @throws UnifyException
	 *             If an error occurs
	 */
	void generateReport(Report report, File file) throws UnifyException;

	/**
	 * Generates a report into specified output stream.
	 * 
	 * @param report
	 *            the report to generate
	 * @param outputStream
	 *            the output stream
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void generateReport(Report report, OutputStream outputStream) throws UnifyException;
}
