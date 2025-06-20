/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.util.xml.adapter;

import com.tcdng.unify.core.constant.ColorScheme;
import com.tcdng.unify.core.util.xml.AbstractEnumConstDeserializer;
import com.tcdng.unify.core.util.xml.AbstractEnumConstSerializer;
import com.tcdng.unify.core.util.xml.AbstractEnumConstXmlAdapter;

/**
 * Color scheme XML adapter.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ColorSchemeXmlAdapter extends AbstractEnumConstXmlAdapter {
    
    public static class Serializer extends AbstractEnumConstSerializer<ColorScheme> {
    	public Serializer() {
    		
    	}

    }
    
    public static class Deserializer extends AbstractEnumConstDeserializer<ColorScheme> {

		public Deserializer() {
			super(ColorScheme.class);
		}

    }
}
