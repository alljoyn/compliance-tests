package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.Project;
import com.at4wireless.spring.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	/*public UserDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}*/
	
	@Override
	//@Transactional
	public void addUser(User user) {
		sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO users (username, password) VALUES ('"+user.getUser()+"', '"+user.getPassword()+"')").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("INSERT INTO user_roles (username, ROLE) VALUES ('"+user.getUser()+"', '"+user.getRole()+"')").executeUpdate();
	}

	@Override
	public User getUser(String name) {
		/*@SuppressWarnings("unchecked")
		List<User> listUser = (List<User>) sessionFactory.getCurrentSession()
				.createSQLQuery("select * from users where username='"+name+"'").list();*/
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
}