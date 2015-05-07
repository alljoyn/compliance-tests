package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.ServiceFramework;

@Repository
public class ServiceDAOImpl implements ServiceDAO {

	@Autowired
	private SessionFactory sessionFactory;

	/*public ServiceDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}*/

	@Override
	//@Transactional
	public List<ServiceFramework> list() {
		@SuppressWarnings("unchecked")
		List<ServiceFramework> listService = (List<ServiceFramework>) sessionFactory.getCurrentSession()
				.createCriteria(ServiceFramework.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listService;
	}
}
