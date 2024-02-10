/*
 * Copyright 2018-2024 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tcdng.unify.web.ui.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.list.AbstractListCommand;
import com.tcdng.unify.core.list.ZeroParams;
import com.tcdng.unify.web.font.FontSymbolManager;

/**
 * Button symbol list command.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("buttonsymbollist")
public class ButtonSymbolListCommand extends AbstractListCommand<ZeroParams> {

	private static final List<String> SYMBOLS = Arrays.asList("bell", "calendar", "clock", "cloud", "cloud-download",
			"cloud-upload", "copy", "cog", "cut", "edit", "file", "flag", "key", "lock", "mail", "paper-clip", "play",
			"plus", "plus-square", "save", "search", "trash", "unlock");

	@Configurable
	private FontSymbolManager fontSymbolManager;

	private static List<ListData> list;

	public ButtonSymbolListCommand() {
		super(ZeroParams.class);
	}

	@Override
	public List<? extends Listable> execute(Locale locale, ZeroParams zeroParams) throws UnifyException {
		if (list == null) {
			synchronized (ButtonSymbolListCommand.class) {
				if (list == null) {
					List<ListData> listables = new ArrayList<ListData>();
					for (String symbol : SYMBOLS) {
						listables.add(new ListData(symbol,
								fontSymbolManager.resolveSymbolHtmlHexCode(symbol) + "&nbsp;&nbsp;" + symbol));
					}

					list = Collections.unmodifiableList(listables);
				}
			}
		}

		return list;
	}

}
