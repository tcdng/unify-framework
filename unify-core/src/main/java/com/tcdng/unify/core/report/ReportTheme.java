/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.report;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Report theme.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ReportTheme {

    public static final ReportTheme DEFAULT_THEME;

    static {
        DEFAULT_THEME = ReportTheme.newBuilder()
                .addGroupTheme(Color.decode("#000000"), Color.decode("#CFC8F0"), Color.decode("#CFC8F0"))
                .addGroupTheme(Color.decode("#FFFFFF"), Color.decode("#4C3B96"), Color.decode("#4C3B96")).build();
    }

    private static final String DEFAULT_COLUMN_FONTNAME = "Times-Roman";

    private static final int DEFAULT_COLUMN_FONTSIZE = 10;

    private static final int DEFAULT_GROUP_FONTSIZE = 12;

    private static final int DEFAULT_COLUMNHEADER_HEIGHT = 18;

    private static final int DEFAULT_DETAIL_HEIGHT = 18;

    private static final int DEFAULT_DETAIL_IMAGE_WIDTH = 360;

    private static final int DEFAULT_DETAIL_IMAGE_HEIGHT = 124;

    private String columnFontName;

    private int columnFontSize;

    private int columnHeaderHeight;

    private int detailHeight;

    private int groupFontSize;

    private int detailImageWidth;

    private int detailImageHeight;

    private Map<String, Object> attributes;

    private ThemeColors paramTheme;

    private ThemeColors columnTheme;

    private ThemeColors detailTheme;

    private ThemeColors shadeTheme;

    private ThemeColors grandSummaryTheme;

    private List<ThemeColors> groupThemeList;

    private ReportTheme(String columnFontName, int columnFontSize, int columnHeaderHeight, int detailHeight,
            int groupFontSize, int detailImageWidth, int detailImageHeight, Map<String, Object> attributes,
            ThemeColors paramTheme, ThemeColors columnTheme, ThemeColors detailTheme, ThemeColors shadeTheme,
            ThemeColors grandSummaryTheme, List<ThemeColors> groupThemeList) {
        this.columnFontName = columnFontName;
        this.columnFontSize = columnFontSize;
        this.columnHeaderHeight = columnHeaderHeight;
        this.detailHeight = detailHeight;
        this.groupFontSize = groupFontSize;
        this.detailImageWidth = detailImageWidth;
        this.detailImageHeight = detailImageHeight;
        this.attributes = attributes;
        this.paramTheme = paramTheme;
        this.columnTheme = columnTheme;
        this.detailTheme = detailTheme;
        this.shadeTheme = shadeTheme;
        this.grandSummaryTheme = grandSummaryTheme;
        this.groupThemeList = groupThemeList;
    }

    public String getColumnFontName() {
        return columnFontName;
    }

    public int getColumnFontSize() {
        return columnFontSize;
    }

    public int getColumnHeaderHeight() {
        return columnHeaderHeight;
    }

    public int getDetailHeight() {
        return detailHeight;
    }

    public int getGroupFontSize() {
        return groupFontSize;
    }

    public int getDetailImageWidth() {
        return detailImageWidth;
    }

    public int getDetailImageHeight() {
        return detailImageHeight;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ThemeColors getParamTheme() {
        return paramTheme;
    }

    public ThemeColors getColumnTheme() {
        return columnTheme;
    }

    public ThemeColors getDetailTheme() {
        return detailTheme;
    }

    public ThemeColors getShadeTheme() {
        return shadeTheme;
    }

    public ThemeColors getGrandSummaryTheme() {
        return grandSummaryTheme;
    }

    public ThemeColors getGroupTheme(int level, boolean inverted) {
        if (level < 0 || groupThemeList.isEmpty()) {
            return columnTheme;
        }

        if (inverted) {
            return groupThemeList.get((groupThemeList.size() - level % groupThemeList.size()) - 1);
        }

        return groupThemeList.get(level % groupThemeList.size());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String columnFontName;

        private int columnFontSize;

        private int columnHeaderHeight;

        private int detailHeight;

        private int groupFontSize;

        private int detailImageWidth;

        private int detailImageHeight;

        private Map<String, Object> attributes;

        private ThemeColors paramTheme;

        private ThemeColors columnTheme;

        private ThemeColors detailTheme;

        private ThemeColors shadeTheme;

        private ThemeColors grandSummaryTheme;

        private List<ThemeColors> groupThemeList;

        private Builder() {
            columnFontName = DEFAULT_COLUMN_FONTNAME;
            columnFontSize = DEFAULT_COLUMN_FONTSIZE;
            columnHeaderHeight = DEFAULT_COLUMNHEADER_HEIGHT;
            detailHeight = DEFAULT_DETAIL_HEIGHT;
            groupFontSize = DEFAULT_GROUP_FONTSIZE;
            detailImageWidth = DEFAULT_DETAIL_IMAGE_WIDTH;
            detailImageHeight = DEFAULT_DETAIL_IMAGE_HEIGHT;

            paramTheme = new ThemeColors(Color.decode("#000000"), Color.decode("#EEEEEE"), Color.decode("#EEEEEE"));
            columnTheme = new ThemeColors(Color.decode("#FFFFFF"), Color.decode("#C0C0C0"), Color.decode("#C0C0C0"));
            detailTheme = new ThemeColors(Color.decode("#000000"), Color.decode("#FFFFFF"), Color.decode("#FFFFFF"));
            shadeTheme = new ThemeColors(Color.decode("#000000"), Color.decode("#EEEEEE"), Color.decode("#EEEEEE"));
            grandSummaryTheme =
                    new ThemeColors(Color.decode("#000000"), Color.decode("#EEEEEE"), Color.decode("#EEEEEE"));
        }

        public Builder columnFontSize(int columnFontSize) {
            this.columnFontSize = columnFontSize;
            return this;
        }

        public Builder columnHeaderHeight(int columnHeaderHeight) {
            this.columnHeaderHeight = columnHeaderHeight;
            return this;
        }

        public Builder detailHeight(int detailHeight) {
            this.detailHeight = detailHeight;
            return this;
        }

        public Builder groupFontSize(int groupFontSize) {
            this.groupFontSize = groupFontSize;
            return this;
        }

        public Builder detailImageWidth(int detailImageWidth) {
            this.detailImageWidth = detailImageWidth;
            return this;
        }

        public Builder detailImageHeight(int detailImageHeight) {
            this.detailImageHeight = detailImageHeight;
            return this;
        }

        public Builder addAttribute(String name, Object value) {
            if (attributes == null) {
                attributes = new HashMap<String, Object>();
            }

            attributes.put(name, value);
            return this;
        }

        public Builder paramTheme(Color fontColor, Color foreColor, Color backColor) {
            paramTheme = new ThemeColors(fontColor, foreColor, backColor);
            return this;
        }

        public Builder columnTheme(Color fontColor, Color foreColor, Color backColor) {
            columnTheme = new ThemeColors(fontColor, foreColor, backColor);
            return this;
        }

        public Builder detailTheme(Color fontColor, Color foreColor, Color backColor) {
            detailTheme = new ThemeColors(fontColor, foreColor, backColor);
            return this;
        }

        public Builder shadeTheme(Color fontColor, Color foreColor, Color backColor) {
            shadeTheme = new ThemeColors(fontColor, foreColor, backColor);
            return this;
        }

        public Builder grandSummaryTheme(Color fontColor, Color foreColor, Color backColor) {
            grandSummaryTheme = new ThemeColors(fontColor, foreColor, backColor);
            return this;
        }

        public Builder addGroupTheme(Color fontColor, Color foreColor, Color backColor) {
            if (groupThemeList == null) {
                groupThemeList = new ArrayList<ThemeColors>();
            }
            groupThemeList.add(new ThemeColors(fontColor, foreColor, backColor));
            return this;
        }

        public ReportTheme build() {
            return new ReportTheme(columnFontName, columnFontSize, columnHeaderHeight, detailHeight, groupFontSize,
                    detailImageWidth, detailImageHeight, DataUtils.unmodifiableMap(attributes), paramTheme, columnTheme,
                    detailTheme, shadeTheme, grandSummaryTheme, DataUtils.unmodifiableList(groupThemeList));
        }
    }

    public static class ThemeColors {

        private Color fontColor;

        private Color foreColor;

        private Color backColor;

        public ThemeColors(Color fontColor, Color foreColor, Color backColor) {
            this.fontColor = fontColor;
            this.foreColor = foreColor;
            this.backColor = backColor;
        }

        public Color getFontColor() {
            return fontColor;
        }

        public Color getForeColor() {
            return foreColor;
        }

        public Color getBackColor() {
            return backColor;
        }
    }

}
