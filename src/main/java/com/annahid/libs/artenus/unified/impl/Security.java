/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.annahid.libs.artenus.unified.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.annahid.libs.artenus.security.AESObfuscator;
import com.annahid.libs.artenus.security.Base64;
import com.annahid.libs.artenus.security.Base64DecoderException;
import com.annahid.libs.artenus.security.Obfuscator;
import com.annahid.libs.artenus.security.ValidationException;
import com.annahid.libs.artenus.unified.UnifiedServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the
 * application on the device. For the sake of simplicity and clarity of this
 * example, this code is included here and is executed on the device. If you
 * must verify the purchases on the phone, you should obfuscate this code to
 * make it harder for an attacker to replace the code with stubs that treat all
 * purchases as verified.
 */
final class Security {
	private static final String TAG = "IABUtil/Security";

	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	/**
	 * Verifies that the data was signed with the given signature, and returns
	 * the verified purchase. The data is in JSON format and signed
	 * with a private key. The data also contains the
	 * and product ID of the purchase.
	 *
	 * @param base64PublicKey the base64-encoded public key to use for verifying.
	 * @param signedData      the signed JSON string (signed, not encrypted)
	 * @param signature       the signature for the data, signed with the private key
	 */
	public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
		if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey) ||
				TextUtils.isEmpty(signature)) {
			Log.e(TAG, "Purchase verification failed: missing data.");
			return false;
		}

		PublicKey key = Security.generatePublicKey(base64PublicKey);
		return Security.verify(key, signedData, signature);
	}

	/**
	 * Generates a PublicKey instance from a string containing the
	 * Base64-encoded public key.
	 *
	 * @param encodedPublicKey Base64-encoded public key
	 * @throws IllegalArgumentException if encodedPublicKey is invalid
	 */
	public static PublicKey generatePublicKey(String encodedPublicKey) {
		try {
			byte[] decodedKey = Base64.decode(encodedPublicKey);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
			return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			Log.e(TAG, "Invalid key specification.");
			throw new IllegalArgumentException(e);
		} catch (Base64DecoderException e) {
			Log.e(TAG, "Base64 decoding failed.");
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Verifies that the signature from the server matches the computed
	 * signature on the data.  Returns true if the data is correctly signed.
	 *
	 * @param publicKey  public key associated with the developer account
	 * @param signedData signed data from server
	 * @param signature  server signature
	 * @return true if the data and signature match
	 */
	public static boolean verify(PublicKey publicKey, String signedData, String signature) {
		Signature sig;
		try {
			sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(signedData.getBytes());
			if (!sig.verify(Base64.decode(signature))) {
				Log.e(TAG, "Signature verification failed.");
				return false;
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "NoSuchAlgorithmException.");
		} catch (InvalidKeyException e) {
			Log.e(TAG, "Invalid key specification.");
		} catch (SignatureException e) {
			Log.e(TAG, "Signature exception.");
		} catch (Base64DecoderException e) {
			Log.e(TAG, "Base64 decoding failed.");
		}
		return false;
	}

	public static String getLicenseKey(Context context, int store) throws java.io.IOException, ValidationException {
		final Obfuscator ob = new AESObfuscator(new byte[]{-47, 87, -95, -45, -103, -17, -11, 32, -64, 101, -36, -113, 64, 33, -128, -85, -57, 75, -64, 51}, context.getPackageName(), "IABK");
		final InputStreamReader reader = new InputStreamReader(context.getAssets().open("artenus.abk"));
		final BufferedReader br = new BufferedReader(reader);

		int currentStore = store;
		String result = null;

		while (true) {
			final String ln = ob.unobfuscate(br.readLine(), "buildScript");

			if (ln == null)
				break;

			if (ln.startsWith("set_store")) {
				if (ln.endsWith("(google)"))
					currentStore = UnifiedServices.STORE_GOOGLE;
				else if (ln.endsWith("(amazon)"))
					currentStore = UnifiedServices.STORE_AMAZON;
				else if (ln.endsWith("(bazaar)"))
					currentStore = UnifiedServices.STORE_BAZAAR;
				else if (ln.endsWith("(cando)"))
					currentStore = UnifiedServices.STORE_CANDO;
				else currentStore = UnifiedServices.STORE_SAMSUNG;
			} else {
				result = ln;

				if (currentStore == store)
					break;
			}
		}

		br.close();
		return result;
	}
}
