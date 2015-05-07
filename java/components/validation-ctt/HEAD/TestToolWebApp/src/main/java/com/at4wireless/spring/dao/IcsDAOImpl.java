package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Ics;

@Repository
public class IcsDAOImpl implements IcsDAO {
	@Autowired
	private SessionFactory sessionFactory;

	/*public IcsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}*/

	@Override
	//@Transactional
	public List<Ics> list() {
		@SuppressWarnings("unchecked")
		List<Ics> listIcs = (List<Ics>) sessionFactory.getCurrentSession()
				.createCriteria(Ics.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listIcs;
	}

	@Override
	@Transactional
	public List<Ics> getService(int idService) {
		@SuppressWarnings("unchecked")
		List<Ics> listIcs = (List<Ics>) sessionFactory.getCurrentSession()
				.createCriteria(Ics.class)
					.add(Restrictions.like("serviceGroup", idService))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listIcs;
	}
}
