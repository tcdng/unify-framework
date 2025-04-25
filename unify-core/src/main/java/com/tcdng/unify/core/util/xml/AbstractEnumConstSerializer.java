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
package com.tcdng.unify.core.util.xml;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tcdng.unify.common.constants.EnumConst;

/**
 * Convenient abstract enumeration constants serializer adapter class.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractEnumConstSerializer<T extends EnumConst> extends JsonSerializer<T> {

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value != null ? value.name() : null);
	}
	
}