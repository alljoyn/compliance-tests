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

	@Override
	public boolean certificationReleaseExists(String certificationRelease) {
		@SuppressWarnings("unchecked")
		List<CertificationRelease> listCertrel = (List<CertificationRelease>) sessionFactory.getCurrentSession()
				.createCriteria(CertificationRelease.class)
					.add(Restrictions.like("name", certificationRelease))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return (listCertrel.size() != 0);
	}

	@Override
	public int addCertificationRelease(CertificationRelease certificationRelease) {
		sessionFactory.getCurrentSession().save(certificationRelease);
		
		return certificationRelease.getIdCertrel();
	}

	@Override
	public List<CertificationRelease> listReleaseVersions() {
		@SuppressWarnings("unchecked")
		List<CertificationRelease> listCertrel = (List<CertificationRelease>) sessionFactory.getCurrentSession()
				.createCriteria(CertificationRelease.class)
					.add(Restrictions.like("enabled", true))
					.add(Restrictions.like("release", true))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listCertrel;
	}

	@Override
	public boolean isReleaseVersion(String certificationRelease) {
		@SuppressWarnings("unchecked")
		List<CertificationRelease> listCertrel = (List<CertificationRelease>) sessionFactory.getCurrentSession()
				.createCriteria(CertificationRelease.class)
					.add(Restrictions.like("name", certificationRelease))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listCertrel.get(0).isRelease();
	}

	@Override
	public void fromDebugToRelease(String certificationRelease) {
		sessionFactory.getCurrentSession().createQuery("update CertificationRelease set release = '"
				+1+"' where name = '"+certificationRelease+"'").executeUpdate();
	}

	@Override
	public String getCertificationReleaseDescription(String certificationRelease) {
		@SuppressWarnings("unchecked")
		List<String> listCertrel = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select description from certrel where name='"
						+certificationRelease+"';").list();
		
		if(listCertrel.isEmpty()) {
			return null;
		} else {
			return listCertrel.get(0);
		}
	}

	@Override
	public void updateDescription(String certificationRelease, String description) {
		sessionFactory.getCurrentSession().createQuery("update CertificationRelease set description = '"
				+description+"' where name = '"+certificationRelease+"'").executeUpdate();
	}
}
