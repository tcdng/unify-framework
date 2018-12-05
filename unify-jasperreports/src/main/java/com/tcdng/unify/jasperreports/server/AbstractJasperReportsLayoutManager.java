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
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignPropertyExpression;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract jasper report layout manager that provides methods for easy
 * manipulation of a jasper design object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractJasperReportsLayoutManager extends AbstractUnifyComponent
		implements JasperReportsLayoutManager {

	private FactoryMap<String, ColumnStyles> columnStylesMap;

	private Map<HAlignType, HorizontalAlignEnum> horizontalAlignmentMap;

	public AbstractJasperReportsLayoutManager() {
		columnStylesMap = new FactoryMap<String, ColumnStyles>() {
			@Override
			protected ColumnStyles create(String key, Object... params) throws Exception {
				return new ColumnStyles((String) params[0], (Integer) params[1]);
			}
		};

		horizontalAlignmentMap = new HashMap<HAlignType, HorizontalAlignEnum>();
		horizontalAlignmentMap.put(HAlignType.LEFT, HorizontalAlignEnum.LEFT);
		horizontalAlignmentMap.put(HAlignType.CENTER, HorizontalAlignEnum.CENTER);
		horizontalAlignmentMap.put(HAlignType.RIGHT, HorizontalAlignEnum.RIGHT);
		horizontalAlignmentMap.put(HAlignType.JUSTIFIED, HorizontalAlignEnum.JUSTIFIED);
	}

	@Override
	public void applyLayout(JasperDesign jasperDesign, Report report) throws UnifyException {
		try {
			if (isListFormat(report.getFormat())) {
				JRDesignBand blankBand = new JRDesignBand();
				jasperDesign.setTitle(blankBand);
				jasperDesign.setPageHeader(blankBand);
				jasperDesign.setPageFooter(blankBand);
				jasperDesign.setColumnHeader(blankBand);
				jasperDesign.setColumnFooter(blankBand);
				jasperDesign.setSummary(blankBand);
			}

			ColumnStyles columnStyles = getReportColumnStyles(report);
			jasperDesign.addStyle(columnStyles.getParentStyle());
			jasperDesign.addStyle(columnStyles.getNormalStyle());
			jasperDesign.addStyle(columnStyles.getBoldStyle());

			boolean isQuery = !StringUtils.isBlank(report.getQuery());
			for (ReportColumn reportColumn : report.getColumns()) {
				jasperDesign.addField(newJRDesignField(reportColumn, isQuery));
			}

			jasperDesign.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");

			doApplyLayout(jasperDesign, columnStyles, report);
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
			throws UnifyException;

	protected HorizontalAlignEnum getHorizontalAlign(HAlignType hAlignType) {
		return horizontalAlignmentMap.get(hAlignType);
	}

	protected JRDesignGroup newJRDesignGroup(ColumnStyles columnStyles, Report report, ReportColumn reportColumn,
			int reportWidth, int columnWidth, boolean footer) throws UnifyException {
		JRDesignGroup jRDesignGroup = new JRDesignGroup();
		jRDesignGroup.setName(reportColumn.getTitle() + " Group");
		jRDesignGroup.setExpression(newJRDesignExpression(reportColumn));

		JRDesignBand jRDesignBand = new JRDesignBand();
		int groupBandHeight = report.getColumnHeaderHeight();
		jRDesignBand.setHeight(groupBandHeight);
		JRDesignTextField jRDesignTextField = new JRDesignTextField();
		jRDesignTextField.setX(0);
		jRDesignTextField.setY(2);
		jRDesignTextField.setWidth(columnWidth);
		jRDesignTextField.setStyle(columnStyles.getBoldStyle());
		jRDesignTextField.setHeight(groupBandHeight - (22));
		jRDesignTextField.setBackcolor(new Color(0xC0, 0xC0, 0xC0));
		jRDesignTextField.setMode(ModeEnum.OPAQUE);
		jRDesignTextField.setHorizontalAlignment(getHorizontalAlign(reportColumn.getHorizontalAlignment()));
		jRDesignTextField.setExpression(newJRDesignExpression(reportColumn));
		jRDesignBand.addElement(jRDesignTextField);
		jRDesignBand.addElement(newJRDesignLine(0, groupBandHeight - 1, reportWidth, 0, Color.BLACK));
		((JRDesignSection) jRDesignGroup.getGroupHeaderSection()).addBand(jRDesignBand);

		if (footer) {
			jRDesignBand = new JRDesignBand();
			jRDesignBand.setHeight(groupBandHeight);
			jRDesignBand.addElement(newJRDesignLine(0, 0, reportWidth, 0, Color.BLACK));
			if (report.getGroupSummationLegend() != null) {
				JRDesignStaticText jRDesignStaticText = new JRDesignStaticText();
				jRDesignStaticText.setX(0);
				jRDesignStaticText.setY(2);
				jRDesignStaticText.setWidth(60);
				jRDesignStaticText.setHeight(groupBandHeight - (22));
				jRDesignStaticText.setStyle(columnStyles.getBoldStyle());
				jRDesignStaticText.setHorizontalAlignment(getHorizontalAlign(reportColumn.getHorizontalAlignment()));
				jRDesignStaticText.setText(report.getGroupSummationLegend());
				jRDesignBand.addElement(jRDesignStaticText);
			}
			((JRDesignSection) jRDesignGroup.getGroupFooterSection()).addBand(jRDesignBand);
		}
		return jRDesignGroup;
	}

	protected JRDesignElement newColumnJRDesignElement(JasperDesign jasperDesign, ColumnStyles columnStyles,
			ReportColumn reportColumn, boolean isListFormat) throws UnifyException {
		if ("byte[]".equals(reportColumn.getTypeName())) {
			JRDesignImage jrDesignImage = new JRDesignImage(jasperDesign);
			jrDesignImage.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
			jrDesignImage.setWidth(reportColumn.getWidthRatio());
			jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
			jrDesignImage.setExpression(newJRDesignExpression(reportColumn));
			jrDesignImage.setPrintWhenExpression(newNotNullJRDesignExpression(reportColumn));
			return jrDesignImage;
		}

		JRDesignTextField textField = new JRDesignTextField();
		textField.setWidth(reportColumn.getWidthRatio());
		textField.setStyle(columnStyles.getNormalStyle());
		textField.setHorizontalAlignment(getHorizontalAlign(reportColumn.getHorizontalAlignment()));
		textField.setExpression(newJRDesignExpression(reportColumn));
		textField.setBlankWhenNull(true);

		textField.addPropertyExpression(
				newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));

		if (!isListFormat) {
			textField.setStretchWithOverflow(true);
		}
		return textField;
	}

	protected JRDesignPropertyExpression newJRDesignPropertyExpression(String key, Object value) {
		JRDesignPropertyExpression propertyExpression = new JRDesignPropertyExpression();
		propertyExpression.setName(key);
		propertyExpression
				.setValueExpression(new JRDesignExpression("String.valueOf(\"" + String.valueOf(value) + "\")"));
		return propertyExpression;
	}

	protected JRDesignStaticText newTitleJRDesignStaticText(ColumnStyles columnStyles, Color foreColor,
			HorizontalAlignEnum horizontalAlignment, ReportColumn reportColumn) throws UnifyException {
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setForecolor(foreColor);
		staticText.setMode(ModeEnum.TRANSPARENT);
		staticText.setStyle(columnStyles.getBoldStyle());
		staticText.setText(reportColumn.getTitle());
		staticText.setHorizontalAlignment(horizontalAlignment);
		return staticText;
	}

	protected JRDesignRectangle newJRDesignRectangle(JasperDesign jasperDesign, int x, int y, int width, int height,
			Color color) throws UnifyException {
		JRDesignRectangle jRDesignRectangle = new JRDesignRectangle(jasperDesign);
		jRDesignRectangle.setX(x);
		jRDesignRectangle.setY(y);
		jRDesignRectangle.setWidth(width);
		jRDesignRectangle.setHeight(height);
		jRDesignRectangle.setBackcolor(color);
		return jRDesignRectangle;
	}

	protected JRDesignLine newJRDesignLine(int x, int y, int width, int height, Color color) throws UnifyException {
		JRDesignLine jRDesignLine = new JRDesignLine();
		jRDesignLine.setX(x);
		jRDesignLine.setY(y);
		jRDesignLine.setWidth(width);
		jRDesignLine.setHeight(height);
		jRDesignLine.setForecolor(color);
		return jRDesignLine;
	}

	protected JRDesignExpression newNotNullJRDesignExpression(ReportColumn reportColumn) {
		return newJRDesignExpression("new Boolean($F{" + reportColumn.getName() + "} != null)");
	}

	protected JRDesignExpression getOnOddJRDesignExpression() {
		return newJRDesignExpression("new Boolean($V{PAGE_COUNT}.intValue() % 2 > 0)");
	}

	protected JRDesignExpression newJRDesignExpression(String expression) {
		JRDesignExpression jRDesignExpression = new JRDesignExpression();
		jRDesignExpression.setText(expression);
		return jRDesignExpression;
	}

	protected JRDesignExpression newJRDesignExpression(ReportColumn reportColumn) throws UnifyException {
		JRDesignExpression expression = new JRDesignExpression();
		if ("byte[]".equals(reportColumn.getTypeName())) {
			expression.setText("new ByteArrayInputStream((byte[])$F{" + reportColumn.getName() + "})");
		} else {
			if (reportColumn.getFormatterUpl() != null) {
				expression.setText("com.tcdng.unify.core.report.ReportFormatUtils.format(\""
						+ reportColumn.getFormatterUpl() + "\", $F{" + reportColumn.getName() + "})");
			} else {
				expression.setText("$F{" + reportColumn.getName() + "}");
			}
		}
		return expression;
	}

	protected JRDesignGroup newJRDesignGroup(JasperDesign jasperDesign, ReportColumn reportColumn)
			throws UnifyException {
		try {
			JRDesignGroup jRDesignGroup = new JRDesignGroup();
			jRDesignGroup.setName(reportColumn.getTitle() + "_Group");
			jRDesignGroup.setExpression(newJRDesignExpression(reportColumn));
			jasperDesign.addGroup(jRDesignGroup);
			return jRDesignGroup;
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
		return null;
	}

	protected JRDesignVariable newGroupSumJRDesignVariable(JasperDesign jasperDesign, JRDesignGroup jRDesignGroup,
			ReportColumn reportColumn) throws UnifyException {
		try {
			JRDesignVariable jRDesignVariable = new JRDesignVariable();
			String name = jRDesignGroup.getName() + "_Sum_" + reportColumn.getName();
			jRDesignVariable.setName(name);
			jRDesignVariable.setValueClass(ReflectUtils.getClassForName(reportColumn.getTypeName()));
			jRDesignVariable.setResetType(ResetTypeEnum.GROUP);
			jRDesignVariable.setResetGroup(jRDesignGroup);
			jRDesignVariable.setCalculation(CalculationEnum.SUM);
			jRDesignVariable
					.setInitialValueExpression(newJRDesignExpression("new " + reportColumn.getTypeName() + "(0)"));
			jRDesignVariable.setExpression(newJRDesignExpression(
					"new " + reportColumn.getTypeName() + "($F{" + reportColumn.getName() + "})"));
			jasperDesign.addVariable(jRDesignVariable);
			return jRDesignVariable;
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
		return null;
	}

	protected JRDesignVariable newReportSumJRDesignVariable(JasperDesign jasperDesign, ReportColumn reportColumn)
			throws UnifyException {
		try {
			JRDesignVariable jRDesignVariable = new JRDesignVariable();
			String name = "Sum_" + reportColumn.getName();
			jRDesignVariable.setName(name);
			jRDesignVariable.setValueClass(ReflectUtils.getClassForName(reportColumn.getTypeName()));
			jRDesignVariable.setResetType(ResetTypeEnum.REPORT);
			jRDesignVariable.setCalculation(CalculationEnum.SUM);
			jRDesignVariable
					.setInitialValueExpression(newJRDesignExpression("new " + reportColumn.getTypeName() + "(0)"));
			jRDesignVariable.setExpression(newJRDesignExpression(
					"new " + reportColumn.getTypeName() + "($F{" + reportColumn.getName() + "})"));
			jasperDesign.addVariable(jRDesignVariable);
			return jRDesignVariable;
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
		return null;
	}

	protected boolean isListFormat(ReportFormat reportFormatType) {
		return !(ReportFormat.PDF.equals(reportFormatType) || ReportFormat.DOC.equals(reportFormatType));
	}

	private JRDesignField newJRDesignField(ReportColumn reportColumn, boolean isQuery) throws UnifyException {
		JRDesignField field = new JRDesignField();
		field.setName(reportColumn.getName());
		String type = reportColumn.getTypeName();
		if (isQuery && type.equals("java.util.Date")) {
			type = "java.sql.Timestamp";
		}
		field.setValueClass(ReflectUtils.getClassForName(type));
		return field;
	}

	private ColumnStyles getReportColumnStyles(Report report) throws UnifyException {
		String key = report.getColumnFontName() + "_" + report.getColumnFontSize();
		return columnStylesMap.get(key, report.getColumnFontName(), report.getColumnFontSize());
	}

	protected static class ColumnStyles {

		private JRDesignStyle parentStyle;

		private JRDesignStyle normalStyle;

		private JRDesignStyle boldStyle;

		public ColumnStyles() {
			this("Arial", 10);
		}

		public ColumnStyles(String fontName, int fontSize) {
			String nameSuffix = String.valueOf(hashCode());
			parentStyle = new JRDesignStyle();
			parentStyle.setName("parent_" + nameSuffix);
			parentStyle.setDefault(true);
			parentStyle.setFontName(fontName);
			parentStyle.setFontSize((float) fontSize);
			parentStyle.setVerticalAlignment(VerticalAlignEnum.MIDDLE);

			normalStyle = new JRDesignStyle();
			normalStyle.setParentStyle(parentStyle);
			normalStyle.setName("normal_" + nameSuffix);

			boldStyle = new JRDesignStyle();
			boldStyle.setParentStyle(parentStyle);
			boldStyle.setName("bold_" + nameSuffix);
			boldStyle.setBold(true);

		}

		public JRDesignStyle getParentStyle() {
			return parentStyle;
		}

		public JRDesignStyle getNormalStyle() {
			return normalStyle;
		}

		public JRDesignStyle getBoldStyle() {
			return boldStyle;
		}
	}
}
