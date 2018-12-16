/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Default implementation of a byte array cryptograph.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_BYTEARRAYCRYPTOGRAPH)
public class TwoWayByteArrayCryptographImpl extends AbstractTwoWayCryptograph implements TwoWayByteArrayCryptograph {

    @Override
    public byte[] forwardTransform(byte[] value) throws UnifyException {
        return encrypt(value);
    }

    @Override
    public byte[] reverseTransform(byte[] value) throws UnifyException {
        return decrypt(value);
    }

    @Override
    public byte[] encrypt(byte[] array) throws UnifyException {
        return doEncrypt(array);
    }

    @Override
    public byte[] decrypt(byte[] array) throws UnifyException {
        return doDecrypt(array);
    }

}
