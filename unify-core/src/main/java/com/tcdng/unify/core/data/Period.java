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

package com.tcdng.unify.core.data;

import com.tcdng.unify.core.constant.FrequencyUnit;

/**
 * Period object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Period {

    private FrequencyUnit unit;

    private int magnitude;

    public Period(FrequencyUnit unit, int magnitude) {
        this.unit = unit;
        this.magnitude = magnitude;
    }

    public FrequencyUnit getUnit() {
        return unit;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (unit != null) {
            sb.append(unit.code());
        }

        if (sb.length() > 0) {
            sb.append(" ");
        }

        sb.append(magnitude);

        return sb.toString();
    }
}
