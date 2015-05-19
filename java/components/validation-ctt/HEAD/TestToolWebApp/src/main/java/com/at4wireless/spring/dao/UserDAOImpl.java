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

package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void addUser(User user) {
		sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO users (username, password) VALUES ('"
				+user.getUser()+"', '"+user.getPassword()+"')").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO user_roles (username, ROLE) VALUES ('"+user.getUser()+"', '"+user.getRole()+"')").executeUpdate();
	}

	@Override
	public User getUser(String name) {
		@SuppressWarnings("unchecked")
		List<User> listUser = (List<User>) sessionFactory.getCurrentSession()
				.createCriteria(User.class)
					.add(Restrictions.like("user", name))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		if(listUser.isEmpty()) {
			return null;
		} else {
			return listUser.get(0);
		}
	}

	@Override
	public void update(String name, String role) {
		sessionFactory.getCurrentSession().createSQLQuery("update user_roles set ROLE = '"
				+role+"' where username = '"+name+"'").executeUpdate();
	}

	@Override
	public void setKey(String name, String key) {
		sessionFactory.getCurrentSession().createSQLQuery("update users set password = '"
				+key+"' where username = '"+name+"'").executeUpdate();
	}
}