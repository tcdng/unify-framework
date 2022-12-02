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
package com.tcdng.unify.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * String token utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class StringTokenUtils {

	private StringTokenUtils() {
		
	}

    public static String buildParameterizedString(List<StringToken> tokens) {
    	if (!tokens.isEmpty()) {
    		StringBuilder sb = new StringBuilder();
    		for (StringToken token: tokens) {
    			if (token.isParam()) {
    				sb.append("{{").append(token.getToken()).append("}}");
    			} else {
    				sb.append(token.getToken());
    			}
    		}
    		return sb.toString();
    	}
    	
    	return null;
    }
    
	public static List<StringToken> breakdownParameterizedString(String string) {
		if (string == null) {
			return Collections.emptyList();
		}

		if ("\n".equals(string)) {
			return Arrays.asList(new NewlineToken());
		}

		List<StringToken> tokenList = new ArrayList<StringToken>();
		int index = 0;
		int pStartIndex = 0;
		while ((pStartIndex = string.indexOf("{{", index)) >= 0) {
			int pEndIndex = string.indexOf("}}", pStartIndex);
			if (pEndIndex <= 0) {
				throw new RuntimeException("Invalid parameterized string: parameter closure expected.");
			}

			if ((pEndIndex - pStartIndex) < 4) {
				throw new RuntimeException(
						"Invalid parameterized string: parameter name expected at index " + (pStartIndex + 1) + ".");
			}

			if (index < pStartIndex) {
				tokenList.addAll(StringTokenUtils.breakdownTextString(string.substring(index, pStartIndex)));
			}

			tokenList.add(ParamToken.getParamToken(string.substring(pStartIndex + 2, pEndIndex)));

			index = pEndIndex + 2;
		}

		if (index < string.length()) {
			tokenList.addAll(StringTokenUtils.breakdownTextString((string.substring(index))));
		}

		return tokenList;
	}

	private static List<StringToken> breakdownTextString(String text) {		
		List<StringToken> resultList = new ArrayList<StringToken>();
		String[] parts = text.split("\n", -1);
		int lim = parts.length - 1;
		for (int i = 0; i < lim; i++) {
			if (parts[i].length() > 0) {
				resultList.add(new TextToken(parts[i]));
			}

			resultList.add(new NewlineToken());
		}

		if (parts[lim].length() > 0) {
			resultList.add(new TextToken(parts[lim]));
		}

		return resultList;
	}
}
