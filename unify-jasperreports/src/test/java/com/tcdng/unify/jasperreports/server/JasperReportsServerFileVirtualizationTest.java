/*
 * Copyright 2018-2022 The Code Department.
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.constant.PageSizeType;
import com.tcdng.unify.core.constant.XOffsetType;
import com.tcdng.unify.core.constant.YOffsetType;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportLayoutType;
import com.tcdng.unify.core.report.ReportPageProperties;
import com.tcdng.unify.core.report.ReportServer;
import com.tcdng.unify.jasperreports.JasperReportsApplicationComponents;
import com.tcdng.unify.jasperreports.JasperReportsPropertyConstants;

/**
 * Jasper reports server tests with file virtualization.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Ignore // Comment Ignore to run
public class JasperReportsServerFileVirtualizationTest extends AbstractUnifyComponentTest {

	@Test
	public void testGeneratePlacementReportWithFileVirtualization() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().size(PageSizeType.A5)
				.landscape(true)
				.build();
		Report report = Report.newBuilder(ReportLayoutType.PLACEMENT_PDF, pageProperties)
				.title("Test Placement Report")
				.addLine(Color.RED, 0, 81, 200, 1)
				.addRectangle(Color.BLUE, Color.GREEN, 0, 84, 200, 40)
				.addText(Color.BLACK, "Hello World!", XOffsetType.RIGHT, YOffsetType.TOP, 20, 30, 100, 40)
				.addField(Color.BLACK, "name", String.class, 0, 0, 200, 40)
				.addField(Color.BLACK, "description", String.class, XOffsetType.RIGHT, YOffsetType.BOTTOM, 0, 40, 200, 40)
				.beanCollection(Arrays.asList(
						new Book("cross", "Cross Airs"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon"),
						new Book("crescent", "Crescent Moon")))
				.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//		IOUtils.writeToFile("d:\\data\\report.pdf", gen);
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {
		addContainerSetting(JasperReportsPropertyConstants.JASPERREPORTS_FILEVIRTUALIZATION, "true");
		addContainerSetting(UnifyCorePropertyConstants.APPLICATION_LOG_LEVEL, "debug");
		addContainerSetting(UnifyCorePropertyConstants.APPLICATION_LOG_TO_CONSOLE, "true");
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
