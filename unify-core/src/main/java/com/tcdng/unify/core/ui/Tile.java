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
package com.tcdng.unify.core.ui;

/**
 * Data object that represents a card. A card object is a user interface object
 * that displays a rectangle bounding an image with some text.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Tile {

	private String imageSrc;

	private String caption;

	private String actionPath;

	private byte[] image;

	private boolean landscape;

	public Tile(String imageSrc, String caption, String actionPath, byte[] image, boolean landscape) {
		this.imageSrc = imageSrc;
		this.caption = caption;
		this.actionPath = actionPath;
		this.image = image;
		this.landscape = landscape;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public String getCaption() {
		return caption;
	}

	public String getActionPath() {
		return actionPath;
	}

	public byte[] getImage() {
		return image;
	}

	public boolean isLandscape() {
		return landscape;
	}
}
