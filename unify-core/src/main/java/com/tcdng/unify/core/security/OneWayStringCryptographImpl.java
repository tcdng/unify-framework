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

import javax.xml.bind.DatatypeConverter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Tooling;

/**
 * Default implementation of a one-way string cryptograph.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Tooling(description = "Default One-way String Cryptograph")
@Component("oneway-stringcryptograph")
public class OneWayStringCryptographImpl extends AbstractOneWayCryptograph implements OneWayStringCryptograph {

    @Override
    public String forwardTransform(String value) throws UnifyException {
        return encrypt(value);
    }

    @Override
    public String reverseTransform(String value) throws UnifyException {
        return value;
    }

    @Override
    public String encrypt(String string) throws UnifyException {
        try {
            if (string != null) {
                byte encrypted[] = doEncrypt(string.getBytes("UTF-8"));
                return DatatypeConverter.printBase64Binary(encrypted);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }
}
