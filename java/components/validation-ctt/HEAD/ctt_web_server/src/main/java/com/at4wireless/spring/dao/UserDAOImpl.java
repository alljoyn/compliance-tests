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

package com.at4wireless.spring.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.User;

@Repository
public class UserDAOImpl implements UserDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void add(User newUser)
	{
		sessionFactory.getCurrentSession().save(newUser);
		sessionFactory.getCurrentSession().flush();
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 * 		  flush() method is needed because second query needs the first one to be inserted. Look for a better way to achieve this.
		 * 
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("INSERT INTO user_roles (username, ROLE) VALUES (:username, :role)")
				.setParameter("username", newUser.getUser())
				.setParameter("role", newUser.getRole())
				.executeUpdate();
	}

	@Override
	public User get(String username)
	{
		TypedQuery<User> query = sessionFactory.getCurrentSession()
				.createQuery("from User where user = :username", User.class)
				.setParameter("username", username);
		
		User foundUser = null;
		try
		{
			foundUser = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundUser;
	}
	
	@Override
	public String getUserRole(String username)
	{
		@SuppressWarnings("unchecked")
		NativeQuery<String> query = sessionFactory.getCurrentSession()
				.createNativeQuery("SELECT ur.ROLE FROM user_roles ur WHERE ur.username = :user")
				.setParameter("user", username);
		
		String foundRole = null;
		try
		{
			foundRole = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundRole;
	}

	@Override
	public void update(String username, String role)
	{
		/*
		 * TODO : Change NativeQuery to TypedQuery
		 */
		sessionFactory.getCurrentSession()
				.createNativeQuery("UPDATE user_roles SET ROLE=:role WHERE username=:username")
				.setParameter("role", role)
				.setParameter("username", username)
				.executeUpdate();
	}

	@Override
	public void setKey(String username, String laApiKey)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update User set password = :password where user = :user")
				.setParameter("password", laApiKey)
				.setParameter("user", username)
				.executeUpdate();
	}

	@Override
	public String getAesCipherKey(String username)
	{
		@SuppressWarnings("unchecked")
		TypedQuery<String> query = sessionFactory.getCurrentSession()
				.createQuery("select u.aesSecretKey from User u where u.user = :user")
				.setParameter("user", username);
		
		String foundKey = null;
		try
		{
			foundKey = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundKey;
	}

	@Override
	public void setAesCipherKey(String username, String aesCipherKey)
	{
		sessionFactory.getCurrentSession()
				.createQuery("update User set aesSecretKey = :aesSecretKey where user = :user")
				.setParameter("aesSecretKey", aesCipherKey)
				.setParameter("user", username)
				.executeUpdate();
	}
}