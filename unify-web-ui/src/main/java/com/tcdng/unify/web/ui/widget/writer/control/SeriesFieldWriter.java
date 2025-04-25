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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.util.WebRegexUtils;
import com.tcdng.unify.web.ui.widget.control.SeriesField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * Series field writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(SeriesField.class)
@Component("seriesfield-writer")
public class SeriesFieldWriter extends TextFieldWriter {

    @Override
    protected String getFormatRegex(TextField textField) throws UnifyException {
        return WebRegexUtils.getSeriesRegex();
    }
}
