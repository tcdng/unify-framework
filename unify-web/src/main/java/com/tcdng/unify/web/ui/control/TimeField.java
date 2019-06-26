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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.Pattern;

/**
 * Input control for presenting and capturing time.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-time")
@UplAttributes({ @UplAttribute(name = "buttonImgSrc", type = String.class, defaultValue = "$t{images/clock.png}"),
        @UplAttribute(name = "formatter", type = Formatter.class, defaultValue = "$d{!timeformat style:short}") })
public class TimeField extends AbstractTimeField {

    private DateTimeFormat[] dateTimeFormat;

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();

        Pattern[] pattern = super.getPattern();
        dateTimeFormat = new DateTimeFormat[pattern.length];
        Formatter<?> formatter = (Formatter<?>) getFormatter();
        for (int i = 0; i < pattern.length; i++) {
            Pattern fp = pattern[i];
            if (!fp.isFiller()) {
                dateTimeFormat[i] =
                        formatter.getFormatHelper().getSubPatternDateTimeFormat(fp.getPattern(), formatter.getLocale());
            }
        }
    }

    @Override
    public Pattern[] getPattern() throws UnifyException {
        Pattern[] pattern = super.getPattern();
        for (int i = 0; i < pattern.length; i++) {
            Pattern fp = pattern[i];
            if (!fp.isFiller()) {
                fp.setTarget(getPrefixedId("timi_") + i);
            }
        }
        return pattern;
    }

    public DateTimeFormat[] getDateTimeFormat() {
        return dateTimeFormat;
    }
}
