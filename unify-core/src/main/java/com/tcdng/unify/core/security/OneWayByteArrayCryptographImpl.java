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
package com.tcdng.unify.core.security;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Tooling;

/**
 * Default implementation of a one-way byte array cryptograph.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "Default One-way Bytes Cryptograph")
@Component("oneway-bytearraycryptograph")
public class OneWayByteArrayCryptographImpl extends AbstractOneWayCryptograph implements OneWayByteArrayCryptograph {

    @Override
    public byte[] forwardTransform(byte[] value) throws UnifyException {
        return encrypt(value);
    }

    @Override
    public byte[] reverseTransform(byte[] value) throws UnifyException {
        return value;
    }

    @Override
    public byte[] encrypt(byte[] toEncrypt) throws UnifyException {
        return doEncrypt(toEncrypt);
    }
}
