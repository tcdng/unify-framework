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

package com.tcdng.unify.core.convert;

import com.tcdng.unify.convert.converters.AbstractConverter;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.core.data.IndexedTarget;

/**
 * A value to indexed target converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class IndexedTargetConverter extends AbstractConverter<IndexedTarget> {

    @Override
    protected IndexedTarget doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof IndexedTarget) {
            return (IndexedTarget) value;
        }

        if (value instanceof String) {
        	String target = ((String) value).trim();
        	final String[] parts = target.split(":");
        	if (parts.length > 1) {
        		final int targetIndex = Integer.parseInt(parts[1]);
        		final String binding = parts.length > 2 ?  parts[2] : null;
        		String _target = parts[0];
        		final int index = _target.lastIndexOf('_');
        		int tabIndex = -1;
        		if(index > 0) {
        			try {
						tabIndex = Integer.parseInt(_target.substring(index + 1));
						_target = _target.substring(0, index);
					} catch (NumberFormatException e) {
					}
        		}
        		
            	return new IndexedTarget(_target, binding, targetIndex, tabIndex);
        	}

        	return new IndexedTarget(target, null, -1, -1);
        }

        return null;
    }

}
