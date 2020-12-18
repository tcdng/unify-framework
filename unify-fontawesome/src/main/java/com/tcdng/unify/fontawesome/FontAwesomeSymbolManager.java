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

package com.tcdng.unify.fontawesome;

import java.util.Arrays;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.font.AbstractFontSymbolManager;

/**
 * Font Awesome symbol manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(FontAwesomeApplicationComponents.FONTAWESOME_FONTSYMBOLMANAGER)
public class FontAwesomeSymbolManager extends AbstractFontSymbolManager {

    @Override
    public List<String> getFontResources() throws UnifyException {
        // Currently uses font awesome 5.14.1
        return Arrays.asList("webfonts/fa-solid-900.woff", "webfonts/fa-regular-400.woff");
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();
        
        // Register symbols by name
        registerSymbol("angle-double-down", "f103");
        registerSymbol("angle-double-left", "f100");
        registerSymbol("angle-double-right", "f101");
        registerSymbol("angle-double-up", "f102");
        registerSymbol("angle-down", "f107");
        registerSymbol("angle-left", "f104");
        registerSymbol("angle-right", "f105");
        registerSymbol("angle-up", "f106");
        registerSymbol("arrow-down", "f063");
        registerSymbol("arrow-left", "f060");
        registerSymbol("arrow-right", "f061");
        registerSymbol("arrow-up", "f062");
        registerSymbol("backward", "f04a");
        registerSymbol("backward-fast", "f049");
        registerSymbol("bell", "f0f3");
        registerSymbol("bell-slash", "f1f6");
        registerSymbol("buffer", "f837");
        registerSymbol("calendar", "f133");
        registerSymbol("calendar-alt", "f073");
        registerSymbol("caret-down", "f0d7");
        registerSymbol("caret-left", "f0d9");
        registerSymbol("caret-right", "f0da");
        registerSymbol("caret-up", "f0d8");
        registerSymbol("chart-area", "f1fe");
        registerSymbol("chart-bar", "f080");
        registerSymbol("chart-pie", "f200");
        registerSymbol("chevron-down", "f078");
        registerSymbol("chevron-left", "f053");
        registerSymbol("chevron-right", "f054");
        registerSymbol("chevron-up", "f077");
        registerSymbol("clock", "f017");
        registerSymbol("cloud", "f0c2");
        registerSymbol("cloud-download", "f381");
        registerSymbol("cloud-upload", "f382");
        registerSymbol("copy", "f0c5");
        registerSymbol("cog", "f013");
        registerSymbol("cogs", "f085");
        registerSymbol("compact-disk", "f51f");
        registerSymbol("credit-card", "f09d");
        registerSymbol("cross", "f00d");
        registerSymbol("cubes", "f1b3");
        registerSymbol("cut", "f0c4");
        registerSymbol("database", "f1c0");
        registerSymbol("desktop", "f108");
        registerSymbol("directions", "f5eb");
        registerSymbol("door-open", "f52b");
        registerSymbol("edit", "f044");
        registerSymbol("equals", "f52c");
        registerSymbol("exchange-alt", "f362");
        registerSymbol("exclamation-circle", "f06a");
        registerSymbol("file", "f15b");
        registerSymbol("file-excel", "1c3");
        registerSymbol("file-pdf", "f1c1");
        registerSymbol("file-word", "f1c2");
        registerSymbol("filter", "f0b0");
        registerSymbol("flag", "f024");
        registerSymbol("flag-checkered", "f11e");
        registerSymbol("folder", "f07b");
        registerSymbol("folder-open", "f07c");
        registerSymbol("forward", "f04e");
        registerSymbol("forward-fast", "f050");
        registerSymbol("globe-africa", "f57c");
        registerSymbol("hat-wizard", "f6e8");
        registerSymbol("harddisk", "f0a0");
        registerSymbol("history", "f1da");
        registerSymbol("house-user", "e065");
        registerSymbol("id-card", "f2c2");
        registerSymbol("image", "f03e");
        registerSymbol("key", "f084");
        registerSymbol("laptop-house", "e066");
        registerSymbol("list", "f03a");
        registerSymbol("magic", "f0d0");
        registerSymbol("mail", "f0e0");
        registerSymbol("mail-bulk", "f674");
        registerSymbol("minus", "f068");
        registerSymbol("money-bill", "f0d6");
        registerSymbol("network-wired", "f6ff");
        registerSymbol("newspaper", "f1ea");
        registerSymbol("outdent", "f03b");
        registerSymbol("paper-clip", "f0c6");
        registerSymbol("play", "f04b");
        registerSymbol("plus", "f067");
        registerSymbol("plus-square", "f0fe");
        registerSymbol("project-diagram", "f542");
        registerSymbol("redo", "f01e");
        registerSymbol("redo-alt", "f2f9");
        registerSymbol("rocket", "f135");
        registerSymbol("satellite-disk", "f7c0");
        registerSymbol("save", "f0c7");
        registerSymbol("search", "f002");
        registerSymbol("sign-in", "f2f6");
        registerSymbol("sign-out", "f2f5");
        registerSymbol("site-map", "f0e8");
        registerSymbol("sort", "f0dc");
        registerSymbol("stream", "f550");
        registerSymbol("swap", "f362");
        registerSymbol("sync", "f2f1");
        registerSymbol("table", "f0ce");
        registerSymbol("tasks", "f0ae");
        registerSymbol("thumbtack", "f08d");
        registerSymbol("times-circle", "f057");
        registerSymbol("undo", "f2ea");
        registerSymbol("user", "f007");
        registerSymbol("users", "f0c0");
        registerSymbol("user-check", "f4fc");
        registerSymbol("user-edit", "f4ff");
        registerSymbol("user-tag", "f507");
        registerSymbol("vector-square", "f5cb");
        registerSymbol("window-maximize", "f2d0");
        registerSymbol("window-restore", "f2d2");
    }

}
