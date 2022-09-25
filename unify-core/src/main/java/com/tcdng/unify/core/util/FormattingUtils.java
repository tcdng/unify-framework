/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.util;

/**
 * Provides utility methods for string manipulation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class FormattingUtils {

    private FormattingUtils() {

    }

    public static String makeParsableGroupedAmount(String amount, String groupSeparator) {
    	if (amount != null && !amount.isEmpty()) {
    		return amount.replaceAll(groupSeparator, "");
    	}
    	
    	return amount;
    }

    public static String makeParsableNegativeAmount(String amount) {
    	if (amount != null && !amount.isEmpty()) {
    		int last = amount.length() - 1;
    		if (amount.charAt(0) == '(' && amount.charAt(last) != ')') {
    			return amount + ')';
    		}
    			
    		if (amount.charAt(last) == ')' && amount.charAt(0) != '(') {
    			return '(' + amount;
    		}
    	}
    	
    	return amount;
    }
}
