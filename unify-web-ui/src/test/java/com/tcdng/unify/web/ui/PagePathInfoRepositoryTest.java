/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.web.AbstractUnifyWebTest;

/**
 * Page path information repository test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PagePathInfoRepositoryTest extends AbstractUnifyWebTest {

    private PagePathInfoRepository pir;

    @Test
    public void testGetPagePathInfo() throws Exception {
        PagePathInfo pagePathInfo = pir.getPagePathInfo("/testauthor");
        assertNotNull(pagePathInfo);
        assertEquals("/testauthor", pagePathInfo.getPathId());
        assertNull(pagePathInfo.getColorScheme());
        assertEquals("/testauthor/openPage", pagePathInfo.getOpenPagePath());
        assertEquals("/testauthor/savePage", pagePathInfo.getSavePagePath());
        assertEquals("/testauthor/closePage", pagePathInfo.getClosePagePath());
        assertFalse(pagePathInfo.isRemoteSave());
    }

    @Override
    protected void onSetup() throws Exception {
        pir = (PagePathInfoRepository) getComponent(WebUIApplicationComponents.APPLICATION_PAGEPATHINFOREPOSITORY);
    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
