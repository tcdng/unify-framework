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
package com.tcdng.unify.core.security;

import java.util.Random;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Random alphanumeric password generator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "randomalphanumeric-passwordgenerator", description = "$m{passwordgenerator.randomalphanumeric}")
public class RandomAlphanumericPasswordGenerator extends AbstractUnifyComponent implements PasswordGenerator {

    private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";

    private Random random;

    public RandomAlphanumericPasswordGenerator() {
        random = new Random();
    }

    @Override
    public String generatePassword(String principal, int length) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
