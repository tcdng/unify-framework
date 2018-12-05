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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Used for columnar report layout.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("jasperreports-columnarlayoutmanager")
public class JasperReportsColumnarLayoutManager extends AbstractJasperReportsLayoutManager {

	@Override
	protected void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
			throws UnifyException {
		List<ReportColumn> reportColumnList = report.getColumns();
		boolean isListFormat = isListFormat(report.getFormat());
		boolean isPrintColumnNames = report.isPrintColumnNames();
		int actualColumnWidth = jasperDesign.getColumnWidth();
		int columnHeaderHeight = report.getColumnHeaderHeight();
		int detailHeight = report.getDetailHeight();

		// Organise layout
		List<ReportColumn> groupingColumnList = new ArrayList<ReportColumn>();
		List<JRDesignElement> detailTitleElementList = new ArrayList<JRDesignElement>();
		List<JRDesignElement> detailElementList = new ArrayList<JRDesignElement>();

		int calcDetailHeight = 0;
		int detailColumnWidth = (actualColumnWidth * 2) / 3;
		int titleWidth = (detailColumnWidth * 2) / 5;
		int actualDetailWidth = (detailColumnWidth * 3) / 5;
		for (ReportColumn reportColumn : reportColumnList) {
			if (reportColumn.isGroup()) {
				groupingColumnList.add(reportColumn);
			} else {
				if (reportColumn.isSum() && DataUtils.isNumberType(reportColumn.getTypeName())) {
				}

				JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, columnStyles,
						reportColumn, isListFormat);
				jRDesignElement.setX(titleWidth);
				jRDesignElement.setY(calcDetailHeight + 2);
				jRDesignElement.setWidth(actualDetailWidth);
				jRDesignElement.setHeight(detailHeight - (22));
				detailElementList.add(jRDesignElement);

				if (isPrintColumnNames) {
					JRDesignElement colHeaderJRDesignElement = newTitleJRDesignStaticText(columnStyles,
							Color.WHITE, HorizontalAlignEnum.LEFT, reportColumn);
					colHeaderJRDesignElement.setBackcolor(new Color(0xD0, 0xD0, 0xD0));
					colHeaderJRDesignElement.setX(0);
					colHeaderJRDesignElement.setY(jRDesignElement.getY());
					colHeaderJRDesignElement.setWidth(titleWidth);
					colHeaderJRDesignElement.setHeight(jRDesignElement.getHeight());

					if (isListFormat) {
						colHeaderJRDesignElement.addPropertyExpression(
								newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
					}
					detailTitleElementList.add(colHeaderJRDesignElement);
				}
				calcDetailHeight += detailHeight;
			}
		}

		// Prepare detail band
		JRDesignBand detailBand = new JRDesignBand();
		detailBand.setHeight(calcDetailHeight);

		if (report.isShadeOddRows()) {
			JRDesignRectangle jRDesignRectangle = newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth,
					calcDetailHeight, new Color(0xEE, 0xEE, 0xEE));
			jRDesignRectangle.getLinePen().setLineWidth(0);
			jRDesignRectangle.setPrintWhenExpression(getOnOddJRDesignExpression());
			detailBand.addElement(jRDesignRectangle);
		}

		if (report.isUnderlineRows()) {
			JRDesignLine jRDesignLine = newJRDesignLine(0, calcDetailHeight - 1, actualColumnWidth, 0,
					Color.BLACK);
			detailBand.addElement(jRDesignLine);
		}

		for (JRDesignElement jRDesignElement : detailTitleElementList) {
			detailBand.addElement(jRDesignElement);
		}

		for (JRDesignElement jRDesignElement : detailElementList) {
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
			JRDesignGroup jRDesignGroup = newJRDesignGroup(jasperDesign, reportColumn);

			JRDesignBand groupBand = new JRDesignBand();
			groupBand.setHeight(columnHeaderHeight);

			JRDesignRectangle grpJRDesignRectangle = newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth,
					columnHeaderHeight - 2, new Color(0xD0, 0xD0, 0xD0));
			grpJRDesignRectangle.getLinePen().setLineWidth(0);
			groupBand.addElement(grpJRDesignRectangle);

			JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, columnStyles, reportColumn,
					isListFormat);
			jRDesignElement.setX(groupHeaderX);
			jRDesignElement.setY(2);
			jRDesignElement.setWidth(actualColumnWidth - jRDesignElement.getX());
			jRDesignElement.setHeight(columnHeaderHeight - (4));
			groupBand.addElement(jRDesignElement);

			((JRDesignSection) jRDesignGroup.getGroupHeaderSection()).addBand(groupBand);

			groupHeaderX += groupCascade;
		}
	}
}
