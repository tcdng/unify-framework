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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;

/**
 * Abstract implementation of a cryptograph providing one-way encryption.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Singleton(false)
public class AbstractOneWayCryptograph extends AbstractUnifyComponent {

    @Configurable("The Code Department")
    private String encryptionKey;

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected final byte[] doEncrypt(byte[] toEncrypt) throws UnifyException {
        if (toEncrypt != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(encryptionKey.getBytes("UTF-8"));
                digest.update(toEncrypt);
                return digest.digest();
            } catch (NoSuchAlgorithmException e) {
                throwOperationErrorException(e);
            } catch (UnsupportedEncodingException e) {
                throwOperationErrorException(e);
            }
        }
        return null;
    }
}
