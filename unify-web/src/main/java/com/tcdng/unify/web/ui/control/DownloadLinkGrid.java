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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.file.FileFilter;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.constant.RealPathConstants;
import com.tcdng.unify.web.ui.AbstractMultiControl;

/**
 * Application download link grid widget. Presents all file resources in
 * application container download folder as download links.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-downloadlinkgrid")
@UplAttributes({ @UplAttribute(name = "columns", type = int.class) })
public class DownloadLinkGrid extends AbstractMultiControl {

    public int getColumns() throws UnifyException {
        return getUplAttribute(int.class, "columns");
    }

    @Override
    public boolean isLayoutCaption() throws UnifyException {
        return false;
    }

    public List<String> getResourceList() throws UnifyException {
        File file = new File(
                IOUtils.buildFilename(getUnifyComponentContext().getWorkingPath(), RealPathConstants.DOWNLOAD_FOLDER));
        if (file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter(true));
            if (files != null && files.length > 0) {
                List<String> resourceList = new ArrayList<String>();
                for (File resourceFile : files) {
                    resourceList.add(resourceFile.getName());
                }

                return resourceList;
            }
        }

        return Collections.emptyList();
    }
}
