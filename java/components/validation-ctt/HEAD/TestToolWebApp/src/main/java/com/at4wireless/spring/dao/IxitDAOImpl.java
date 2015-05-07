package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Ixit;

@Repository
public class IxitDAOImpl implements IxitDAO {
	@Autowired
	private SessionFactory sessionFactory;

	/*public IxitDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}*/

	@Override
	//@Transactional
	public List<Ixit> list() {
		@SuppressWarnings("unchecked")
		List<Ixit> listIxit = (List<Ixit>) sessionFactory.getCurrentSession()
				.createCriteria(Ixit.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listIxit;
	}
	
	@Override
	@Transactional
	public List<Ixit> getService(int idService) {
		@SuppressWarnings("unchecked")
		List<Ixit> listIxit = (List<Ixit>) sessionFactory.getCurrentSession()
				.createCriteria(Ixit.class)
					.add(Restrictions.like("serviceGroup", idService))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listIxit;
	}

}
