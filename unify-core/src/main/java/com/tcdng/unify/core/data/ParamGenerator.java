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

package com.tcdng.unify.core.data;

import com.tcdng.unify.common.util.ParamToken;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Parameter generator.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ParamGenerator extends UnifyComponent {

	/**
	 * Generates parameter value
	 * 
	 * @param itemReader   the item reader
	 * @param parentReader the parent reader
	 * @param token        the generator token
	 * @return the generated parameter value
	 * @throws UnifyException if an error occurs
	 */
	Object generate(ValueStoreReader itemReader, ValueStoreReader parentReader, ParamToken token) throws UnifyException;
}
