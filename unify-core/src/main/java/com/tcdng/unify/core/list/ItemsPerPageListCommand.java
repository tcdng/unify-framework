/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.core.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.ListNumberData;
import com.tcdng.unify.core.data.Listable;

/**
 * Items per page list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("itemsperpagelist")
public class ItemsPerPageListCommand extends AbstractZeroParamsListCommand {

	private static final List<Listable> ITEMSPERPAGE = new ArrayList<Listable>();

	static {
		ITEMSPERPAGE.add(new ListNumberData(50));
		ITEMSPERPAGE.add(new ListNumberData(100));
		ITEMSPERPAGE.add(new ListNumberData(200));
		ITEMSPERPAGE.add(new ListNumberData(500));
		ITEMSPERPAGE.add(new ListNumberData(1000));
		ITEMSPERPAGE.add(new ListData("-1", "All"));
	}

	@Override
	public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
		return ITEMSPERPAGE;
	}
}
