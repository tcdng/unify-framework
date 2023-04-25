/*
 * Copyright 2018-2023 The Code Department.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.FrequencyUnit;
import com.tcdng.unify.core.constant.PageSizeType;
import com.tcdng.unify.core.data.AbstractRoundRobin;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.DataSourceDialect;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.report.AbstractReportServer;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportFilter;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.report.ReportLayoutType;
import com.tcdng.unify.core.report.ReportPageProperties;
import com.tcdng.unify.core.report.ReportTableJoin;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.jasperreports.JasperReportsApplicationComponents;
import com.tcdng.unify.jasperreports.JasperReportsPropertyConstants;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
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
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRConcurrentSwapFile;
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

/**
 * Implementation of a report server using JasperReports.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = JasperReportsApplicationComponents.JASPERREPORTS_SERVER, description = "Jasper Reports Server")
public class JasperReportsServer extends AbstractReportServer {

	@SuppressWarnings("serial")
	private static final Map<PageSizeType, DPIPageDimension> PAGESIZE_TO_DPI = Collections
			.unmodifiableMap(new EnumMap<PageSizeType, DPIPageDimension>(PageSizeType.class) {
				{
					put(PageSizeType.A3, new DPIPageDimension(842, 1191));
					put(PageSizeType.A4, new DPIPageDimension(595, 842));
					put(PageSizeType.A5, new DPIPageDimension(420, 595));
					put(PageSizeType.B4, new DPIPageDimension(709, 1001));
					put(PageSizeType.B5, new DPIPageDimension(499, 709));
					put(PageSizeType.JIS_B4, new DPIPageDimension(709, 1001));
					put(PageSizeType.JIS_B5, new DPIPageDimension(499, 709));
					put(PageSizeType.LEGAL, new DPIPageDimension(612, 1009));
					put(PageSizeType.LETTER, new DPIPageDimension(612, 791));
				}
			});

	private static final String SWAP_FILE_FOLDER = "/jasperreports/swap";

	private static final int MIN_NUMBEROFSWAPFILES = 1;

	private static final int MIN_CACHEDPAGESPERVIRTUALIZER = 64;

	private static final int MAX_NUMBEROFSWAPFILES = 16;

	private static final int MAX_CACHEDPAGESPERVIRTUALIZER = 512;

	@Configurable("20")
	private int reportExpirationPeriod;

	@Configurable
	private boolean logDebug;

	private boolean fileVirtualization;

	private JRSwapFileVirtualizerRoundRobin virtualizers;

	private JasperReportsCache jasperReportsCache;

	public void setJasperReportCache(JasperReportsCache jasperReportsCache) {
		this.jasperReportsCache = jasperReportsCache;
	}

	public final void setReportExpirationPeriod(int reportExpirationPeriod) {
		this.reportExpirationPeriod = reportExpirationPeriod;
	}

	public final void setLogDebug(boolean logDebug) {
		this.logDebug = logDebug;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		super.onInitialize();
		jasperReportsCache = (JasperReportsCache) getComponent("jasperreports-cache");
		registerReportLayoutManager(ReportLayoutType.COLUMNAR,
				(JasperReportsLayoutManager) getComponent("jasperreports-columnarlayoutmanager"));
		registerReportLayoutManager(ReportLayoutType.TABULAR_IMAGESONLY,
				(JasperReportsLayoutManager) getComponent("jasperreports-tabularimagesonlylayoutmanager"));
		registerReportLayoutManager(ReportLayoutType.TABULAR_THUMBIMAGESONLY,
				(JasperReportsLayoutManager) getComponent("jasperreports-tabularthumbimagesonlylayoutmanager"));
		registerReportLayoutManager(ReportLayoutType.TABULAR,
				(JasperReportsLayoutManager) getComponent("jasperreports-tabularlayoutmanager"));
		registerReportLayoutManager(ReportLayoutType.SINGLECOLUMN_EMBEDDED_HTML,
				(JasperReportsLayoutManager) getComponent("jasperreports-singlecolumnhtmllayoutmanager"));
		registerReportLayoutManager(ReportLayoutType.PLACEMENT_PDF,
				(JasperReportsLayoutManager) getComponent("jasperreports-placementlayoutmanager"));

		if (!logDebug) {
			Logger.getLogger("net.sf.jasperreports").setLevel((Level) Level.ERROR);
			Logger.getLogger("org.apache.commons.beanutils").setLevel((Level) Level.ERROR);
			Logger.getLogger("org.apache.commons.digester").setLevel((Level) Level.ERROR);
		}

		fileVirtualization = getContainerSetting(boolean.class,
				JasperReportsPropertyConstants.JASPERREPORTS_FILEVIRTUALIZATION);
		if (fileVirtualization) {
			logDebug("File virtualization enabled.");
			final String swapFileFolder = getWorkingPathFilename(SWAP_FILE_FOLDER);
			final int noOfSwapFiles = getContainerSetting(int.class,
					JasperReportsPropertyConstants.JASPERREPORTS_FILEVIRTUALIZATION_NUMBEROFSWAPFILES,
					MIN_NUMBEROFSWAPFILES);
			final int actNoOfSwapFiles = noOfSwapFiles > 0
					? (noOfSwapFiles > MAX_NUMBEROFSWAPFILES ? MAX_NUMBEROFSWAPFILES : noOfSwapFiles)
					: MIN_NUMBEROFSWAPFILES;
			final int cachedPagesPerVirtualizer = getContainerSetting(int.class,
					JasperReportsPropertyConstants.JASPERREPORTS_FILEVIRTUALIZATION_CACHEDPAGESPERVIRTUALIZER,
					MIN_CACHEDPAGESPERVIRTUALIZER);
			final int actCachedPagesPerVirtualizer = cachedPagesPerVirtualizer > 0
					? (cachedPagesPerVirtualizer > MAX_CACHEDPAGESPERVIRTUALIZER ? MAX_CACHEDPAGESPERVIRTUALIZER
							: cachedPagesPerVirtualizer)
					: MIN_CACHEDPAGESPERVIRTUALIZER;

			logDebug("File virtualization using swap file folder [{0}]...", swapFileFolder);
			IOUtils.ensureDirectoryExists(swapFileFolder);
			IOUtils.deleteDirectoryContents(swapFileFolder);

			logDebug("File virtualization using [{0}] swap files and [{1}] cached pages per virtualizer...",
					actNoOfSwapFiles, actCachedPagesPerVirtualizer);
			List<JRSwapFileVirtualizer> virtualizerList = new ArrayList<JRSwapFileVirtualizer>();
			for (int i = 0; i < actNoOfSwapFiles; i++) {
				JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(actCachedPagesPerVirtualizer,
						new JRConcurrentSwapFile(swapFileFolder, 2048, 1024));
				virtualizerList.add(virtualizer);
			}

			virtualizers = new JRSwapFileVirtualizerRoundRobin(virtualizerList);
		}
	}

	@Override
	protected void doGenerateReport(Report report, OutputStream outputStream) throws UnifyException {
		if (report.isWorkbookXLS()) {
			try {
				Workbook workbook = (Workbook) report.getCustomObject();
				workbook.write(outputStream);
				workbook.close();
				return;
			} catch (IOException e) {
				throwOperationErrorException(e);
			}
		}
		
		DataSource dataSource = getDataSource(report);
		Connection connection = null;
		try {
			JasperReport jasperReport = null;
			if (report.isGenerated()) {
				jasperReport = generateJasperReport(report);
			} else {
				jasperReport = getCachedJasperReport(report.getTemplate());
			}

			JasperPrint jasperPrint = null;
			Collection<?> content = report.getBeanCollection();
			Map<String, Object> parameters = report.getParameters();
			if (fileVirtualization) {
				JRSwapFileVirtualizer virtualizer = virtualizers.next();
				parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			}
			
			if (report.isWithBeanCollection()) {
				JRBeanCollectionDataSource jrBeanDataSource = new JRBeanCollectionDataSource(content);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanDataSource);
			} else if (report.isEmbeddedHtml()) {
				JRBeanCollectionDataSource jrBeanDataSource = new JRBeanCollectionDataSource(report.getEmbeddedHtmls());
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanDataSource);
			} else {
				connection = (Connection) dataSource.getConnection();
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
			}

			ReportFormat reportFormat = report.isPlacements() ? ReportFormat.PDF : report.getFormat();
			Exporter<ExporterInput, ?, ?, ?> exporter = getExporter(reportFormat, outputStream);
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
			JasperDesign jasperDesign = null;
			if (report.isWithTemplate()) {
				inputStream = IOUtils.openFileResourceInputStream(report.getTemplate(),
						getUnifyComponentContext().getWorkingPath());

				jasperDesign = JRXmlLoader.load(inputStream);
				if (ReportFormat.XLS.equals(report.getFormat()) || ReportFormat.XLSX.equals(report.getFormat())) {
					jasperDesign.setProperty("net.sf.jasperreports.export.xls.detect.cell.type", "true");
				}

				jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
			} else {
				jasperDesign = new JasperDesign();
				jasperDesign.setName(report.getTitle());
				final ReportPageProperties properties = report.getPageProperties();
				logDebug("Generating report using properties {0}...", properties);
				final PageSizeType size = properties.getSize();
				final int pageWidth = size.isCustom() ? report.getPageProperties().getPageWidth()
						: (properties.isLandscape() ? PAGESIZE_TO_DPI.get(size).getHeight()
								: PAGESIZE_TO_DPI.get(size).getWidth());
				jasperDesign.setLeftMargin(properties.getMarginLeft());
				jasperDesign.setRightMargin(properties.getMarginRight());
				if (pageWidth > 0) {
					jasperDesign.setPageWidth(pageWidth);
					jasperDesign
							.setColumnWidth(pageWidth - jasperDesign.getLeftMargin() - jasperDesign.getRightMargin());
				}

				final int pageHeight = size.isCustom() ? report.getPageProperties().getPageHeight()
						: (properties.isLandscape() ? PAGESIZE_TO_DPI.get(size).getWidth()
								: PAGESIZE_TO_DPI.get(size).getHeight());
				jasperDesign.setTopMargin(properties.getMarginTop());
				jasperDesign.setBottomMargin(properties.getMarginBottom());
				if (pageHeight > 0) {
					jasperDesign.setPageHeight(pageHeight);
				}

				logDebug("Using resolved dimensions [pageWidth = {0}, pageHeight = {1}]...", pageWidth, pageHeight);
			}

			if (!report.isWithBeanCollection() && !report.isEmbeddedHtml()) {
				String query = null;
				if (report.isQuery()) {
					query = report.getQuery();
				} else {
					DataSourceDialect dataSourceDialect = getDataSource(report).getDialect();
					NativeQuery.Builder nqb = NativeQuery.newBuilder();
					nqb.tableName(report.getTable().getName());
					for (ReportColumn rc : report.getColumns()) {
						nqb.addColumn(rc.getTable(), rc.getName());
						if (rc.getOrder() != null) {
							nqb.addOrderBy(rc.getOrder(), rc.getName());
						} else if (rc.isGroup()) {
							nqb.addOrderBy(rc.getName());
						}
					}

					for (ReportTableJoin rj : report.getJoins()) {
						nqb.addJoin(rj.getType(), rj.getTableA(), rj.getColumnA(), rj.getTableB(), rj.getColumnB());
					}

					ReportFilter rootFilter = report.getFilter();
					if (rootFilter != null && !(rootFilter.isCompound() && !rootFilter.isSubFilters())) {
						buildNativeQueryFilters(nqb, rootFilter);
					}

					query = dataSourceDialect.generateNativeQuery(nqb.build());
				}

				logDebug("Setting jasper reports query [{0}]...", query);
				JRDesignQuery jRDesignQuery = new JRDesignQuery();
				jRDesignQuery.setText(query);
				jasperDesign.setQuery(jRDesignQuery);
			}

			JasperReportsLayoutManager jasperReportsLayoutManager = (JasperReportsLayoutManager) getReportLayoutManager(
					report.getLayout());
			report.setReportTheme(getReportTheme(report.getTheme()));
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
		// sxmrc.setDetectCellType(true);
		return sxmrc;
	}

	private void buildNativeQueryFilters(NativeQuery.Builder nqb, ReportFilter compoundFilter) throws UnifyException {
		nqb.beginCompoundFilter(compoundFilter.getOp());
		for (ReportFilter subFilter : compoundFilter.getSubFilterList()) {
			if (subFilter.isCompound()) {
				buildNativeQueryFilters(nqb, subFilter);
			} else {
				nqb.addSimpleFilter(subFilter.getOp(), subFilter.getTableName(), subFilter.getColumnName(),
						subFilter.getParam1(), subFilter.getParam2());
			}
		}
		nqb.endCompoundFilter();
	}

	private static class JRSwapFileVirtualizerRoundRobin extends AbstractRoundRobin<JRSwapFileVirtualizer> {

		public JRSwapFileVirtualizerRoundRobin(List<JRSwapFileVirtualizer> virtualizers) {
			super(virtualizers);
		}
	}

	private static class DPIPageDimension {

		final int width;

		final int height;

		public DPIPageDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

}
