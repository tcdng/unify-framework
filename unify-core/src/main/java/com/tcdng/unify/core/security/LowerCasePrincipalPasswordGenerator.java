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
package com.tcdng.unify.core.security;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Lower case principal password generator. Always generates a password equal to
 * the supplied principal in lower case. Ignores length parameter.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = "lowercaseprincipal-passwordgenerator", description = "$m{passwordgenerator.lowercaseprincipal}")
public class LowerCasePrincipalPasswordGenerator extends AbstractUnifyComponent implements PasswordGenerator {

    @Override
    public String generatePassword(String principal, int length) throws UnifyException {
        return principal.toLowerCase();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
