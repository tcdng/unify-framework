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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportParameters;
import com.tcdng.unify.core.report.ReportTheme;
import com.tcdng.unify.core.report.ReportTheme.ThemeColors;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;

/**
 * Used for tabular report layout.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("jasperreports-tabularlayoutmanager")
public class JasperReportsTabularLayoutManager extends AbstractJasperReportsLayoutManager {

    @Override
    protected void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
            throws UnifyException {
        ReportTheme theme = report.getReportTheme();
        boolean isListFormat = isListFormat(report.getFormat());
        final int actualColumnWidth = jasperDesign.getColumnWidth();
        final int columnHeaderHeight = theme.getColumnHeaderHeight();
        final int detailHeight = theme.getDetailHeight();

        // Organize layout
        List<ReportColumn> reportColumnList = report.getColumns();
        List<ReportColumn> groupingColumnList = new ArrayList<ReportColumn>();
        List<ReportColumn> detailColumnList = new ArrayList<ReportColumn>();
        List<ReportColumn> summationColumnList = new ArrayList<ReportColumn>();
        Map<ReportColumn, JRDesignElement> detailJRElementMap = new HashMap<ReportColumn, JRDesignElement>();

        ThemeColors detailColors = theme.getDetailTheme();
        int reportWidth = 0;
        for (ReportColumn reportColumn : reportColumnList) {
            if (reportColumn.isGroup()) {
                groupingColumnList.add(reportColumn);
            } else {
                detailColumnList.add(reportColumn);
                if (reportColumn.isSum() && reportColumn.isNumber()) {
                    summationColumnList.add(reportColumn);
                }

                JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, detailColors,
                        columnStyles.getNormalStyle(), reportColumn, isListFormat);
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
            jRDesignElement.setWidth(newWidth - TOTAL_X_PADDING);
            jRDesignElement.setX(x + LEFT_PADDING);
            lastJRDesignElement = jRDesignElement;
            x += newWidth;
        }

        if (x < actualColumnWidth && lastJRDesignElement != null) {
            lastJRDesignElement.setWidth(lastJRDesignElement.getWidth() + actualColumnWidth - x);
        }

        // Construct column header (or title header in case of list layout)
        boolean isWithGroupColumns =
                report.isPrintColumnNames() && report.isPrintGroupColumnNames() && !groupingColumnList.isEmpty();
        if (report.isPrintColumnNames()) {
            JRDesignBand columnHeaderBand = new JRDesignBand();
            columnHeaderBand.setHeight(columnHeaderHeight);

            constructColumnHeaderToBand(jasperDesign, columnHeaderBand, theme.getColumnTheme(), columnStyles,
                    detailColumnList, detailJRElementMap, 0, columnHeaderHeight, actualColumnWidth, isListFormat);

            if (!isWithGroupColumns) {
                if (isListFormat) {
                    jasperDesign.setTitle(columnHeaderBand);
                } else {
                    jasperDesign.setColumnHeader(columnHeaderBand);
                }
            }
        }

        // Construct parameter header
        if (report.isShowParameterHeader()) {
            ReportParameters reportParameters = report.getReportParameters();
            if (reportParameters != null && reportParameters.isWithShowInHeader()) {
                JRDesignBand titleBand = (JRDesignBand) jasperDesign.getTitle();
                constructParamHeaderToBand(jasperDesign, titleBand, theme.getParamTheme(), columnStyles,
                        reportParameters, actualColumnWidth, theme.getDetailHeight(), isListFormat);
            }
        }

        // Construct detail band
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(detailHeight);

        if (report.isShadeOddRows()) {
            JRDesignRectangle jRDesignRectangle =
                    newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth, detailHeight, theme.getShadeTheme());
            jRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
            jRDesignRectangle.setPrintWhenExpression(getOnOddJRDesignExpression());
            jRDesignRectangle.setStretchType(StretchTypeEnum.CONTAINER_HEIGHT);
            detailBand.addElement(jRDesignRectangle);
        }

        if (report.isUnderlineRows()) {
            JRDesignLine jRDesignLine = newJRDesignLine(0, detailHeight - 1, actualColumnWidth, 0, Color.BLACK);
            jRDesignLine.getLinePen().setLineWidth(FLOAT_ZERO_POINT_FIVE);
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

        // Construct groups
        int groupHeaderX = 4;
        int groupCascade = 20;
        int glen = groupingColumnList.size();
        boolean invertGroupColors = report.isInvertGroupColors();
        for (int i = 0; i < glen; i++) {
            ReportColumn grpReportColumn = groupingColumnList.get(i);
            JRDesignGroup jRDesignGroup = newJRDesignGroup(jasperDesign, grpReportColumn);

            // Grouping header
            boolean showColumnHeader = isWithGroupColumns && ((i + 1) == glen);
            JRDesignBand groupHeaderBand = new JRDesignBand();
            if (showColumnHeader) {
                // Add vertical space for column header
                groupHeaderBand.setHeight(columnHeaderHeight + columnHeaderHeight);
            } else {
                groupHeaderBand.setHeight(columnHeaderHeight);
            }

            ThemeColors groupTheme = theme.getGroupTheme(i, invertGroupColors);
            JRDesignRectangle grpJRDesignRectangle =
                    newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth, columnHeaderHeight - 2, groupTheme);
            grpJRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
            groupHeaderBand.addElement(grpJRDesignRectangle);

            JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, groupTheme,
                    columnStyles.getBoldLargeStyle(), grpReportColumn, isListFormat);
            jRDesignElement.setX(groupHeaderX);
            jRDesignElement.setY(2);
            jRDesignElement.setWidth(actualColumnWidth - jRDesignElement.getX());
            jRDesignElement.setHeight(columnHeaderHeight - (4));
            groupHeaderBand.addElement(jRDesignElement);

            if (showColumnHeader) {
                // Add column header strip
                constructColumnHeaderToBand(jasperDesign, groupHeaderBand, theme.getColumnTheme(), columnStyles,
                        detailColumnList, detailJRElementMap, columnHeaderHeight, columnHeaderHeight, actualColumnWidth,
                        isListFormat);
            }

            ((JRDesignSection) jRDesignGroup.getGroupHeaderSection()).addBand(groupHeaderBand);

            if (!summationColumnList.isEmpty()) {
                // Grouping footer
                JRDesignBand groupFooterBand = new JRDesignBand();
                groupFooterBand.setHeight(columnHeaderHeight);
                grpJRDesignRectangle =
                        newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth, columnHeaderHeight - 2, groupTheme);
                grpJRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
                groupFooterBand.addElement(grpJRDesignRectangle);

                int sumStartX = actualColumnWidth;
                for (ReportColumn sumReportColumn : summationColumnList) {
                    JRDesignVariable sumJRDesignVariable =
                            newGroupSumJRDesignVariable(jasperDesign, jRDesignGroup, sumReportColumn);

                    JRDesignTextField sumJRDesignElement = (JRDesignTextField) newColumnJRDesignElement(jasperDesign,
                            groupTheme, columnStyles.getNormalStyle(), sumReportColumn, isListFormat);

                    JRDesignElement refJRDesignElement = detailJRElementMap.get(sumReportColumn);
                    int sumX = refJRDesignElement.getX();
                    if (sumStartX > sumX) {
                        sumStartX = sumX;
                    }

                    sumJRDesignElement.setX(sumX);
                    sumJRDesignElement.setY(refJRDesignElement.getY());
                    sumJRDesignElement.setWidth(refJRDesignElement.getWidth());
                    sumJRDesignElement.setHeight(columnHeaderHeight - (4));
                    if (sumReportColumn.getFormatterUpl() != null) {
                        sumJRDesignElement.setExpression(
                                newJRDesignExpression("com.tcdng.unify.core.report.ReportFormatUtils.format(\""
                                        + sumReportColumn.getFormatterUpl() + "\", $V{" + sumJRDesignVariable.getName()
                                        + "})"));
                    } else {
                        sumJRDesignElement
                                .setExpression(newJRDesignExpression("$V{" + sumJRDesignVariable.getName() + "}"));
                    }

                    groupFooterBand.addElement(sumJRDesignElement);
                }
                
                // Total legend
                final int totalElemWidth = actualColumnWidth / 4;
                sumStartX -= totalElemWidth;
                if (sumStartX >= 0) {
                    JRDesignExpression totalExpression = newJRDesignExpression(
                            "$V{" + jRDesignGroup.getName() + "_COUNT} + \" line item(s)  Total :\"");
                    jRDesignElement = newJRDesignTextField(groupTheme, columnStyles.getNormalStyle(), sumStartX, 2,
                            totalElemWidth, columnHeaderHeight - 4, totalExpression, HAlignType.RIGHT);
                    groupFooterBand.addElement(jRDesignElement);

                    sumStartX -= totalElemWidth;
                    if (sumStartX >= 0) {
                        jRDesignElement = newJRDesignTextField(groupTheme, columnStyles.getNormalStyle(), sumStartX, 2,
                                totalElemWidth, columnHeaderHeight - 4, newJRDesignExpression(grpReportColumn), HAlignType.RIGHT);
                        groupFooterBand.addElement(jRDesignElement);
                    }
                }
                
                ((JRDesignSection) jRDesignGroup.getGroupFooterSection()).addBand(groupFooterBand);
            }

            groupHeaderX += groupCascade;
        }

        // Construct final summary if necessary
        final boolean isGrandSummation = report.isShowGrandFooter();
        if (isGrandSummation && !summationColumnList.isEmpty()) {
            final int grandSummaryHeight = columnHeaderHeight * 2;
            JRDesignBand summaryBand = new JRDesignBand();
            summaryBand.setHeight(grandSummaryHeight);

            ThemeColors grandTheme = theme.getGrandSummaryTheme();
            JRDesignRectangle jRDesignRectangle =
                    newJRDesignRectangle(jasperDesign, 0, 2, actualColumnWidth, grandSummaryHeight - 2, grandTheme);
            summaryBand.addElement(jRDesignRectangle);

            int sumStartX = actualColumnWidth;
            for (ReportColumn sumReportColumn : summationColumnList) {
                JRDesignVariable sumJRDesignVariable = newReportSumJRDesignVariable(jasperDesign, sumReportColumn);
                JRDesignElement jRDesignElement = detailJRElementMap.get(sumReportColumn);

                JRDesignTextField sumJRDesignElement = (JRDesignTextField) newColumnJRDesignElement(jasperDesign,
                        grandTheme, columnStyles.getNormalStyle(), sumReportColumn, isListFormat);
                int sumX = jRDesignElement.getX();
                if (sumStartX > sumX) {
                    sumStartX = sumX;
                }

                sumJRDesignElement.setX(sumX);
                sumJRDesignElement.setY(jRDesignElement.getY());
                sumJRDesignElement.setWidth(jRDesignElement.getWidth());
                sumJRDesignElement.setHeight(columnHeaderHeight - (4));
                if (sumReportColumn.getFormatterUpl() != null) {
                    sumJRDesignElement.setExpression(
                            newJRDesignExpression("com.tcdng.unify.core.report.ReportFormatUtils.format(\""
                                    + sumReportColumn.getFormatterUpl() + "\", $V{" + sumJRDesignVariable.getName()
                                    + "})"));
                } else {
                    sumJRDesignElement
                            .setExpression(newJRDesignExpression("$V{" + sumJRDesignVariable.getName() + "}"));
                }
                summaryBand.addElement(sumJRDesignElement);
            }
            
            
            // Total legend
            final int totalElemWidth = actualColumnWidth / 4;
            sumStartX -= totalElemWidth;
            if (sumStartX >= 0) {
                JRDesignExpression totalExpression = newJRDesignExpression(
                        "$V{REPORT_COUNT} + \" line item(s)  Grand Total :\"");
                JRDesignElement jRDesignElement = newJRDesignTextField(grandTheme, columnStyles.getNormalStyle(), sumStartX, 2,
                        totalElemWidth, columnHeaderHeight - 4, totalExpression, HAlignType.RIGHT);
                summaryBand.addElement(jRDesignElement);
            }
            
            jasperDesign.setSummary(summaryBand);
        }
    }

    private void constructColumnHeaderToBand(JasperDesign jasperDesign, JRDesignBand jrDesignBand,
            ThemeColors columnHeaderColors, ColumnStyles columnStyles, List<ReportColumn> detailColumnList,
            Map<ReportColumn, JRDesignElement> detailJRElementMap, int y, int columnHeaderHeight, int actualColumnWidth,
            boolean isListFormat) throws UnifyException {
        JRDesignRectangle jRDesignRectangle =
                newJRDesignRectangle(jasperDesign, 0, y, actualColumnWidth, columnHeaderHeight, columnHeaderColors);
        jRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
        jRDesignRectangle.setStretchType(StretchTypeEnum.CONTAINER_HEIGHT);
        jrDesignBand.addElement(jRDesignRectangle);

        Color colHeaderColor = columnHeaderColors.getFontColor();
        if (isListFormat) {
            colHeaderColor = Color.BLACK;
        }

        for (ReportColumn reportColumn : detailColumnList) {
            JRDesignElement colHeaderJRDesignElement = newTitleJRDesignStaticText(columnStyles, colHeaderColor,
                    HorizontalTextAlignEnum.CENTER, reportColumn.getTitle());
            JRDesignElement jRDesignElement = detailJRElementMap.get(reportColumn);
            colHeaderJRDesignElement.setX(jRDesignElement.getX());
            colHeaderJRDesignElement.setY(y + 2);
            colHeaderJRDesignElement.setWidth(jRDesignElement.getWidth());
            colHeaderJRDesignElement.setHeight(columnHeaderHeight - (4));

            if (isListFormat) {
                colHeaderJRDesignElement.addPropertyExpression(
                        newJRDesignPropertyExpression("net.sf.jasperreports.print.keep.full.text", true));
            }
            jrDesignBand.addElement(colHeaderJRDesignElement);
        }
    }
}
