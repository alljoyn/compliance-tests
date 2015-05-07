package com.at4wireless.spring.dao;

import com.at4wireless.spring.model.User;

public interface UserDAO {
	public void addUser(User user);
	public User getUser(String name);
}
