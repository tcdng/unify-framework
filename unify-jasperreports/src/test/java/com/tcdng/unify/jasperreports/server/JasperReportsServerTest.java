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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.report.ReportLayoutType;
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
    public void testGenerateCsvReport() throws Exception {
        ReportServer reportServer = (ReportServer) getComponent(
                JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
        Report report = Report.newBuilder()
                .template("report/templates/dynamicreportportrait.jrxml")
                .format(ReportFormat.CSV)
                .addColumn("Name", "name", String.class, 1)
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
        Report report = Report.newBuilder()
                .template("report/templates/dynamicreportportrait.jrxml")
                .format(ReportFormat.PDF)
                .addColumn("Name", "name", String.class, 1)
                .addColumn("Description", "description", String.class, 1)
                .beanCollection(Arrays.asList(new Book("cross", "Cross"), new Book("crescent", "Crescent"))).build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        reportServer.generateReport(report, baos);
        baos.flush();
        byte[] gen = baos.toByteArray();
        assertNotNull(gen);
        assertTrue(gen.length > 0);
        IOUtils.writeToFile("d:\\data\\report.pdf", gen);
    }

    @Test
    public void testGenerateXlsReport() throws Exception {
        ReportServer reportServer = (ReportServer) getComponent(
                JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
        Report report = Report.newBuilder()
                .template("report/templates/dynamicreportportrait.jrxml")
                .format(ReportFormat.XLS)
                .addColumn("Name", "name", String.class, 1)
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
        Report report = Report.newBuilder()
                .template("report/templates/dynamicreportportrait.jrxml")
                .format(ReportFormat.XLSX)
                .addColumn("Name", "name", String.class, 1)
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
    public void testGenerateHtmlToPdfReport() throws Exception {
        ReportServer reportServer = (ReportServer) getComponent(
                JasperReportsApplicationComponents.JASPERREPORTS_SERVER);
        Report report = Report.newBuilder(ReportLayoutType.SINGLECOLUMN_EMBEDDED_HTML)
                .template("report/templates/dynamicreportportrait.jrxml")
                .format(ReportFormat.PDF)
                .addEmbeddedHtml("first", "<HTML>\r\n"
                		+ "<BODY LANG=\"en-US\" DIR=\"LTR\">\r\n"
                		+ "<P ALIGN=LEFT STYLE=\"margin-bottom: 0in\"><FONT FACE=\"DejaVu Sans, sans-serif\"><FONT SIZE=4 STYLE=\"font-size: 16pt\"><FONT COLOR=\"#000000\">This\r\n"
                		+ "is a </FONT><FONT COLOR=\"#000000\"><I><U><B>text field</B></U></I></FONT><FONT COLOR=\"#000000\"><I><B>\r\n"
                		+ "</B></I></FONT><FONT COLOR=\"#000000\"> element containing </FONT><FONT COLOR=\"#FF8800\"><B><U><A HREF=\"http://en.wikipedia.org/wiki/HTML?x=1&y=2\" target=\"_blank\">HTML</A></U></B></FONT><FONT COLOR=\"#000000\">\r\n"
                		+ " text. </FONT><FONT COLOR=\"#000000\"><I><B><SPAN STYLE=\"background: #ffff00\">HTML\r\n"
                		+ "snippets</SPAN></B></I></FONT><FONT COLOR=\"#000000\"> can be used\r\n"
                		+ "inside text elements by setting the </FONT><FONT COLOR=\"#0000ff\"><I>markup\r\n"
                		+ "</I></FONT><FONT COLOR=\"#000000\">attribute available for the\r\n"
                		+ "</FONT><FONT COLOR=\"#ff00ff\"><B>textElement </B></FONT><FONT COLOR=\"#000000\">tag\r\n"
                		+ "to </FONT><FONT COLOR=\"#ff0000\"><I>html</I></FONT><FONT COLOR=\"#000000\">.</FONT></FONT></FONT></P>\r\n"
                		+ "<P/>\r\n"
                		+ "<P/>\r\n"
                		+ "This is a bulleted list of fruits:\r\n"
                		+ "<UL>\r\n"
                		+ "<LI>apple</LI>\r\n"
                		+ "<LI>banana</LI>\r\n"
                		+ "<LI>cherry</LI>\r\n"
                		+ "</UL>\r\n"
                		+ "<P/>\r\n"
                		+ "<P/>\r\n"
                		+ "This is a numbered list of sports starting with number 4:\r\n"
                		+ "<OL start=\"4\">\r\n"
                		+ "<LI>football</LI>\r\n"
                		+ "<LI>rugby</LI>\r\n"
                		+ "<LI>tennis</LI>\r\n"
                		+ "</OL>\r\n"
                		+ "<P/>\r\n"
                		+ "<P/>\r\n"
                		+ "This is a lettered list of shapes starting with letter c:\r\n"
                		+ "<OL type=\"a\" start=\"3\">\r\n"
                		+ "<LI>circle</LI>\r\n"
                		+ "<LI>ellipse</LI>\r\n"
                		+ "<LI>rectangle</LI>\r\n"
                		+ "</OL>\r\n"
                		+ "<P/>\r\n"
                		+ "<P/>\r\n"
                		+ "Following is a numbered list of animals using Roman numerals and starting with number 3 (III):\r\n"
                		+ "<OL type=\"I\" start=\"3\">\r\n"
                		+ "<LI>lion</LI>\r\n"
                		+ "<LI>elephant</LI>\r\n"
                		+ "<LI>zebra</LI>\r\n"
                		+ "</OL>\r\n"
                		+ "<P/>\r\n"
                		+ "</BODY>\r\n"
                		+ "</HTML>")
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        reportServer.generateReport(report, baos);
        baos.flush();
        byte[] gen = baos.toByteArray();
        assertNotNull(gen);
        assertTrue(gen.length > 0);
        IOUtils.writeToFile("d:\\data\\report.pdf", gen);
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
