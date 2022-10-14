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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportEmbeddedHtml;
import com.tcdng.unify.core.report.ReportTheme;
import com.tcdng.unify.core.report.ReportTheme.ThemeColors;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

/**
 * Single column embedded HTML layout
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("jasperreports-singlecolumnhtmllayoutmanager")
public class JasperReportsSingleColumnHtmlLayoutManager extends AbstractJasperReportsLayoutManager {

	@Override
	protected void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
			throws UnifyException {
		ReportTheme theme = report.getReportTheme();
		ThemeColors detailColors = theme.getDetailTheme();
		final int width = jasperDesign.getPageWidth() - jasperDesign.getLeftMargin() - jasperDesign.getRightMargin();
		List<JRDesignElement> htmlJRElements = new ArrayList<JRDesignElement>();
		for (ReportEmbeddedHtml html : report.getEmbeddedHtmls()) {
			// Register HTML as parameter
			addReportParameter(jasperDesign, PARAMETER_PREFIX_HTML + html.getName(), html.getHtml());
			JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, detailColors,
					columnStyles.getNormalStyle(), html, width);
			htmlJRElements.add(jRDesignElement);
		}

		// Construct detail band
		clearDetailSection(jasperDesign);
		for (JRDesignElement htmlJRElement : htmlJRElements) {
			JRDesignBand detailBand = new JRDesignBand();
			// detailBand.setHeight(detailHeight);
			detailBand.addElement(htmlJRElement);
			addDetailBand(jasperDesign, detailBand);
		}

	}

}
