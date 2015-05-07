package com.at4wireless.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.dao.UserDAO;
import com.at4wireless.spring.model.Sample;
import com.at4wireless.spring.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDao;
	
	@Override
	@Transactional
	public boolean addUser(User u) {
		
		// if dut's assigned user and context's user are equal
		if(userDao.getUser(u.getUser())!=null) {
			return false;
		} else {
			userDao.addUser(u);
			return true;
		}
	}

}
