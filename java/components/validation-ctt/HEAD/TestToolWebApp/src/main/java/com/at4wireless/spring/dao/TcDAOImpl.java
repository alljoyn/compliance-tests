package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.TestCase;

@Repository
public class TcDAOImpl implements TcDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<TestCase> list() {
		@SuppressWarnings("unchecked")
		List<TestCase> listTC = (List<TestCase>) sessionFactory.getCurrentSession()
				.createCriteria(TestCase.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listTC;
	}
	
	@Override
	public List<TestCase> getService(String type, int idService) {
		@SuppressWarnings("unchecked")
		List<TestCase> listTc = (List<TestCase>) sessionFactory.getCurrentSession()
				.createCriteria(TestCase.class)
					.add(Restrictions.like("serviceGroup", idService))
					.add(Restrictions.like("type",type))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listTc;
	}
	
	@Override
	public List<TestCase> list(int idCertRel) {
		@SuppressWarnings("unchecked")
		List<TestCase> tcList = (List<TestCase>) sessionFactory.getCurrentSession()
				.createSQLQuery("select tc.* from testcases tc where tc.id_test in"
						+"(select tcr.id_test from testcases_certrel tcr where tcr.id_certrel="+idCertRel+");").list();

		return tcList;
	}
	
	@Override
	public List<TestCase> getServiceWithRestriction(String type, int idService, List<Integer> intList) {
		@SuppressWarnings("unchecked")
		List<TestCase> tcList = (List<TestCase>) sessionFactory.getCurrentSession()
				.createCriteria(TestCase.class)
					.add(Restrictions.like("serviceGroup", idService))
					.add(Restrictions.like("type",type))
					.add(Restrictions.in("idTC", intList))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		return tcList;
	}
}
