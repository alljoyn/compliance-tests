/*******************************************************************************
 *  Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright 2016 Open Connectivity Foundation and Contributors to
 *     AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.localagent.model.common;

import java.io.IOException;
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
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileEncryption
{
	private static final Logger logger = LogManager.getLogger(FileEncryption.class.getName());
	
	KeyPairGenerator rsaKeyPairGenerator = null;
	KeyGenerator aesKeyGenerator = null;
	PublicKey rsaPublicKey = null;
	PrivateKey rsaPrivateKey = null;
	SecretKey aesSecretKey = null;
	Cipher rsaCipher = null;
	Cipher aesCipher = null;
	
	public FileEncryption() throws GeneralSecurityException
	{
		rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		aesKeyGenerator = KeyGenerator.getInstance("AES");
		rsaCipher = Cipher.getInstance("RSA");
		aesCipher = Cipher.getInstance("AES");
	}
	
	public void makeKeys() throws NoSuchAlgorithmException
	{
		KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();
		rsaPublicKey = rsaKeyPair.getPublic();
		rsaPrivateKey = rsaKeyPair.getPrivate();
	}

	public String encrypt(byte[] in) throws IOException, InvalidKeyException
	{
		aesCipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);

		String out = "";
		try
		{
			byte[] encValue = aesCipher.doFinal(in);
			out = DatatypeConverter.printBase64Binary(encValue);
		} 
		catch (IllegalBlockSizeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out;
	}
	
	public String decrypt(String in) throws IOException, InvalidKeyException
	{
		logger.debug(in);
		aesCipher.init(Cipher.DECRYPT_MODE, aesSecretKey);

		byte[] decodedValue = DatatypeConverter.parseBase64Binary(in);
		byte[] decryptedVal = null;
		try
		{
			decryptedVal = aesCipher.doFinal(decodedValue);
		}
		catch (IllegalBlockSizeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new String(decryptedVal);
	}
	
	public SecretKey decryptAESwithRSA(String encryptedKey) throws InvalidKeyException
	{
		logger.debug(encryptedKey);
		rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
		
		SecretKey key = null;
		try
		{
			key = new SecretKeySpec(rsaCipher.doFinal(DatatypeConverter.parseBase64Binary(encryptedKey)), "AES");
		}
		catch (IllegalBlockSizeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	
	public PublicKey getRsaPublicKey()
	{
		return rsaPublicKey;
	}

	public void setRsaPublicKey(PublicKey rsaPublicKey)
	{
		this.rsaPublicKey = rsaPublicKey;
	}

	public PrivateKey getRsaPrivateKey()
	{
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(PrivateKey rsaPrivateKey)
	{
		this.rsaPrivateKey = rsaPrivateKey;
	}

	public SecretKey getAesSecretKey()
	{
		return aesSecretKey;
	}

	public void setAesSecretKey(SecretKey aesSecretKey)
	{
		this.aesSecretKey = aesSecretKey;
	}
	
	public void test()
	{
		try
		{
			String encrypt = encrypt("hi".getBytes());
			String decrypt = decrypt(encrypt);
			System.out.println(decrypt);
		}
		catch (InvalidKeyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}