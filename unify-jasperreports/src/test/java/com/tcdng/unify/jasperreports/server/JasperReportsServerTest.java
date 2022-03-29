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
import com.tcdng.unify.core.report.ReportServer;
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
//        IOUtils.writeToFile("c:\\data\\report.pdf", gen);
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
