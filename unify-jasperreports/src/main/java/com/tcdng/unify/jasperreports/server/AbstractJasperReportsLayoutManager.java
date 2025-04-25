/*
 * Copyright 2018-2025 The Code Department.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.VAlignType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportField;
import com.tcdng.unify.core.report.ReportFormat;
import com.tcdng.unify.core.report.ReportFormatUtils;
import com.tcdng.unify.core.report.ReportParameter;
import com.tcdng.unify.core.report.ReportParameters;
import com.tcdng.unify.core.report.ReportPlacement;
import com.tcdng.unify.core.report.ReportTheme;
import com.tcdng.unify.core.report.ReportTheme.ThemeColors;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignPropertyExpression;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;

/**
 * Abstract jasper report layout manager that provides methods for easy
 * manipulation of a jasper design object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractJasperReportsLayoutManager extends AbstractUnifyComponent
		implements JasperReportsLayoutManager {

	protected static final Float FLOAT_ZERO = Float.valueOf(0);

	protected static final Float FLOAT_ZERO_POINT_FIVE = Float.valueOf(0.5f);

	protected static final int LEFT_PADDING = 2;

	protected static final int TOTAL_X_PADDING = LEFT_PADDING + 2;

	private FactoryMap<String, ColumnStyles> columnStylesMap;

	private Map<HAlignType, HorizontalTextAlignEnum> hAlignMap;

	private Map<VAlignType, VerticalTextAlignEnum> vAlignMap;

	public AbstractJasperReportsLayoutManager() {
		columnStylesMap = new FactoryMap<String, ColumnStyles>() {
			@Override
			protected ColumnStyles create(String key, Object... params) throws Exception {
				return new ColumnStyles((String) params[0], (Integer) params[1], (Integer) params[2]);
			}
		};

		hAlignMap = new HashMap<HAlignType, HorizontalTextAlignEnum>();
		hAlignMap.put(HAlignType.LEFT, HorizontalTextAlignEnum.LEFT);
		hAlignMap.put(HAlignType.CENTER, HorizontalTextAlignEnum.CENTER);
		hAlignMap.put(HAlignType.RIGHT, HorizontalTextAlignEnum.RIGHT);
		hAlignMap.put(HAlignType.JUSTIFIED, HorizontalTextAlignEnum.JUSTIFIED);
		hAlignMap = Collections.unmodifiableMap(hAlignMap);

		vAlignMap = new HashMap<VAlignType, VerticalTextAlignEnum>();
		vAlignMap.put(VAlignType.TOP, VerticalTextAlignEnum.TOP);
		vAlignMap.put(VAlignType.MIDDLE, VerticalTextAlignEnum.MIDDLE);
		vAlignMap.put(VAlignType.BOTTOM, VerticalTextAlignEnum.BOTTOM);
		vAlignMap = Collections.unmodifiableMap(vAlignMap);
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
			jasperDesign.addStyle(columnStyles.getNormalLargeStyle());
			jasperDesign.addStyle(columnStyles.getBoldStyle());
			jasperDesign.addStyle(columnStyles.getBoldLargeStyle());

			boolean isQuery = !report.isWithBeanCollection();
			for (ReportColumn reportColumn : report.getColumns()) {
				jasperDesign.addField(newJRDesignField(reportColumn, isQuery));
			}

			for (ReportPlacement reportPlacement : report.getPlacements()) {
				if (reportPlacement.isField()) {
					jasperDesign.addField(newJRDesignField(reportPlacement, isQuery));
				}
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

	protected HorizontalTextAlignEnum getHorizontalAlign(HAlignType hAlignType) {
		return hAlignMap.get(hAlignType);
	}

	protected VerticalTextAlignEnum getVerticalAlign(VAlignType vAlignType) {
		return vAlignMap.get(vAlignType);
	}

	protected void clearAll(JasperDesign jasperDesign) {
		jasperDesign.setColumnHeader(null);
		jasperDesign.setColumnFooter(null);
		clearDetailSection(jasperDesign);
	}

	protected void clearDetailSection(JasperDesign jasperDesign) {
		JRDesignSection detailJRDesignSection = ((JRDesignSection) jasperDesign.getDetailSection());
		int len = detailJRDesignSection.getBands().length;
		for (int i = 0; i < len; i++) {
			detailJRDesignSection.removeBand(i);
		}
	}

	protected void addDetailBand(JasperDesign jasperDesign, JRDesignBand detailBand) {
		JRDesignSection detailJRDesignSection = ((JRDesignSection) jasperDesign.getDetailSection());
		detailJRDesignSection.addBand(detailBand);
	}

	protected void setTitleBand(JasperDesign jasperDesign, JRDesignBand titleBand) {
		jasperDesign.setTitle(titleBand);
	}

	protected void defineParameter(JasperDesign jasperDesign, String name, Class<?> valueClass) throws UnifyException {
		try {
			JRDesignParameter jrParameter = new JRDesignParameter();
			jrParameter.setName(name);
			jrParameter.setValueClass(valueClass);
			jasperDesign.addParameter(jrParameter);
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
	}

	protected void defineField(JasperDesign jasperDesign, String name, Class<?> valueClass) throws UnifyException {
		try {
			JRDesignField jrField = new JRDesignField();
			jrField.setName(name);
			jrField.setValueClass(valueClass);
			jasperDesign.addField(jrField);
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
	}

	protected void constructParamHeaderToBand(JasperDesign jasperDesign, JRDesignBand jrDesignBand,
			ThemeColors paramHeaderColors, ColumnStyles columnStyles, ReportParameters reportParameters,
			final int actualColumnWidth, final int detailHeight, boolean isListFormat) throws UnifyException {
		// Compute dimensions
		final int NUMBER_OF_COLUMNS = 2;
		final int width = actualColumnWidth - 0;
		int paramCount = reportParameters.getShowInHeaderCount();
		int paramLineCount = paramCount / NUMBER_OF_COLUMNS;
		if (paramCount % NUMBER_OF_COLUMNS > 0) {
			paramLineCount++;
		}

		final int height = (detailHeight * paramLineCount) + 8;
		final int x = (actualColumnWidth - width) / 2;
		final int y = jrDesignBand.getHeight();
		jrDesignBand.setHeight(y + height);

		// Draw background
		JRDesignRectangle jRDesignRectangle = newJRDesignRectangle(jasperDesign, x, y + 2, width, height - 4,
				paramHeaderColors);
		jRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
		jRDesignRectangle.setStretchType(StretchTypeEnum.CONTAINER_HEIGHT);
		jrDesignBand.addElement(jRDesignRectangle);

		Color fontColor = paramHeaderColors.getFontColor();
		if (isListFormat) {
			fontColor = Color.BLACK;
		}

		int linesToWrite = paramLineCount;
		int penX = x;
		int penY = y + 4;
		int sectionWidth = width / (NUMBER_OF_COLUMNS * 3 - 1);
		for (ReportParameter reportParameter : reportParameters.getParameters()) {
			if (reportParameter.isHeaderDetail()) {
				// Label
				String paramLabel = StringUtils.concatenate(reportParameter.getDescription(), " :");
				JRDesignElement paramLabelJRDesignElement = newJRDesignStaticText(columnStyles, fontColor, null,
						HorizontalTextAlignEnum.LEFT, paramLabel);
				paramLabelJRDesignElement.setX(penX + 2);
				paramLabelJRDesignElement.setY(penY + 2);
				paramLabelJRDesignElement.setWidth(sectionWidth - 4);
				paramLabelJRDesignElement.setHeight(detailHeight - (4));

				if (isListFormat) {
					paramLabelJRDesignElement.addPropertyExpression(
							newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
				}
				jrDesignBand.addElement(paramLabelJRDesignElement);

				// Value
				String paramVal = ReportFormatUtils.format(reportParameter.getFormatter(), reportParameter.getValue());
				JRDesignElement paramValJRDesignElement = newJRDesignStaticText(columnStyles, fontColor, null,
						HorizontalTextAlignEnum.LEFT, paramVal);
				paramValJRDesignElement.setX(penX + sectionWidth + 2);
				paramValJRDesignElement.setY(penY + 2);
				paramValJRDesignElement.setWidth(sectionWidth - 4);
				paramValJRDesignElement.setHeight(detailHeight - (4));

				if (isListFormat) {
					paramValJRDesignElement.addPropertyExpression(
							newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
				}
				jrDesignBand.addElement(paramValJRDesignElement);

				// Next line
				penY += detailHeight;

				if (--linesToWrite == 0) {
					// Reset to first line
					penY = y + 4;

					// Move to next column
					penX += sectionWidth * 3;
				}
			}
		}
	}

	protected JRDesignGroup newJRDesignGroup(JasperDesign jasperDesign, ReportColumn reportColumn)
			throws UnifyException {
		try {
			JRDesignGroup jRDesignGroup = new JRDesignGroup();
			jRDesignGroup.setName(reportColumn.getName() + "_Group");
			jRDesignGroup.setExpression(newJRDesignExpression(reportColumn));
			jRDesignGroup.setStartNewPage(reportColumn.isGroupOnNewPage());
			jasperDesign.addGroup(jRDesignGroup);
			return jRDesignGroup;
		} catch (JRException e) {
			throwOperationErrorException(e);
		}
		return null;
	}

	protected JRDesignImage newJRDesignImage(JasperDesign jasperDesign, int x, int y, int width, int height,
			ReportColumn reportColumn) throws UnifyException {
		JRDesignImage jRDesignImage = new JRDesignImage(jasperDesign);
		jRDesignImage.setX(x);
		jRDesignImage.setY(y);
		jRDesignImage.setWidth(width);
		jRDesignImage.setHeight(height);
		jRDesignImage.setExpression(newJRDesignExpression(reportColumn));
		jRDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		return jRDesignImage;
	}

	protected JRDesignElement newColumnJRDesignElement(JasperDesign jasperDesign, ThemeColors themeColors,
			ColumnStyles columnStyles, ReportColumn reportColumn, boolean isListFormat) throws UnifyException {
		if (reportColumn.isBlob()) {
			JRDesignImage jrDesignImage = new JRDesignImage(jasperDesign);
			jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
			jrDesignImage.setWidth(reportColumn.getWidthRatio());
			jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
			jrDesignImage.setExpression(newJRDesignExpression(reportColumn));
			jrDesignImage.setPrintWhenExpression(newNotNullJRDesignExpression(reportColumn));
			return jrDesignImage;
		}

		JRDesignTextField textField = new JRDesignTextField();
		textField.setWidth(reportColumn.getWidthRatio());
		textField.setForecolor(themeColors.getFontColor());
		textField.setStyle(reportColumn.isBold() ? columnStyles.getBoldStyle() : columnStyles.getNormalStyle());
		textField.setHorizontalTextAlign(getHorizontalAlign(reportColumn.getHAlign()));
		textField.setVerticalTextAlign(getVerticalAlign(reportColumn.getVAlign()));
		textField.setExpression(newJRDesignExpression(reportColumn));
		textField.setBlankWhenNull(true);

		textField.addPropertyExpression(
				newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));

		if (!isListFormat) {
			textField.setStretchWithOverflow(true);
		}
		return textField;
	}

	protected JRDesignElement newPlacementJRDesignElement(JasperDesign jasperDesign, ColumnStyles columnStyles,
			ReportPlacement reportPlacement) throws UnifyException {
		final int x = reportPlacement.getXOffsetType().isLeft() ? reportPlacement.getX()
				: jasperDesign.getColumnWidth() - reportPlacement.getWidth() - reportPlacement.getX();
		final int y = reportPlacement.getYOffsetType().isTop() ? reportPlacement.getY()
				: (jasperDesign.getPageHeight() - (jasperDesign.getTopMargin() + jasperDesign.getBottomMargin()))
						- reportPlacement.getHeight() - reportPlacement.getY();
		if (reportPlacement.isText()) {
			return newJRDesignStaticText(columnStyles, reportPlacement.getColors().getFontColor(),
					reportPlacement.getColors().getBackColor(), getHorizontalAlign(reportPlacement.getHAlign()), x, y,
					reportPlacement.getWidth(), reportPlacement.getHeight(), reportPlacement.getText());
		} else if (reportPlacement.isImage()) {
			if (reportPlacement.isBlob()) {
				JRDesignImage jrDesignImage = new JRDesignImage(jasperDesign);
				jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
				jrDesignImage.setX(x);
				jrDesignImage.setY(y);
				jrDesignImage.setWidth(reportPlacement.getWidth());
				jrDesignImage.setHeight(reportPlacement.getHeight());
				jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
				jrDesignImage.setExpression(newJRDesignExpression(reportPlacement));
				jrDesignImage.setPrintWhenExpression(newNotNullJRDesignExpression(reportPlacement));
				return jrDesignImage;
			}
		} else if (reportPlacement.isRectangle()) {
			return newJRDesignRectangle(jasperDesign, x, y, reportPlacement.getWidth(), reportPlacement.getHeight(),
					reportPlacement.getColors());
		} else if (reportPlacement.isLine()) {
			return newJRDesignLine(x, y, reportPlacement.getWidth(), reportPlacement.getHeight(),
					reportPlacement.getColors().getForeColor());
		}

		JRDesignTextField textField = new JRDesignTextField();
		textField.setX(x);
		textField.setY(y);
		textField.setWidth(reportPlacement.getWidth());
		textField.setHeight(reportPlacement.getHeight());
		textField.setForecolor(reportPlacement.getColors().getFontColor());
		textField.setBackcolor(reportPlacement.getColors().getBackColor());
		textField.setStyle(reportPlacement.isBold() ? columnStyles.getBoldStyle() : columnStyles.getNormalStyle());
		textField.setHorizontalTextAlign(getHorizontalAlign(reportPlacement.getHAlign()));
		textField.setVerticalTextAlign(getVerticalAlign(reportPlacement.getVAlign()));
		textField.setExpression(newJRDesignExpression(reportPlacement));
		textField.setBlankWhenNull(true);

		textField.addPropertyExpression(
				newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
		return textField;
	}

	protected JRDesignElement newEmbeddedHtmlColumnJRDesignElement(JasperDesign jasperDesign, ThemeColors themeColors,
			JRDesignStyle fontStyle, int width) throws UnifyException {
		JRDesignTextField textField = new JRDesignTextField();
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setMode(ModeEnum.OPAQUE);
		textField.setWidth(width);
		textField.setHeight(110);
		textField.setStyle(fontStyle);
		textField.setMarkup("html");
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$F{html}");
		textField.setExpression(expression);
		textField.setStretchWithOverflow(true);
		textField.setBlankWhenNull(true);
		return textField;
	}

	protected JRDesignPropertyExpression newJRDesignPropertyExpression(String key, Object value) {
		JRDesignPropertyExpression propertyExpression = new JRDesignPropertyExpression();
		propertyExpression.setName(key);
		propertyExpression
				.setValueExpression(new JRDesignExpression("String.valueOf(\"" + String.valueOf(value) + "\")"));
		return propertyExpression;
	}

	protected JRDesignStaticText newJRDesignStaticText(ColumnStyles columnStyles, Color foreColor, Color backColor,
			HorizontalTextAlignEnum hAlign, String text) throws UnifyException {
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setForecolor(foreColor);
		staticText.setBackcolor(backColor);
		staticText.setMode(ModeEnum.TRANSPARENT);
		staticText.setStyle(columnStyles.getBoldStyle());
		staticText.setText(text);
		staticText.setHorizontalTextAlign(hAlign);
		return staticText;
	}

	protected JRDesignStaticText newJRDesignStaticText(ColumnStyles columnStyles, Color foreColor, Color backColor,
			HorizontalTextAlignEnum hAlign, int x, int y, int width, int height, String text) throws UnifyException {
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setX(x);
		staticText.setY(y);
		staticText.setWidth(width);
		staticText.setHeight(height);
		staticText.setForecolor(foreColor);
		staticText.setBackcolor(backColor);
		staticText.setMode(ModeEnum.TRANSPARENT);
		staticText.setStyle(columnStyles.getBoldStyle());
		staticText.setText(text);
		staticText.setHorizontalTextAlign(hAlign);
		return staticText;
	}

	protected JRDesignTextField newJRDesignTextField(ThemeColors themeColors, JRStyle style, int x, int y, int width,
			int height, JRDesignExpression expression, HAlignType hAlignType, VAlignType vAlignType)
			throws UnifyException {
		JRDesignTextField jRDesignTextField = new JRDesignTextField();
		jRDesignTextField.setX(x);
		jRDesignTextField.setY(y);
		jRDesignTextField.setWidth(width);
		jRDesignTextField.setHeight(height);
		jRDesignTextField.setForecolor(themeColors.getFontColor());
		jRDesignTextField.setBackcolor(themeColors.getBackColor());
		jRDesignTextField.setStyle(style);
		jRDesignTextField.setMode(ModeEnum.OPAQUE);
		jRDesignTextField.setHorizontalTextAlign(getHorizontalAlign(hAlignType));
		jRDesignTextField.setVerticalTextAlign(getVerticalAlign(vAlignType));
		jRDesignTextField.setExpression(expression);
		return jRDesignTextField;
	}

	protected JRDesignRectangle newJRDesignRectangle(JasperDesign jasperDesign, int x, int y, int width, int height,
			ThemeColors themeColors) throws UnifyException {
		JRDesignRectangle jRDesignRectangle = new JRDesignRectangle(jasperDesign);
		jRDesignRectangle.setX(x);
		jRDesignRectangle.setY(y);
		jRDesignRectangle.setWidth(width);
		jRDesignRectangle.setHeight(height);
		jRDesignRectangle.setForecolor(themeColors.getForeColor());
		jRDesignRectangle.setBackcolor(themeColors.getBackColor());
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

	protected JRDesignExpression newNotNullJRDesignExpression(ReportField reportField) {
		return newJRDesignExpression("new Boolean($F{" + reportField.getName() + "} != null)");
	}

	protected JRDesignExpression getOnOddJRDesignExpression() {
		return newJRDesignExpression("new Boolean($V{PAGE_COUNT}.intValue() % 2 > 0)");
	}

	protected JRDesignExpression newJRDesignExpression(String expression) {
		JRDesignExpression jRDesignExpression = new JRDesignExpression();
		jRDesignExpression.setText(expression);
		return jRDesignExpression;
	}

	protected JRDesignExpression newJRDesignExpression(ReportField reportField) throws UnifyException {
		JRDesignExpression expression = new JRDesignExpression();
		if (reportField.isBlob()) {
			if (StringUtils.isNotBlank(reportField.getSqlBlobTypeName())) {
				expression.setText("$F{" + reportField.getName() + "}.getBinaryStream()");
			} else {
				expression.setText("new ByteArrayInputStream((byte[])$F{" + reportField.getName() + "})");
			}
		} else {
			if (reportField.getFormatterUpl() != null) {
				expression.setText("com.tcdng.unify.core.report.ReportFormatUtils.format(\""
						+ reportField.getFormatterUpl() + "\", $F{" + reportField.getName() + "})");
			} else {
				expression.setText("$F{" + reportField.getName() + "}");
			}
		}
		return expression;
	}

	protected JRDesignVariable newGroupSumJRDesignVariable(JasperDesign jasperDesign, JRDesignGroup jRDesignGroup,
			ReportColumn reportColumn) throws UnifyException {
		try {
			JRDesignVariable jRDesignVariable = new JRDesignVariable();
			String name = jRDesignGroup.getName() + "_Sum_" + reportColumn.getName();
			jRDesignVariable.setName(name);
			jRDesignVariable.setValueClass(ReflectUtils.classForName(reportColumn.getTypeName()));
			jRDesignVariable.setResetType(ResetTypeEnum.GROUP);
			jRDesignVariable.setResetGroup(jRDesignGroup);
			jRDesignVariable.setCalculation(CalculationEnum.SUM);
			if (reportColumn.getTypeName().equals("java.math.BigDecimal")) {
				jRDesignVariable.setInitialValueExpression(newJRDesignExpression("new java.math.BigDecimal(0)"));
				jRDesignVariable.setExpression(newJRDesignExpression(
						"new java.math.BigDecimal($F{" + reportColumn.getName() + "}.doubleValue())"));
			} else {
				jRDesignVariable
						.setInitialValueExpression(newJRDesignExpression("new " + reportColumn.getTypeName() + "(0)"));
				jRDesignVariable.setExpression(newJRDesignExpression(
						"new " + reportColumn.getTypeName() + "($F{" + reportColumn.getName() + "})"));
			}
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
			jRDesignVariable.setValueClass(ReflectUtils.classForName(reportColumn.getTypeName()));
			jRDesignVariable.setResetType(ResetTypeEnum.REPORT);
			jRDesignVariable.setCalculation(CalculationEnum.SUM);
			if (reportColumn.getTypeName().equals("java.math.BigDecimal")) {
				jRDesignVariable.setInitialValueExpression(newJRDesignExpression("new java.math.BigDecimal(0)"));
				jRDesignVariable.setExpression(newJRDesignExpression(
						"new java.math.BigDecimal($F{" + reportColumn.getName() + "}.doubleValue())"));
			} else {
				jRDesignVariable
						.setInitialValueExpression(newJRDesignExpression("new " + reportColumn.getTypeName() + "(0)"));
				jRDesignVariable.setExpression(newJRDesignExpression(
						"new " + reportColumn.getTypeName() + "($F{" + reportColumn.getName() + "})"));
			}
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

	private JRDesignField newJRDesignField(ReportField reportField, boolean isQuery) throws UnifyException {
		JRDesignField field = new JRDesignField();
		field.setName(reportField.getName());
		String type = reportField.getTypeName();
		if (isQuery) {
			if (reportField.isDate()) {
				type = "java.sql.Timestamp";
			} else if (reportField.isBlob() && StringUtils.isNotBlank(reportField.getSqlBlobTypeName())) {
				type = reportField.getSqlBlobTypeName();
			}
		}

		field.setValueClass(ReflectUtils.classForName(type));
		return field;
	}

	private ColumnStyles getReportColumnStyles(Report report) throws UnifyException {
		ReportTheme theme = report.getReportTheme();
		String key = theme.getColumnFontName() + "_" + theme.getColumnFontSize();
		return columnStylesMap.get(key, theme.getColumnFontName(), theme.getColumnFontSize(), theme.getGroupFontSize());
	}

	protected static class ColumnStyles {

		private JRDesignStyle parentStyle;

		private JRDesignStyle normalStyle;

		private JRDesignStyle normalLargeStyle;

		private JRDesignStyle boldStyle;

		private JRDesignStyle boldLargeStyle;

		public ColumnStyles() {
			this("Arial", 10, 14);
		}

		public ColumnStyles(String fontName, int fontSize, int largeFontSize) {
			String nameSuffix = String.valueOf(hashCode());
			parentStyle = new JRDesignStyle();
			parentStyle.setName("parent_" + nameSuffix);
			parentStyle.setDefault(true);
			parentStyle.setFontName(fontName);
			parentStyle.setFontSize((float) fontSize);
			parentStyle.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);

			normalStyle = new JRDesignStyle();
			normalStyle.setParentStyle(parentStyle);
			normalStyle.setName("normal_" + nameSuffix);

			normalLargeStyle = new JRDesignStyle();
			normalLargeStyle.setParentStyle(parentStyle);
			normalLargeStyle.setName("normallarge_" + nameSuffix);
			normalLargeStyle.setFontSize((float) largeFontSize);

			boldStyle = new JRDesignStyle();
			boldStyle.setParentStyle(parentStyle);
			boldStyle.setName("bold_" + nameSuffix);
			boldStyle.setBold(Boolean.TRUE);

			boldLargeStyle = new JRDesignStyle();
			boldLargeStyle.setParentStyle(parentStyle);
			boldLargeStyle.setName("boldlarge_" + nameSuffix);
			boldLargeStyle.setFontSize((float) largeFontSize);
			boldLargeStyle.setBold(Boolean.TRUE);
		}

		public JRDesignStyle getParentStyle() {
			return parentStyle;
		}

		public JRDesignStyle getNormalStyle() {
			return normalStyle;
		}

		public JRDesignStyle getNormalLargeStyle() {
			return normalLargeStyle;
		}

		public JRDesignStyle getBoldStyle() {
			return boldStyle;
		}

		public JRDesignStyle getBoldLargeStyle() {
			return boldLargeStyle;
		}
	}
}
