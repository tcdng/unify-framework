/*
 * Copyright detailHeight14 The Code Department
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Used for tabular report layout.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("jasperreports-tabularlayoutmanager")
public class JasperReportsTabularLayoutManager extends AbstractJasperReportsLayoutManager {

	@Override
	protected void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
			throws UnifyException {
		List<ReportColumn> reportColumnList = report.getColumns();
		boolean isListFormat = this.isListFormat(report.getFormat());
		int actualColumnWidth = jasperDesign.getColumnWidth();
		int columnHeaderHeight = report.getColumnHeaderHeight();
		int detailHeight = report.getDetailHeight();

		// Organize layout
		List<ReportColumn> groupingColumnList = new ArrayList<ReportColumn>();
		List<ReportColumn> detailColumnList = new ArrayList<ReportColumn>();
		List<ReportColumn> summationColumnList = new ArrayList<ReportColumn>();
		Map<ReportColumn, JRDesignElement> detailJRElementMap = new HashMap<ReportColumn, JRDesignElement>();

		int reportWidth = 0;
		for (ReportColumn reportColumn : reportColumnList) {
			if (reportColumn.isGroup()) {
				groupingColumnList.add(reportColumn);
			} else {
				detailColumnList.add(reportColumn);
				if (reportColumn.isSum() && DataUtils.isNumberType(reportColumn.getTypeName())) {
					summationColumnList.add(reportColumn);
				}

				JRDesignElement jRDesignElement = this.newColumnJRDesignElement(jasperDesign, columnStyles,
						reportColumn, isListFormat);
				jRDesignElement.setX(reportWidth);
				jRDesignElement.setY(2);
				reportWidth += jRDesignElement.getWidth();
				detailJRElementMap.put(reportColumn, jRDesignElement);
			}
		}

		// Fit details to width
		JRDesignElement lastJRDesignElement = null;
		int x = 0;
		for (ReportColumn reportColumn : detailColumnList) {
			JRDesignElement jRDesignElement = detailJRElementMap.get(reportColumn);
			int newWidth = (jRDesignElement.getWidth() * actualColumnWidth) / reportWidth;
			jRDesignElement.setWidth(newWidth);
			jRDesignElement.setX(x);
			lastJRDesignElement = jRDesignElement;
			x += newWidth;
		}

		if (x < actualColumnWidth && lastJRDesignElement != null) {
			lastJRDesignElement.setWidth(lastJRDesignElement.getWidth() + actualColumnWidth - x);
		}

		// Prepare column header (or title header in case of list layout)
		if (report.isPrintColumnNames()) {
			JRDesignBand columnHeaderBand = new JRDesignBand();
			columnHeaderBand.setHeight(columnHeaderHeight);

			JRDesignRectangle jRDesignRectangle = this.newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth,
					columnHeaderHeight, new Color(0xC0, 0xC0, 0xC0));
			jRDesignRectangle.getLinePen().setLineWidth(0);
			jRDesignRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
			columnHeaderBand.addElement(jRDesignRectangle);

			Color colHeaderColor = Color.WHITE;
			if (isListFormat) {
				colHeaderColor = Color.BLACK;
			}

			for (ReportColumn reportColumn : detailColumnList) {
				JRDesignElement colHeaderJRDesignElement = this.newTitleJRDesignStaticText(columnStyles, colHeaderColor,
						HorizontalAlignEnum.LEFT, reportColumn);
				JRDesignElement jRDesignElement = detailJRElementMap.get(reportColumn);
				colHeaderJRDesignElement.setX(jRDesignElement.getX());
				colHeaderJRDesignElement.setY(2);
				colHeaderJRDesignElement.setWidth(jRDesignElement.getWidth());
				colHeaderJRDesignElement.setHeight(columnHeaderHeight - (4));

				if (isListFormat) {
					colHeaderJRDesignElement.addPropertyExpression(
							this.newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
				}
				columnHeaderBand.addElement(colHeaderJRDesignElement);
			}

			if (isListFormat) {
				jasperDesign.setTitle(columnHeaderBand);
			} else {
				jasperDesign.setColumnHeader(columnHeaderBand);
			}
		}

		// Prepare detail band
		JRDesignBand detailBand = new JRDesignBand();
		detailBand.setHeight(detailHeight);

		if (report.isShadeOddRows()) {
			JRDesignRectangle jRDesignRectangle = this.newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth,
					detailHeight, new Color(0xEE, 0xEE, 0xEE));
			jRDesignRectangle.getLinePen().setLineWidth(0);
			jRDesignRectangle.setPrintWhenExpression(this.getOnOddJRDesignExpression());
			jRDesignRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
			detailBand.addElement(jRDesignRectangle);
		}

		if (report.isUnderlineRows()) {
			JRDesignLine jRDesignLine = this.newJRDesignLine(0, detailHeight - 1, actualColumnWidth, 0, Color.BLACK);
			jRDesignLine.getLinePen().setLineWidth(0.5f);
			jRDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
			detailBand.addElement(jRDesignLine);
		}

		for (ReportColumn reportColumn : detailColumnList) {
			JRDesignElement jRDesignElement = detailJRElementMap.get(reportColumn);
			jRDesignElement.setHeight(detailHeight - (4));
			detailBand.addElement(jRDesignElement);
		}

		JRDesignSection detailJRDesignSection = ((JRDesignSection) jasperDesign.getDetailSection());
		int len = detailJRDesignSection.getBands().length;
		for (int i = 0; i < len; i++) { // Clear all template bands first
			detailJRDesignSection.removeBand(i);
		}
		detailJRDesignSection.addBand(detailBand); // Add new detail band

		// Prepare groups
		int groupHeaderX = 0;
		int groupCascade = 20;
		for (ReportColumn reportColumn : groupingColumnList) {
			JRDesignGroup jRDesignGroup = this.newJRDesignGroup(jasperDesign, reportColumn);

			JRDesignBand groupBand = new JRDesignBand();
			groupBand.setHeight(columnHeaderHeight);

			JRDesignRectangle grpJRDesignRectangle = this.newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth,
					columnHeaderHeight - 2, new Color(0xD0, 0xD0, 0xD0));
			grpJRDesignRectangle.getLinePen().setLineWidth(0);
			groupBand.addElement(grpJRDesignRectangle);

			// JRDesignElement jRDesignElement = this
			// .newColumnHeaderJRDesignElement(columnStyles, reportColumn);
			// jRDesignElement.setX(groupHeaderX);
			// jRDesignElement.setY(2);
			// jRDesignElement.setWidth(groupHeaderLegendWidth);
			// jRDesignElement.setHeight(columnHeaderHeight - (4));
			// groupBand.addElement(jRDesignElement);

			JRDesignElement jRDesignElement = this.newColumnJRDesignElement(jasperDesign, columnStyles, reportColumn,
					isListFormat);
			// jRDesignElement.setX(groupHeaderX + groupHeaderLegendWidth);
			jRDesignElement.setX(groupHeaderX);
			jRDesignElement.setY(2);
			jRDesignElement.setWidth(actualColumnWidth - jRDesignElement.getX());
			jRDesignElement.setHeight(columnHeaderHeight - (4));
			groupBand.addElement(jRDesignElement);

			((JRDesignSection) jRDesignGroup.getGroupHeaderSection()).addBand(groupBand);

			if (!summationColumnList.isEmpty()) {
				groupBand = new JRDesignBand();
				groupBand.setHeight(columnHeaderHeight);
				grpJRDesignRectangle = this.newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth,
						columnHeaderHeight - 2, new Color(0xD0, 0xD0, 0xD0));
				grpJRDesignRectangle.getLinePen().setLineWidth(0);
				groupBand.addElement(grpJRDesignRectangle);

				for (ReportColumn sumReportColumn : summationColumnList) {
					JRDesignVariable sumJRDesignVariable = this.newGroupSumJRDesignVariable(jasperDesign, jRDesignGroup,
							sumReportColumn);
					jRDesignElement = detailJRElementMap.get(sumReportColumn);

					JRDesignTextField sumJRDesignElement = (JRDesignTextField) this
							.newColumnJRDesignElement(jasperDesign, columnStyles, sumReportColumn, isListFormat);

					sumJRDesignElement.setX(jRDesignElement.getX());
					sumJRDesignElement.setY(jRDesignElement.getY());
					sumJRDesignElement.setWidth(jRDesignElement.getWidth());
					sumJRDesignElement.setHeight(columnHeaderHeight - (4));
					sumJRDesignElement
							.setExpression(this.newJRDesignExpression("$V{" + sumJRDesignVariable.getName() + "}"));
					groupBand.addElement(sumJRDesignElement);
				}
				((JRDesignSection) jRDesignGroup.getGroupFooterSection()).addBand(groupBand);
			}

			groupHeaderX += groupCascade;
		}

		// Add summary if necessary
		if (!summationColumnList.isEmpty()) {
			JRDesignBand summaryBand = new JRDesignBand();
			summaryBand.setHeight(columnHeaderHeight);

			JRDesignRectangle jRDesignRectangle = this.newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth,
					columnHeaderHeight, new Color(0xC0, 0xC0, 0xC0));
			summaryBand.addElement(jRDesignRectangle);

			for (ReportColumn sumReportColumn : summationColumnList) {
				JRDesignVariable sumJRDesignVariable = this.newReportSumJRDesignVariable(jasperDesign, sumReportColumn);
				JRDesignElement jRDesignElement = detailJRElementMap.get(sumReportColumn);

				JRDesignTextField sumJRDesignElement = (JRDesignTextField) this.newColumnJRDesignElement(jasperDesign,
						columnStyles, sumReportColumn, isListFormat);

				sumJRDesignElement.setX(jRDesignElement.getX());
				sumJRDesignElement.setY(jRDesignElement.getY());
				sumJRDesignElement.setWidth(jRDesignElement.getWidth());
				sumJRDesignElement.setHeight(columnHeaderHeight - (4));
				sumJRDesignElement
						.setExpression(this.newJRDesignExpression("$V{" + sumJRDesignVariable.getName() + "}"));
				summaryBand.addElement(sumJRDesignElement);
			}
			jasperDesign.setSummary(summaryBand);
		}
	}
}
