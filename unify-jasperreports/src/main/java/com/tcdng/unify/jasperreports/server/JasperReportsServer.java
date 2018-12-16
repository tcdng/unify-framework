/*
 * Copyright 2018 The Code Department
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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.EnumMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleCsvReportConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleRtfReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsMetadataReportConfiguration;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.DataSourceDialect;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.report.AbstractReportServer;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportColumnFilter;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.report.ReportTableJoin;
import com.tcdng.unify.core.report.ReportLayout;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.jasperreports.JasperReportsApplicationComponents;

/**
 * Implementation of a report server using JasperReports.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = JasperReportsApplicationComponents.JASPERREPORTS_SERVER, description = "Jasper Reports Server")
public class JasperReportsServer extends AbstractReportServer {

    private JasperReportsCache jasperReportsCache;

    @Configurable("20")
    private int reportExpirationPeriod;

    private EnumMap<ReportLayout, JasperReportsLayoutManager> layoutManagers;

    public JasperReportsServer() {
        layoutManagers = new EnumMap<ReportLayout, JasperReportsLayoutManager>(ReportLayout.class);
    }

    public void setJasperReportCache(JasperReportsCache jasperReportsCache) {
        this.jasperReportsCache = jasperReportsCache;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();
        jasperReportsCache = (JasperReportsCache) getComponent("jasperreports-cache");
        layoutManagers.put(ReportLayout.TABULAR,
                (JasperReportsLayoutManager) getComponent("jasperreports-tabularlayoutmanager"));
        layoutManagers.put(ReportLayout.COLUMNAR,
                (JasperReportsLayoutManager) getComponent("jasperreports-columnarlayoutmanager"));
    }

    @Override
    protected void doGenerateReport(Report report, OutputStream outputStream) throws UnifyException {
        DataSource dataSource = getDataSource(report);
        Connection connection = null;
        try {
            JasperReport jasperReport = null;
            if (report.isDynamic()) {
                jasperReport = generateJasperReport(report);
            } else {
                jasperReport = getCachedJasperReport(report.getTemplate());
            }

            JasperPrint jasperPrint = null;
            Collection<?> content = report.getBeanCollection();
            if (content != null) {
                JRBeanCollectionDataSource jrBeanDataSource = new JRBeanCollectionDataSource(content);
                jasperPrint = JasperFillManager.fillReport(jasperReport, report.getReportParameters().getParameters(),
                        jrBeanDataSource);
            } else {
                connection = (Connection) dataSource.getConnection();
                jasperPrint = JasperFillManager.fillReport(jasperReport, report.getReportParameters().getParameters(),
                        connection);
            }

            Exporter<ExporterInput, ?, ?, ?> exporter = getExporter(report.getFormat(), outputStream);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            if (connection != null) {
                dataSource.restoreConnection(connection);
            }
        }
    }

    private JasperReport getCachedJasperReport(String template) throws UnifyException {
        InputStream inputStream = null;
        try {
            JasperReport jasperReport = jasperReportsCache.get(template);
            if (jasperReport == null) {
                inputStream = IOUtils.openFileResourceInputStream(template,
                        getUnifyComponentContext().getWorkingPath());
                jasperReport = JasperCompileManager.compileReport(inputStream);
                jasperReportsCache.put(template, jasperReport,
                        CalendarUtils.getMilliSecondsByFrequency(FrequencyUnit.MINUTE, reportExpirationPeriod));
            }
            return jasperReport;
        } catch (JRException e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(inputStream);
        }
        return null;
    }

    private JasperReport generateJasperReport(Report report) throws UnifyException {
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.openFileResourceInputStream(report.getTemplate(),
                    getUnifyComponentContext().getWorkingPath());

            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
            if (!report.isBeanCollection()) {
                String query = null;
                if (report.isQuery()) {
                    query = report.getQuery();
                } else {
                    DataSourceDialect dataSourceDialect = getDataSource(report).getDialect();
                    NativeQuery nativeQuery = new NativeQuery();
                    nativeQuery.tableName(report.getTable().getName());
                    for (ReportColumn rc : report.getColumns()) {
                        nativeQuery.addColumn(rc.getTable(), rc.getName());
                    }

                    for (ReportTableJoin rj : report.getJoins()) {
                        nativeQuery.addJoin(rj.getType(), rj.getTableA(), rj.getColumnA(), rj.getTableB(),
                                rj.getColumnB());
                    }

                    for (ReportColumnFilter rf : report.getFilters()) {
                        nativeQuery.addFilter(rf.getOp(), rf.getTableName(), rf.getColumnName(), rf.getParam1(),
                                rf.getParam2());
                    }
                    query = dataSourceDialect.generateNativeQuery(nativeQuery);
                }

                logDebug("Setting jasper reports query [{0}]...", query);
                JRDesignQuery jRDesignQuery = new JRDesignQuery();
                jRDesignQuery.setText(query);
                jasperDesign.setQuery(jRDesignQuery);
            }

            JasperReportsLayoutManager jasperReportsLayoutManager = layoutManagers.get(report.getLayout());
            jasperReportsLayoutManager.applyLayout(jasperDesign, report);
            return JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(inputStream);
        }
        return null;
    }

    private Exporter<ExporterInput, ?, ?, ?> getExporter(ReportFormat reportFormatType, OutputStream outputStream)
            throws Exception {
        switch (reportFormatType) {
        case CSV:
            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setConfiguration(new SimpleCsvReportConfiguration());
            csvExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            return csvExporter;
        case DOC:
            JRRtfExporter docExporter = new JRRtfExporter();
            docExporter.setConfiguration(new SimpleRtfReportConfiguration());
            docExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            return docExporter;
        case DOCX:
            JRDocxExporter docxExporter = new JRDocxExporter();
            docxExporter.setConfiguration(new SimpleDocxReportConfiguration());
            docxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            return docxExporter;
        case XLS:
            JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setConfiguration(getSimpleXlsMetadataReportConfiguration());
            xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            return xlsExporter;
        case XLSX:
            JRXlsExporter xlsxExporter = new JRXlsExporter();
            xlsxExporter.setConfiguration(getSimpleXlsMetadataReportConfiguration());
            xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            return xlsxExporter;
        case XML:
            JRXmlExporter xmlExporter = new JRXmlExporter();
            xmlExporter.setExporterOutput(new SimpleXmlExporterOutput(outputStream));
            return xmlExporter;
        case PDF:
        default:
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setConfiguration(new SimplePdfExporterConfiguration());
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            return pdfExporter;
        }
    }

    private SimpleXlsMetadataReportConfiguration getSimpleXlsMetadataReportConfiguration() {
        SimpleXlsMetadataReportConfiguration sxmrc = new SimpleXlsMetadataReportConfiguration();
        sxmrc.setOnePagePerSheet(false);
        sxmrc.setRemoveEmptySpaceBetweenColumns(true);
        sxmrc.setRemoveEmptySpaceBetweenRows(true);
        sxmrc.setWhitePageBackground(false);
        sxmrc.setDetectCellType(true);
        return sxmrc;
    }
}
