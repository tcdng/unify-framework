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

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.data.MapValues;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.data.PackableDocConfig;
import com.tcdng.unify.web.Author;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;

/**
 * Test author page controller.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("/testauthor")
@UplBinding("web/test/upl/testauthor.upl")
@ResultMappings({ @ResultMapping(name = "resultMappingA", response = "!postresponse"),
		@ResultMapping(name = "resultMappingB", response = "!hidepopupresponse") })
public class AuthorPageController extends AbstractPageController<AuthorPageBean> {

	private Map<String, Author> authorDatabase;

	public AuthorPageController() {
		super(AuthorPageBean.class);
	}

	@Action
	public String createAuthor() throws UnifyException {
		AuthorPageBean authorPageBean = getPageBean();
		authorDatabase.put(authorPageBean.getFullName(),
				new Author(authorPageBean.getFullName(), authorPageBean.getBirthDt(), authorPageBean.getHeight()));
		return noResult();
	}

	@Action
	public String viewAuthor() throws UnifyException {
		AuthorPageBean authorPageBean = getPageBean();
		Author author = authorDatabase.get(authorPageBean.getFullName());
		authorPageBean.setBirthDt(author.getBirthDt());
		authorPageBean.setHeight(author.getHeight());
		return noResult();
	}

	@Action
	public String newAuthor() throws UnifyException {
		reset();
		return noResult();
	}

	@Override
	protected void onInitPage() throws UnifyException {
		AuthorPageBean authorPageBean = getPageBean();
		authorDatabase = new HashMap<String, Author>();
		MapValues bio = new MapValues();
		bio.addValue("color", String.class);
		bio.addValue("age", Integer.class);
		bio.addValue("gender", Gender.class);

		PackableDocConfig docConfig = PackableDocConfig.newBuilder("ledgerConfig")
				.addFieldConfig("marker", DataType.STRING).addFieldConfig("height", DataType.DOUBLE).build();

		PackableDoc pDoc = new PackableDoc(docConfig, false);
		bio.addValue("metric", PackableDoc.class, pDoc);
		authorPageBean.setBio(bio);
	}

}
