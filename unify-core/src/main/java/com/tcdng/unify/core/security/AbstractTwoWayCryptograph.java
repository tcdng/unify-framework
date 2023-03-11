/*
 * Copyright 2018-2023 The Code Department.
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

import javax.crypto.Cipher;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;

/**
 * Abstract implementation of a cryptograph providing two-way encryption.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractTwoWayCryptograph extends AbstractUnifyComponent {

    @Configurable("The Code Department")
    private String encryptionKey;

    /** The encrypt cipher pool */
    private CipherPool ecipherPool;

    /** The decrypt cipher pool */
    private CipherPool dcipherPool;

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        String encryptionKey = getEncryptionKey();
        ecipherPool = new CipherPool("PBEWithMD5AndDES", encryptionKey, Cipher.ENCRYPT_MODE);
        dcipherPool = new CipherPool("PBEWithMD5AndDES", encryptionKey, Cipher.DECRYPT_MODE);
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected String getEncryptionKey() throws UnifyException {
        return encryptionKey;
    }

    protected final byte[] doEncrypt(byte[] toEncrypt) throws UnifyException {
        if (toEncrypt != null) {
            Cipher ecipher = ecipherPool.borrowObject();
            try {
                return ecipher.doFinal(toEncrypt);
            } catch (Exception e) {
                throwOperationErrorException(e);
            } finally {
                ecipherPool.returnObject(ecipher);
            }
        }
        return null;
    }

    protected final byte[] doDecrypt(byte[] toDecrypt) throws UnifyException {
        if (toDecrypt != null) {
            Cipher dcipher = dcipherPool.borrowObject();
            try {
                return dcipher.doFinal(toDecrypt);
            } catch (Exception e) {
                throwOperationErrorException(e);
            } finally {
                dcipherPool.returnObject(dcipher);
            }
        }
        return null;
    }
}
