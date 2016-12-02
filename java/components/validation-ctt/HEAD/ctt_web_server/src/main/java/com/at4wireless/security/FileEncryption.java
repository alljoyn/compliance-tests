/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

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

public class FileEncryption
{
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
		aesSecretKey = aesKeyGenerator.generateKey();
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

		return new String(decryptedVal, StandardCharsets.UTF_8);
	}
	
	public byte[] encryptAESwithRSA() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
		
		return rsaCipher.doFinal(aesSecretKey.getEncoded());
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
}