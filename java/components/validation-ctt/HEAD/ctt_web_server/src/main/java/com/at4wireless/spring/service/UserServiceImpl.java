/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/

package com.at4wireless.spring.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.security.FileEncryption;
import com.at4wireless.spring.dao.UserDAO;
import com.at4wireless.spring.model.User;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserDAO userDao;

	@Override
	@Transactional
	public boolean addUser(User newUser)
	{
		// if dut's assigned user and context's user are equal
		if (userDao.get(newUser.getUser()) != null)
		{
			return false;
		}
		else
		{
			userDao.add(newUser);
			return true;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean exists(String name)
	{
		return userDao.get(name) != null;
	}

	@Override
	@Transactional
	public void update(String user, String role)
	{
		userDao.update(user, role);
	}

	@Override
	@Transactional
	public int setKey(String name)
	{
		int pass = -1;
		SecureRandom random = new SecureRandom();
		int high = 1000000;
		int low = 100000;
		Integer rnd = random.nextInt(high - low) + low;

		byte[] messageDigest;
		try
		{
			messageDigest = MessageDigest.getInstance("SHA-256").digest(rnd.toString().getBytes());
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
			{
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
				{
					hexString.append('0');
				}
				hexString.append(hex);
			}

			userDao.setKey(name, hexString.toString());
			pass = rnd;
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pass;
	}

	@Override
	@Transactional
	public String keyExchange(String user, String localKey)
	{
		FileEncryption fE;
		String encryptedKey = "";
		try
		{
			fE = new FileEncryption();
			KeyFactory kF = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(localKey));
			PublicKey pK = kF.generatePublic(publicKeySpec);
			fE.setRsaPublicKey(pK);
			byte[] encodedKey = DatatypeConverter.parseBase64Binary(userDao.getAesCipherKey(user));
			SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
			fE.setAesSecretKey(originalKey);
			encryptedKey = DatatypeConverter.printBase64Binary(fE.encryptAESwithRSA());
		}
		catch (GeneralSecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedKey;
	}

	@Override
	@Transactional
	public SecretKey getAesSecretKey(String user)
	{
		byte[] encodedKey = DatatypeConverter.parseBase64Binary(userDao.getAesCipherKey(user));
		return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	}

	@Override
	@Transactional
	public boolean hasCipherKey(String user)
	{
		return (userDao.getAesCipherKey(user) != null);
	}

	@Override
	@Transactional
	public void setCipherKey(String user, String aesSecretKey)
	{
		userDao.setAesCipherKey(user, aesSecretKey);
	}
	
	@Override
	@Transactional(readOnly = true)
	public String getUserRole(String username)
	{
		return userDao.getUserRole(username);
	}
}