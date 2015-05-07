package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.CertificationRelease;

@Repository
public class CertificationReleaseDAOImpl implements CertificationReleaseDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<CertificationRelease> list() {
		@SuppressWarnings("unchecked")
		List<CertificationRelease> listCertrel = (List<CertificationRelease>) sessionFactory.getCurrentSession()
				.createCriteria(CertificationRelease.class)
					.add(Restrictions.like("enabled", true))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listCertrel;
	}

	@Override
	public String getName(int id) {
		
		@SuppressWarnings("unchecked")
		List<String> listCertrel = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select name from certrel where id_certrel='"+id+"';").list();
		
		if(listCertrel.isEmpty()) {
			return null;
		} else {
			return listCertrel.get(0);
		}
	}

	@Override
	public List<Integer> getIds(int idCertRel) {
		@SuppressWarnings("unchecked")
		List<BigInteger> idList = (List<BigInteger>) sessionFactory.getCurrentSession()
				.createSQLQuery("select id_test from testcases_certrel where id_certrel="
						+idCertRel+";").list();
		
		List<Integer> intList = new ArrayList<Integer>();
		for (BigInteger bi : idList) {
			intList.add(bi.intValue());
		}
		return intList;
	}
}
