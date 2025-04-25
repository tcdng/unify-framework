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
package com.tcdng.unify.web.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.web.AbstractUnifyWebTest;
import com.tcdng.unify.web.ui.widget.DocumentLayout;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Page manager test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PageManagerTest extends AbstractUnifyWebTest {

    @Test
    public void testCreatePage() throws Exception {
        PageManager pageManager = (PageManager) this.getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
        Page page = pageManager.createPage(Locale.getDefault(), "/testauthor");
        assertNotNull(page);
    }

    @Test
    public void testPageAttributes() throws Exception {
        PageManager pageManager = (PageManager) this.getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
        Page page = pageManager.createPage(Locale.getDefault(), "/testauthor");
        DocumentLayout documentLayout = page.getUplAttribute(DocumentLayout.class, "layout");
        assertNotNull(documentLayout);
        assertEquals("ui-desktoptype0", documentLayout.getName());
    }

    @Test
    public void testPageReferencesByLongName() throws Exception {
        PageManager pageManager = (PageManager) this.getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
        Page page = pageManager.createPage(Locale.getDefault(), "/testauthor");

        // Page level
        assertEquals(15, page.getWidgetLongNames().size());

        Widget uici = page.getWidgetByLongName("/testauthor.mainPanel");
        assertEquals("/testauthor.mainPanel", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertTrue(uici.isValueConforming(page));
        assertFalse(uici.isFixedConforming());

        uici = page.getWidgetByLongName("/testauthor.fullName");
        assertEquals("/testauthor.fullName", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertFalse(uici.isValueConforming(page));
        assertFalse(uici.isFixedConforming());

        uici = page.getWidgetByLongName("/testauthor.birthDt");
        assertNotNull(uici);
        assertEquals("/testauthor.birthDt", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertFalse(uici.isValueConforming(page));
        assertFalse(uici.isFixedConforming());

        uici = page.getWidgetByLongName("/testauthor.height");
        assertNotNull(uici);
        assertEquals("/testauthor.height", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertFalse(uici.isValueConforming(page));
        assertFalse(uici.isFixedConforming());

        // Panel level
        Panel panel = (Panel) page.getWidgetByLongName("/testauthor.mainPanel");
        assertEquals(3, panel.getWidgetLongNames().size());

        uici = panel.getWidgetByLongName("/testauthor.fullName");
        assertNotNull(uici);
        assertEquals("/testauthor.fullName", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertTrue(uici.isValueConforming(panel));
        assertFalse(uici.isFixedConforming());

        uici = panel.getWidgetByLongName("/testauthor.birthDt");
        assertNotNull(uici);
        assertEquals("/testauthor.birthDt", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertTrue(uici.isValueConforming(panel));
        assertFalse(uici.isFixedConforming());

        uici = panel.getWidgetByLongName("/testauthor.height");
        assertNotNull(uici);
        assertEquals("/testauthor.height", uici.getLongName());
        assertEquals(page.getLongName(), uici.getParentLongName());
        assertTrue(uici.isConforming());
        assertTrue(uici.isValueConforming(panel));
        assertFalse(uici.isFixedConforming());
    }

    @Test
    public void testPageReferencesByShortName() throws Exception {
        PageManager pageManager = (PageManager) this.getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
        Page page = pageManager.createPage(Locale.getDefault(), "/testauthor");

        // Page level
        Widget uic = page.getWidgetByShortName("mainPanel");
        assertNotNull(uic);
        assertEquals("/testauthor.mainPanel", uic.getLongName());

        uic = page.getWidgetByShortName("fullName");
        assertNotNull(uic);
        assertEquals("/testauthor.fullName", uic.getLongName());

        uic = page.getWidgetByShortName("birthDt");
        assertNotNull(uic);
        assertEquals("/testauthor.birthDt", uic.getLongName());

        uic = page.getWidgetByShortName("height");
        assertNotNull(uic);
        assertEquals("/testauthor.height", uic.getLongName());

        // Panel level
        Panel panel = (Panel) page.getWidgetByShortName("mainPanel");

        uic = panel.getWidgetByShortName("fullName");
        assertNotNull(uic);
        assertEquals("/testauthor.fullName", uic.getLongName());

        uic = panel.getWidgetByShortName("birthDt");
        assertNotNull(uic);
        assertEquals("/testauthor.birthDt", uic.getLongName());

        uic = panel.getWidgetByShortName("height");
        assertNotNull(uic);
        assertEquals("/testauthor.height", uic.getLongName());
    }

    @Test
    public void testPageGetPanel() throws Exception {
        PageManager pageManager = (PageManager) this.getComponent(WebUIApplicationComponents.APPLICATION_PAGEMANAGER);
        Page page = pageManager.createPage(Locale.getDefault(), "/testauthor");

        Panel panel1 = page.getPanelByLongName("/testauthor.mainPanel");
        assertEquals("/testauthor.mainPanel", panel1.getLongName());

        Panel panel2 = page.getPanelByShortName("mainPanel");
        assertEquals("/testauthor.mainPanel", panel2.getLongName());

        assertEquals(panel1, panel2);
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
