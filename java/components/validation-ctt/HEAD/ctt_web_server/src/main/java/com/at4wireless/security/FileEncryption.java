/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class FileEncryption {

	KeyPairGenerator rsaKeyPairGenerator = null;
	KeyGenerator aesKeyGenerator = null;
	PublicKey rsaPublicKey = null;
	PrivateKey rsaPrivateKey = null;
	SecretKey aesSecretKey = null;
	Cipher rsaCipher = null;
	Cipher aesCipher = null;
	
	public FileEncryption() throws GeneralSecurityException {
		rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		aesKeyGenerator = KeyGenerator.getInstance("AES");
		rsaCipher = Cipher.getInstance("RSA");
		aesCipher = Cipher.getInstance("AES");
	}
	
	public void makeKeys() throws NoSuchAlgorithmException {
		KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();
		rsaPublicKey = rsaKeyPair.getPublic();
		rsaPrivateKey = rsaKeyPair.getPrivate();
		aesSecretKey = aesKeyGenerator.generateKey();
	}

	public String encrypt(byte[] in) throws IOException, InvalidKeyException {
		
		aesCipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);

		String out = "";
		try {
			byte[] encValue = aesCipher.doFinal(in);
			out = DatatypeConverter.printBase64Binary(encValue);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out;
	}
	
	public String decrypt(String in) throws IOException, InvalidKeyException {
		
		aesCipher.init(Cipher.DECRYPT_MODE, aesSecretKey);

		byte[] decodedValue = DatatypeConverter.parseBase64Binary(in);
		byte[] decryptedVal = null;
		try {
			decryptedVal = aesCipher.doFinal(decodedValue);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new String(decryptedVal, StandardCharsets.UTF_8);
	}
	
	public byte[] encryptAESwithRSA() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
		
		return rsaCipher.doFinal(aesSecretKey.getEncoded());
	}
	
	public PublicKey getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(PublicKey rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public PrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(PrivateKey rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}

	public SecretKey getAesSecretKey() {
		return aesSecretKey;
	}

	public void setAesSecretKey(SecretKey aesSecretKey) {
		this.aesSecretKey = aesSecretKey;
	}
}
