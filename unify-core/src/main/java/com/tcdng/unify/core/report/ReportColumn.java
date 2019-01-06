/*
 * Copyright 2018-2019 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.constant.OrderType;

/**
 * A report column.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ReportColumn {

    private String title;

    private String table;

    private String name;

    private String className;

    private String formatterUpl;

    private OrderType order;

    private HAlignType horizontalAlignment;

    private int widthRatio;

    private boolean group;

    private boolean sum;

    private ReportColumn(String title, String table, String name, String className, String formatterUpl,
            OrderType order, HAlignType horizontalAlignment, int widthRatio, boolean group, boolean sum) {
        this.title = title;
        this.table = table;
        this.name = name;
        this.className = className;
        this.horizontalAlignment = horizontalAlignment;
        this.widthRatio = widthRatio;
        this.order = order;
        this.formatterUpl = formatterUpl;
        this.group = group;
        this.sum = sum;
    }

    public String getTitle() {
        return title;
    }

    public String getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return className;
    }

    public HAlignType getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public int getWidthRatio() {
        return widthRatio;
    }

    public String getFormatterUpl() {
        return formatterUpl;
    }

    public OrderType getOrder() {
        return order;
    }

    public boolean isGroup() {
        return group;
    }

    public boolean isSum() {
        return sum;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String title;

        private String table;

        private String name;

        private String className;

        private String formatterUpl;

        private OrderType order;

        private HAlignType horizontalAlignment;

        private int widthRatio;

        private boolean group;

        private boolean sum;

        private Builder() {

        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder formatter(String formatterUpl) {
            this.formatterUpl = formatterUpl;
            return this;
        }

        public Builder horizontalAlignment(HAlignType horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
            return this;
        }

        public Builder widthRatio(int widthRatio) {
            this.widthRatio = widthRatio;
            return this;
        }

        public Builder order(OrderType order) {
            this.order = order;
            return this;
        }

        public Builder group(boolean group) {
            this.group = group;
            return this;
        }

        public Builder sum(boolean sum) {
            this.sum = sum;
            return this;
        }

        public ReportColumn build() throws UnifyException {
            ReportColumn reportColumn = new ReportColumn(title, table, name, className, formatterUpl, order,
                    horizontalAlignment, widthRatio, group, sum);
            return reportColumn;
        }
    }
}
