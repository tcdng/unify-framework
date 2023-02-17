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

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.constant.PageSizeType;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.report.ReportLayoutType;
import com.tcdng.unify.core.report.ReportPageProperties;
import com.tcdng.unify.core.report.ReportServer;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.jasperreports.JasperReportsApplicationComponents;

/**
 * Jasper reports server tests
 * 
 * @author The Code Department
 * @since 1.0
 */
public class JasperReportsServerTest extends AbstractUnifyComponentTest {

	@Test
	public void testGeneratePlacementReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().size(PageSizeType.A5)
				.landscape(true)
				.build();
		Report report = Report.newBuilder(ReportLayoutType.PLACEMENT_PDF, pageProperties)
				.title("Test Placement Report")
				.addLine(Color.RED, 0, 81, 200, 1)
				.addRectangle(Color.BLUE, Color.GREEN, 0, 84, 200, 40)
				.addText(Color.BLACK, "Hello World!", 100, 300, 100, 40)
				.addField(Color.BLACK, "name", String.class, 0, 0, 200, 40)
				.addField(Color.BLACK, "description", String.class, 0, 40, 200, 40)
				.beanCollection(Arrays.asList(new Book("cross", "Cross Airs"), new Book("crescent", "Crescent Moon")))
				.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//		IOUtils.writeToFile("d:\\data\\report.pdf", gen);
	}

	@Test
	public void testGenerateCsvReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().build();
		Report report = Report.newBuilder(pageProperties).template("report/templates/dynamicreportportrait.jrxml")
				.format(ReportFormat.CSV).addColumn("Name", "name", String.class, 1)
				.addColumn("Description", "description", String.class, 1)
				.beanCollection(Arrays.asList(new Book("cross", "Cross"), new Book("crescent", "Crescent"))).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("c:\\data\\report.csv", gen);
	}

	@Test
	public void testGeneratePdfReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().build();
		Report report = Report.newBuilder(pageProperties).template("report/templates/dynamicreportportrait.jrxml")
				.format(ReportFormat.PDF).addColumn("Name", "name", String.class, 1)
				.addColumn("Description", "description", String.class, 1)
				.beanCollection(Arrays.asList(new Book("cross", "Cross"), new Book("crescent", "Crescent"))).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("d:\\data\\report.pdf", gen);
	}

	@Test
	public void testGenerateXlsReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().build();
		Report report = Report.newBuilder(pageProperties).template("report/templates/dynamicreportportrait.jrxml")
				.format(ReportFormat.XLS).addColumn("Name", "name", String.class, 1)
				.addColumn("Description", "description", String.class, 1)
				.beanCollection(Arrays.asList(new Book("cross", "Cross"), new Book("crescent", "Crescent"))).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("c:\\data\\report.xls", gen);
	}

	@Test
	public void testGenerateXlsxReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().build();
		Report report = Report.newBuilder(pageProperties).template("report/templates/dynamicreportportrait.jrxml")
				.format(ReportFormat.XLSX).addColumn("Name", "name", String.class, 1)
				.addColumn("Description", "description", String.class, 1)
				.beanCollection(Arrays.asList(new Book("cross", "Cross"), new Book("crescent", "Crescent"))).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("c:\\data\\report.xlsx", gen);
	}

	@Test
	public void testGenerateSingleColumnEmbeddedHtmlToPdfReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().build();
		Report report = Report.newBuilder(ReportLayoutType.SINGLECOLUMN_EMBEDDED_HTML, pageProperties)
				.template("report/templates/dynamicreportportrait.jrxml").format(ReportFormat.PDF)
				.addCompleteHtml("first", "<HTML>\r\n" + "<BODY LANG=\"en-US\" DIR=\"LTR\">\r\n"
						+ "<P ALIGN=LEFT STYLE=\"margin-bottom: 0in\"><FONT FACE=\"DejaVu Sans, sans-serif\"><FONT SIZE=4 STYLE=\"font-size: 16pt\"><FONT COLOR=\"#000000\">This\r\n"
						+ "is a </FONT><FONT COLOR=\"#000000\"><I><U><B>text field</B></U></I></FONT><FONT COLOR=\"#000000\"><I><B>\r\n"
						+ "</B></I></FONT><FONT COLOR=\"#000000\"> element containing </FONT><FONT COLOR=\"#FF8800\"><B><U><A HREF=\"http://en.wikipedia.org/wiki/HTML?x=1&y=2\" target=\"_blank\">HTML</A></U></B></FONT><FONT COLOR=\"#000000\">\r\n"
						+ " text. </FONT><FONT COLOR=\"#000000\"><I><B><SPAN STYLE=\"background: #ffff00\">HTML\r\n"
						+ "snippets</SPAN></B></I></FONT><FONT COLOR=\"#000000\"> can be used\r\n"
						+ "inside text elements by setting the </FONT><FONT COLOR=\"#0000ff\"><I>markup\r\n"
						+ "</I></FONT><FONT COLOR=\"#000000\">attribute available for the\r\n"
						+ "</FONT><FONT COLOR=\"#ff00ff\"><B>textElement </B></FONT><FONT COLOR=\"#000000\">tag\r\n"
						+ "to </FONT><FONT COLOR=\"#ff0000\"><I>html</I></FONT><FONT COLOR=\"#000000\">.</FONT></FONT></FONT></P>\r\n"
						+ "<P/>\r\n" + "<P/>\r\n" + "This is a bulleted list of fruits:\r\n" + "<UL>\r\n"
						+ "<LI>apple</LI>\r\n" + "<LI>banana</LI>\r\n" + "<LI>cherry</LI>\r\n" + "</UL>\r\n"
						+ "<P/>\r\n" + "<P/>\r\n" + "This is a numbered list of sports starting with number 4:\r\n"
						+ "<OL start=\"4\">\r\n" + "<LI>football</LI>\r\n" + "<LI>rugby</LI>\r\n"
						+ "<LI>tennis</LI>\r\n" + "</OL>\r\n" + "<P/>\r\n" + "<P/>\r\n"
						+ "This is a lettered list of shapes starting with letter c:\r\n"
						+ "<OL type=\"a\" start=\"3\">\r\n" + "<LI>circle</LI>\r\n" + "<LI>ellipse</LI>\r\n"
						+ "<LI>rectangle</LI>\r\n" + "</OL>\r\n" + "<P/>\r\n" + "<P/>\r\n"
						+ "Following is a numbered list of animals using Roman numerals and starting with number 3 (III):\r\n"
						+ "<OL type=\"I\" start=\"3\">\r\n" + "<LI>lion</LI>\r\n" + "<LI>elephant</LI>\r\n"
						+ "<LI>zebra</LI>\r\n" + "</OL>\r\n" + "<P/>\r\n" + "</BODY>\r\n" + "</HTML>")
				.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("d:\\data\\report.pdf", gen);
	}

	@Test
	public void testGenerateMultiDocHtmlToPdfReport() throws Exception {
		ReportServer reportServer = (ReportServer) getComponent(
				JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
		ReportPageProperties pageProperties = ReportPageProperties.newBuilder().allMargin(10).build();
		Report report = Report.newBuilder(ReportLayoutType.MULTIDOCHTML_PDF, pageProperties).addCompleteHtml("first",
				"<html>\r\n" + "<body>\r\n" + "\r\n" + "<h1 style=\"color:blue;\">A Blue Heading</h1>\r\n" + "\r\n"
						+ "<p style=\"color:red;\">A red paragraph.</p>\r\n" + "\r\n" + "</body>\r\n" + "</html>")
				.addBodyContentHtml("second", ".fc-formlisting {max-width:1240px;}\r\n"
						+ ".fc-formlisting .flsection {}\r\n"
						+ ".fc-formlisting .flsection span {display: block; padding: 5px 4px; margin-bottom: 8px; font-size: 11px;font-weight: bold;\r\n"
						+ "	background-color:#6e819d; color: #ffffff; /*border-radius: 4px;*/}\r\n"
						+ ".fc-formlisting .flsectionbody {display:table;margin-bottom:8px;}\r\n"
						+ ".fc-formlisting .flsectionbodyrow {display:table-row;}\r\n"
						+ ".fc-formlisting .flsectionbodycell {display:table-cell;padding-left: 8px;padding-right: 8px;padding-bottom: 5px;font-size:11px}\r\n"
						+ ".fc-formlisting .flgray {background-color:#f4f4f4;}\r\n"
						+ ".fc-formlisting .fltable {display:table;width:100%;}\r\n"
						+ ".fc-formlisting .flrow {display:table-row;}\r\n"
						+ ".fc-formlisting .flcell {display:table-cell;box-sizing: border-box;padding: 4px 2px;}\r\n"
						+ ".fc-formlisting .flcontent {display:block;}\r\n"
						+ ".fc-formlisting .flboldlabel {font-weight:bold;}\r\n"
						+ ".fc-formlisting .flboldtext {font-weight:bold;}\r\n"
						+ ".fc-formlisting .haleft {text-align:left;}\r\n"
						+ ".fc-formlisting .hacenter {text-align:center;}\r\n"
						+ ".fc-formlisting .haright {text-align:right;}\r\n"
						+ ".fc-formlisting .hajustified {text-align:justify;}\r\n",
						"<div id=\"p2391453920\" class=\"fc-formlisting\">\r\n"
//            					+ "         <div><img src=\"file:///D:/data/web/images/microchip.png\" width=\"40px;\"/></div>\r\n"
								+ "         <div class=\"flsection\"><span>Date: 15-10-2022      Risk / Debit Note      No.:22/516/1</span></div>\r\n"
								+ "         <div class=\"flsectionbody\" style=\"width:100%;margin-left:0%;\">\r\n"
								+ "            <div class=\"flsectionbodyrow\">\r\n"
								+ "               <div class=\"flsectionbodycell\" style=\"width:50%;\">\r\n"
								+ "                  <div class=\"fltable\">\r\n"
								+ "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Insured Name:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\"></span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Insured Address:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">P O Box 098878-6555, </span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Insurer Name:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">Geminia Insurance Company Ltd, P O Box 61316-00200, Nairobi</span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Type of Business:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">New - New</span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">PIN No.:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">45557766777</span></div>\r\n"
								+ "                     </div>\r\n" + "                  </div>\r\n"
								+ "               </div>\r\n"
								+ "               <div class=\"flsectionbodycell\" style=\"width:50%;\">\r\n"
								+ "                  <div class=\"fltable\">\r\n"
								+ "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Motor Cover:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">Comprehensive</span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Product:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">Private Comprehensive</span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Policy No.:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">TBA/22/516/1</span></div>\r\n"
								+ "                     </div>\r\n" + "                     <div class=\"flrow\">\r\n"
								+ "                        <div class=\"flcell\" style=\"width:30%;\"><span class=\"flcontent  haleft\">Period of Cover:</span></div>\r\n"
								+ "                        <div class=\"flcell\" style=\"width:70%;\"><span class=\"flcontent  haleft\">03-10-2022 to 02-10-2023</span></div>\r\n"
								+ "                     </div>\r\n" + "                  </div>\r\n"
								+ "               </div>\r\n" + "            </div>\r\n" + "         </div>\r\n"
								+ "      </div>\r\n")
				.build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reportServer.generateReport(report, baos);
		baos.flush();
		byte[] gen = baos.toByteArray();
		assertNotNull(gen);
		assertTrue(gen.length > 0);
//        IOUtils.writeToFile("d:\\data\\report.pdf", gen);
	}

	@Override
	protected void doAddSettingsAndDependencies() throws Exception {

	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
