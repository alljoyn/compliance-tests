package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Parameter;

@Repository
public class ParameterDAOImpl implements ParameterDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public List<Parameter> list() {
		@SuppressWarnings("unchecked")
		List<Parameter> listParameter = (List<Parameter>) sessionFactory.getCurrentSession()
				.createCriteria(Parameter.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listParameter;
	}

}
