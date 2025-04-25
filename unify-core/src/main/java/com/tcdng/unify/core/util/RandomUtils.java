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
package com.tcdng.unify.core.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Random utilities
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class RandomUtils {

	private static String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static String DIGITS = "0123456789";
	
	private RandomUtils() {
		
	}
	
	public static String generateRandomLetters(int length) { 
		return RandomUtils.generateRandom(LETTERS, length);
	}
	
	public static String generateRandomAlphanumeric(int length) { 
		return RandomUtils.generateRandom(ALPHANUMERIC, length);
	}
	
	public static String generateRandomDigits(int length) { 
		return RandomUtils.generateRandom(DIGITS, length);
	}

	private static String generateRandom(String characters, int length) { 
		StringBuilder sb = new StringBuilder();
		try {
			SecureRandom secureRandom = SecureRandom.getInstanceStrong();
			for (int i = 0; i < length; i++) {
				sb.append(characters.charAt(secureRandom.nextInt(characters.length())));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}

}
