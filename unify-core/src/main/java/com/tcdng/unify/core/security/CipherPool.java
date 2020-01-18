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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.tcdng.unify.core.data.AbstractPool;

/**
 * Cipher pool.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CipherPool extends AbstractPool<Cipher> {

    private String transformation;

    private String key;

    private int mode;

    public CipherPool(String transformation, String key, int mode) {
        this(transformation, key, mode, 2000, 1, 4);
    }

    public CipherPool(String transformation, String key, int mode, long getTimeout, int minSize, int maxSize) {
        super(getTimeout, minSize, maxSize);
        this.transformation = transformation;
        this.key = key;
        this.mode = mode;
    }

    @Override
    protected Cipher createObject(Object... params) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(this.key.toCharArray());
        SecretKeyFactory kf = SecretKeyFactory.getInstance(this.transformation);
        SecretKey key = kf.generateSecret(keySpec);

        byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
                (byte) 0xf2 };
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);

        Cipher cipher = Cipher.getInstance(this.transformation);
        cipher.init(this.mode, key, paramSpec);
        return cipher;
    }

    @Override
    protected void onGetObject(Cipher object, Object... params) throws Exception {

    }

    @Override
    protected void destroyObject(Cipher object) {

    }
}
