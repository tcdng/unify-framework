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
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.report.Report;
import com.tcdng.unify.core.report.ReportColumn;
import com.tcdng.unify.core.report.ReportParameters;
import com.tcdng.unify.core.report.ReportTheme;
import com.tcdng.unify.core.report.ReportTheme.ThemeColors;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.PositionTypeEnum;

/**
 * Used to manage tabular thumb nail images only report layout.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("jasperreports-tabularthumbimagesonlylayoutmanager")
public class JasperReportsTabularThumbnailImagesOnlyLayoutManager extends AbstractJasperReportsLayoutManager {

    @Override
    protected void doApplyLayout(JasperDesign jasperDesign, ColumnStyles columnStyles, Report report)
            throws UnifyException {
        ReportTheme theme = report.getReportTheme();
        boolean isListFormat = isListFormat(report.getFormat());
        int actualColumnWidth = jasperDesign.getColumnWidth();

        // Organize layout
        List<ReportColumn> reportColumnList = report.getColumns();
        List<ReportColumn> groupingColumnList = new ArrayList<ReportColumn>();
        List<ReportColumn> imageColumnList = new ArrayList<ReportColumn>();
        List<ReportColumn> summationColumnList = new ArrayList<ReportColumn>();

        for (ReportColumn reportColumn : reportColumnList) {
            if (reportColumn.isGroup()) {
                groupingColumnList.add(reportColumn);
            } else {
                if (reportColumn.isSum() && reportColumn.isNumber()) {
                    summationColumnList.add(reportColumn);
                } else if (reportColumn.isBlob()) {
                    imageColumnList.add(reportColumn);
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
        final int imageHeight = theme.getDetailImageHeight();
        final int detailHeight = imageHeight + 8;
        detailBand.setHeight(detailHeight);

        if (report.isUnderlineRows()) {
            JRDesignLine jRDesignLine = newJRDesignLine(0, detailHeight - 1, actualColumnWidth, 0, Color.BLACK);
            jRDesignLine.getLinePen().setLineWidth(FLOAT_ZERO_POINT_FIVE);
            jRDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
            detailBand.addElement(jRDesignLine);
        }

        final int imageCount = imageColumnList.size();
        if (imageCount > 0) {
            final int imageGap = 10;
            final int imageWidth = (actualColumnWidth - ((imageCount - 1) * imageGap)) / imageCount;
            int x = 0;
            final int y = 4;
            for (ReportColumn reportColumn : imageColumnList) {
                JRDesignImage jRDesignImage =
                        newJRDesignImage(jasperDesign, x, y, imageWidth, imageHeight, reportColumn);
                detailBand.addElement(jRDesignImage);
                x += imageWidth + imageGap;
            }
        }

        JRDesignSection detailJRDesignSection = ((JRDesignSection) jasperDesign.getDetailSection());
        int len = detailJRDesignSection.getBands().length;
        for (int i = 0; i < len; i++) { // Clear all template bands first
            detailJRDesignSection.removeBand(i);
        }
        detailJRDesignSection.addBand(detailBand); // Add new detail band

        // Construct groups
        final int sumWidth = actualColumnWidth / 6;
        int columnHeaderHeight = theme.getColumnHeaderHeight();
        int groupHeaderX = 4;
        int groupCascade = 20;
        int glen = groupingColumnList.size();
        boolean invertGroupColors = report.isInvertGroupColors();
        for (int i = 0; i < glen; i++) {
            ReportColumn reportColumn = groupingColumnList.get(i);
            JRDesignGroup jRDesignGroup = newJRDesignGroup(jasperDesign, reportColumn);

            // Grouping header
            JRDesignBand groupHeaderBand = new JRDesignBand();
            groupHeaderBand.setHeight(columnHeaderHeight);

            ThemeColors groupTheme = theme.getGroupTheme(i, invertGroupColors);
            JRDesignRectangle grpJRDesignRectangle =
                    newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth, columnHeaderHeight - 2, groupTheme);
            grpJRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
            groupHeaderBand.addElement(grpJRDesignRectangle);

            JRDesignElement jRDesignElement = newColumnJRDesignElement(jasperDesign, groupTheme,
                    columnStyles.getBoldLargeStyle(), reportColumn, isListFormat);
            jRDesignElement.setX(groupHeaderX);
            jRDesignElement.setY(2);
            jRDesignElement.setWidth(actualColumnWidth - jRDesignElement.getX());
            jRDesignElement.setHeight(columnHeaderHeight - (4));
            groupHeaderBand.addElement(jRDesignElement);
            ((JRDesignSection) jRDesignGroup.getGroupHeaderSection()).addBand(groupHeaderBand);

            if (!summationColumnList.isEmpty()) {
                // Grouping footer
                JRDesignBand groupFooterBand = new JRDesignBand();
                groupFooterBand.setHeight(columnHeaderHeight);
                grpJRDesignRectangle =
                        newJRDesignRectangle(jasperDesign, 0, 1, actualColumnWidth, columnHeaderHeight - 2, groupTheme);
                grpJRDesignRectangle.getLinePen().setLineWidth(FLOAT_ZERO);
                groupFooterBand.addElement(grpJRDesignRectangle);

                int sumX = 4 + sumWidth * (6 - summationColumnList.size()) - 2;
                for (ReportColumn sumReportColumn : summationColumnList) {
                    JRDesignVariable sumJRDesignVariable =
                            newGroupSumJRDesignVariable(jasperDesign, jRDesignGroup, sumReportColumn);

                    JRDesignTextField sumJRDesignElement = (JRDesignTextField) newColumnJRDesignElement(jasperDesign,
                            groupTheme, columnStyles.getNormalStyle(), sumReportColumn, isListFormat);

                    sumJRDesignElement.setX(sumX);
                    sumJRDesignElement.setY(2);
                    sumJRDesignElement.setWidth(sumWidth);
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

                    sumX += sumWidth;
                    groupFooterBand.addElement(sumJRDesignElement);
                }
                ((JRDesignSection) jRDesignGroup.getGroupFooterSection()).addBand(groupFooterBand);
            }

            groupHeaderX += groupCascade;
        }

        // Construct final summary if necessary
        boolean isGrandSummation = false; // TODO Get from report object
        if (isGrandSummation && !summationColumnList.isEmpty()) {
            JRDesignBand summaryBand = new JRDesignBand();
            summaryBand.setHeight(columnHeaderHeight);

            ThemeColors grandTheme = theme.getGrandSummaryTheme();
            JRDesignRectangle jRDesignRectangle =
                    newJRDesignRectangle(jasperDesign, 0, 0, actualColumnWidth, columnHeaderHeight, grandTheme);
            summaryBand.addElement(jRDesignRectangle);

            int sumX = groupHeaderX + sumWidth * (6 - summationColumnList.size()) - 2;
            for (ReportColumn sumReportColumn : summationColumnList) {
                JRDesignVariable sumJRDesignVariable = newReportSumJRDesignVariable(jasperDesign, sumReportColumn);
                JRDesignTextField sumJRDesignElement = (JRDesignTextField) newColumnJRDesignElement(jasperDesign,
                        grandTheme, columnStyles.getNormalStyle(), sumReportColumn, isListFormat);

                sumJRDesignElement.setX(sumX);
                sumJRDesignElement.setY(2);
                sumJRDesignElement.setWidth(sumWidth);
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

                sumX += sumWidth;
                summaryBand.addElement(sumJRDesignElement);
            }
            jasperDesign.setSummary(summaryBand);
        }
    }
}
