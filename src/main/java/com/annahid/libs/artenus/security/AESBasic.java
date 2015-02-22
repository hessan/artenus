/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.annahid.libs.artenus.security;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class provides AES encryption functionality.
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class AESBasic {
	private static final String UTF8 = "UTF-8";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final byte[] IV = {'1', '2', '4', '6', 'F', 'g', 'h', 'z', 'L', 'K', '6', 'd', 'b', 'N', '7', '3'};

	private Cipher mEncryptor;
	private Cipher mDecryptor;

	/**
	 * Creates a new instance of {@code AESBasic} with the given password.
	 *
	 * @param password The password for this instance.
	 */
	public AESBasic(String password) {
		byte[] secretKey = new byte[16];

		for (int i = 0; i < Math.min(16, password.length()); i++)
			secretKey[i] = (byte) password.charAt(i);

		SecretKey secret = new SecretKeySpec(secretKey, "AES");

		try {
			mEncryptor = Cipher.getInstance(CIPHER_ALGORITHM);
			mEncryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
			mDecryptor = Cipher.getInstance(CIPHER_ALGORITHM);
			mDecryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
		} catch (GeneralSecurityException e) {
			// Do nothing
		}
	}

	/**
	 * Encrypts a given string.
	 *
	 * @param original The string to be encrypted.
	 * @return The string representation of the encrypted value.
	 */
	public String encrypt(String original) {
		if (original == null)
			return null;

		try {
			return Base64.encode(mEncryptor.doFinal(original.getBytes(UTF8)));
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			throw new RuntimeException("Invalid environment", e);
		}
	}

	/**
	 * Decrypts an encrypted string.
	 *
	 * @param encrypted The encrypted string.
	 * @throws ValidationException
	 * @return The original string.
	 */
	public String decrypt(String encrypted) throws ValidationException {
		if (encrypted == null)
			return null;

		try {
			return new String(mDecryptor.doFinal(Base64.decode(encrypted)), UTF8);
		} catch (Base64DecoderException | IllegalBlockSizeException | BadPaddingException e) {
			throw new ValidationException(e.getMessage() + ":" + encrypted);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Invalid environment", e);
		}
	}
}
