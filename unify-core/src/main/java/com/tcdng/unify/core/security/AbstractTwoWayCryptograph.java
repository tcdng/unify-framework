/*
 * Copyright 2014 The Code Department
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

/**
 * Abstract implementation of a cryptograph providing two-way encryption.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTwoWayCryptograph extends AbstractUnifyComponent {

	@Configurable("The Code Department")
	private String encryptionKey;

	/** The encrypt cipher pool */
	private CipherPool ecipherPool;

	/** The decrypt cipher pool */
	private CipherPool dcipherPool;

	@Override
	protected void onInitialize() throws UnifyException {
		String encryptionKey = this.getEncryptionKey();
		this.ecipherPool = new CipherPool("PBEWithMD5AndDES", encryptionKey, Cipher.ENCRYPT_MODE);
		this.dcipherPool = new CipherPool("PBEWithMD5AndDES", encryptionKey, Cipher.DECRYPT_MODE);
	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected String getEncryptionKey() throws UnifyException {
		return this.encryptionKey;
	}

	protected final byte[] doEncrypt(byte[] toEncrypt) throws UnifyException {
		if (toEncrypt != null) {
			Cipher ecipher = this.ecipherPool.borrowObject();
			try {
				return ecipher.doFinal(toEncrypt);
			} catch (Exception e) {
				this.throwOperationErrorException(e);
			} finally {
				this.ecipherPool.returnObject(ecipher);
			}
		}
		return null;
	}

	protected final byte[] doDecrypt(byte[] toDecrypt) throws UnifyException {
		if (toDecrypt != null) {
			Cipher dcipher = this.dcipherPool.borrowObject();
			try {
				return dcipher.doFinal(toDecrypt);
			} catch (Exception e) {
				this.throwOperationErrorException(e);
			} finally {
				this.dcipherPool.returnObject(dcipher);
			}
		}
		return null;
	}
}
