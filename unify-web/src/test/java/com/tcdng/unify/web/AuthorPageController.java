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
package com.tcdng.unify.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.data.MapValues;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.data.PackableDocConfig;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;

/**
 * Test author page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/testauthor")
@UplBinding("web/test/upl/testauthor.upl")
@ResultMappings({ @ResultMapping(name = "resultMappingA", response = "!postresponse"),
		@ResultMapping(name = "resultMappingB", response = "!hidepopupresponse") })
public class AuthorPageController extends AbstractPageController {

	private String fullName;

	private Date birthDt;

	private Double height;

	private Map<String, Author> authorDatabase;

	private MapValues bio;

	public AuthorPageController() throws UnifyException {

	}

	@Action
	public String createAuthor() throws UnifyException {
		authorDatabase.put(fullName, new Author(fullName, birthDt, height));
		return this.noResult();
	}

	@Action
	public String viewAuthor() throws UnifyException {
		Author author = authorDatabase.get(fullName);
		this.birthDt = author.getBirthDt();
		this.height = author.getHeight();
		return this.noResult();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Action
	public String newAuthor() throws UnifyException {
		this.fullName = null;
		this.birthDt = null;
		this.height = null;
		return this.noResult();
	}

	public Date getBirthDt() {
		return birthDt;
	}

	public void setBirthDt(Date birthDt) {
		this.birthDt = birthDt;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public MapValues getBio() {
		return bio;
	}

	public void setBio(MapValues bio) {
		this.bio = bio;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		super.onInitialize();
		this.authorDatabase = new HashMap<String, Author>();
		this.bio = new MapValues();
		this.bio.addValue("color", String.class);
		this.bio.addValue("age", Integer.class);
		this.bio.addValue("gender", Gender.class);

		PackableDocConfig docConfig = new PackableDocConfig("ledgerConfig",
				new PackableDocConfig.FieldConfig("marker", String.class),
				new PackableDocConfig.FieldConfig("height", Double.class));

		PackableDoc pDoc = new PackableDoc(docConfig, false);
		this.bio.addValue("metric", pDoc);
	}
}
