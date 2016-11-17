package com.at4wireless.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.User;
import com.at4wireless.spring.service.UserService;

@ContextConfiguration(locations = "classpath:spring-database.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestUserService
{
	private final static String usernameTest = "testUser";
	private final static String roleTest = "ROLE_USER";
	
	@Autowired
	private UserService userService;
	
	@Test
	@Transactional
	public void testGetUserRole()
	{
		userService.addUser(new User(usernameTest, "", "", roleTest, ""));
		assertEquals("returned ROLE is not equal to the one set", userService.getUserRole(usernameTest), roleTest);
	}
}