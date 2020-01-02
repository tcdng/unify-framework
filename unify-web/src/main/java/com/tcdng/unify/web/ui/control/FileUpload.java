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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.ui.AbstractControl;

/**
 * A file upload widget used for selecting files and uploading to server.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-fileupload")
@UplAttributes({ @UplAttribute(name = "accept", type = String.class),
        @UplAttribute(name = "acceptBinding", type = String.class),
        @UplAttribute(name = "multiple", type = boolean.class),
        @UplAttribute(name = "selectOnly", type = boolean.class),
        @UplAttribute(name = "maxSize", type = int.class),
        @UplAttribute(name = "maxSizeBinding", type = String.class),
        @UplAttribute(name = "uploadPath", type = String.class),
        @UplAttribute(name = "browseCaption", type = String.class, defaultVal = "$m{button.browse}"),
        @UplAttribute(name = "uploadCaption", type = String.class, defaultVal = "$m{button.upload}") })
public class FileUpload extends AbstractControl {

    public String getAccept() throws UnifyException {
        return getUplAttribute(String.class, "accept", "acceptBinding");
    }

    public int getMaxSize() throws UnifyException {
        return getUplAttribute(int.class, "maxSize", "maxSizeBinding");
    }

    public String getUploadURL() throws UnifyException {
        return getUplAttribute(String.class, "uploadPath");
    }

    public String getBrowseCaption() throws UnifyException {
        return getUplAttribute(String.class, "browseCaption");
    }

    public String getUploadCaption() throws UnifyException {
        return getUplAttribute(String.class, "uploadCaption");
    }

    public boolean isSelectOnly() throws UnifyException {
        return getUplAttribute(boolean.class, "selectOnly");
    }

    public String getButtonId() throws UnifyException {
        return getPrefixedId("btn_");
    }

    public String getSpanId() throws UnifyException {
        return getPrefixedId("spn_");
    }

    public String getUploadButtonId() throws UnifyException {
        return getPrefixedId("btnu_");
    }
}
