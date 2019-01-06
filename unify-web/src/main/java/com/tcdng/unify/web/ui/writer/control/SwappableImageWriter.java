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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.SwappableImage;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Swappable image writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(SwappableImage.class)
@Component("swappableimage-writer")
public class SwappableImageWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        SwappableImage swappableImage = (SwappableImage) widget;
        String[] src = swappableImage.getSrc();
        int index = swappableImage.getValue(int.class);
        if (index < 0 || index >= src.length) {
            index = 0;
        }
        writer.write("<img");
        writeTagAttributes(writer, swappableImage);
        writer.write(" src=\"");
        writer.writeFileImageContextURL(src[index]);
        if (swappableImage.isAlwaysFetch()) {
            writer.writeURLParameter("morsic", String.valueOf(System.currentTimeMillis()));
        }
        writer.write("\">");
    }
}
