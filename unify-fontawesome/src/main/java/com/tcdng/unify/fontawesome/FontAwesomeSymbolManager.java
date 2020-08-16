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

    public List<String> getFontResources() throws UnifyException {
        return Arrays.asList("webfonts/fa-regular-400.woff", "webfonts/fa-solid-900.woff");
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();
        
        // Register symbols by name
        registerSymbol("angle-double-down", "&#xf103;");
        registerSymbol("angle-double-left", "&#xf100;");
        registerSymbol("angle-double-right", "&#xf101;");
        registerSymbol("angle-double-up", "&#xf102;");
        registerSymbol("angle-down", "&#xf107;");
        registerSymbol("angle-left", "&#xf104;");
        registerSymbol("angle-right", "&#xf105;");
        registerSymbol("angle-up", "&#xf106;");
        registerSymbol("arrow-down", "&#xf063;");
        registerSymbol("arrow-left", "&#xf060;");
        registerSymbol("arrow-right", "&#xf061;");
        registerSymbol("arrow-up", "&#xf062;");
        registerSymbol("backward", "&#xf04a;");
        registerSymbol("backward-fast", "&#xf049;");
        registerSymbol("bell", "&#xf0f3;");
        registerSymbol("bell-slash", "&#xf1f6;");
        registerSymbol("calendar", "&#xf133;");
        registerSymbol("calendar-alt", "&#xf073;");
        registerSymbol("caret-down", "&#xf0d7;");
        registerSymbol("caret-left", "&#xf0d9;");
        registerSymbol("caret-right", "&#xf0da;");
        registerSymbol("caret-up", "&#xf0d8;");
        registerSymbol("chevron-down", "&#xf078;");
        registerSymbol("chevron-left", "&#xf053;");
        registerSymbol("chevron-right", "&#xf054;");
        registerSymbol("chevron-up", "&#xf077;");
        registerSymbol("clock", "&#xf017;");
        registerSymbol("cloud", "&#xf0c2;");
        registerSymbol("cloud-download", "&#xf381;");
        registerSymbol("cloud-upload", "&#xf382;");
        registerSymbol("copy", "&#xf0c5;");
        registerSymbol("cog", "&#xf013;");
        registerSymbol("cogs", "&#xf085;");
        registerSymbol("credit-card", "&#xf09d;");
        registerSymbol("cross", "&#xf00d;");
        registerSymbol("cut", "&#xf0c4;");
        registerSymbol("database", "&#xf1c0;");
        registerSymbol("desktop", "&#xf108;");
        registerSymbol("edit", "&#xf044;");
        registerSymbol("file", "&#xf15b;");
        registerSymbol("file-excel", "&#x1c3;");
        registerSymbol("file-pdf", "&#xf1c1;");
        registerSymbol("file-word", "&#xf1c2;");
        registerSymbol("filter", "&#xf0b0;");
        registerSymbol("folder", "&#xf07b;");
        registerSymbol("folder-open", "&#xf07c;");
        registerSymbol("forward", "&#xf04e;");
        registerSymbol("forward-fast", "&#xf050;");
        registerSymbol("harddisk", "&#xf0a0;");
        registerSymbol("id-card", "&#xf2c2;");
        registerSymbol("image", "&#xf03e;");
        registerSymbol("mail", "&#xf0e0;");
        registerSymbol("minus", "&#xf068;");
        registerSymbol("money-bill", "&#xf0d6;");
        registerSymbol("paper-clip", "&#xf0c6;");
        registerSymbol("plus", "&#xf067;");
        registerSymbol("save", "&#xf0c7;");
        registerSymbol("search", "&#xf002;");
        registerSymbol("sign-in", "&#xf2f6;");
        registerSymbol("sign-out", "&#xf2f5;");
        registerSymbol("sort", "&#xf0dc;");
        registerSymbol("swap", "&#xf362;");
        registerSymbol("sync", "&#xf2f1;");
        registerSymbol("thumbtack", "&#xf08d;");
        registerSymbol("undo", "&#xf2ea;");
        registerSymbol("user", "&#xf007;");
    }

}
