package com.at4wireless.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.UserDAO;
import com.at4wireless.spring.model.User;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestUserDAO
{
	private final static String usernameTest = "testUser";
	private final static String roleTest = "ROLE_USER";
	
	@Autowired
	private UserDAO userDAO;
	
	@Test
	@Transactional
	public void testGetUserRole()
	{
		userDAO.add(new User(usernameTest, "", "", roleTest, ""));
		assertEquals("returned ROLE is not equal to the one set", userDAO.getUserRole(usernameTest), roleTest);
	}
}