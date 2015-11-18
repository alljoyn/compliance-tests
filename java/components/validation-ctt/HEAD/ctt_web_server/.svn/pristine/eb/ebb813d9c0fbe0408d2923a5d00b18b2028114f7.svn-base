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

	@Override
	public void assignTestCasesToCertificationRelease(int certificationReleaseId) {
		StringBuilder values = new StringBuilder();
		List<TestCase> listTC = list();
		
		for(int i=0; i<listTC.size(); i++) {
			String s = "("+listTC.get(i).getIdTC()+","+certificationReleaseId+")";
			values.append(s);
			if(i != (listTC.size() -1)) values.append(",");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into testcases_certrel(id_test,id_certrel)"
				+" values "+values+";").executeUpdate();
	}
	
	@Override
	public void add(TestCase testCase)
	{
		sessionFactory.getCurrentSession().save(testCase);
		
		String[] var = testCase.getSupportedCrs().split("[\\.]+");
		StringBuilder str = new StringBuilder("values ");
		for (int i=0; i<var.length; i++) {
			str.append("("+Integer.toString(testCase.getIdTC())+","+var[i]+")");
			if(i!=(var.length-1)) str.append(",");
			else str.append(";");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into testcases_certrel (id_test,id_certrel) "
				+str.toString()).executeUpdate();
	}
	
	@Override
	public void update(TestCase testCase)
	{
		sessionFactory.getCurrentSession().createQuery("update TestCase set name = '"+testCase.getName()
		+"', type = '"+testCase.getType()+"', applicability = '"+testCase.getApplicability()
		+"', serviceGroup = '"+testCase.getServiceGroup()+"', description = '"+testCase.getDescription()
		+"' where idTC = '"+testCase.getIdTC()+"'").executeUpdate();
		
		sessionFactory.getCurrentSession().createSQLQuery("delete from testcases_certrel where id_test="
				+testCase.getIdTC()+";").executeUpdate();
		
		String[] var = testCase.getSupportedCrs().split("[\\.]+");
		StringBuilder str = new StringBuilder("values ");
		for (int i=0; i<var.length; i++) {
			str.append("("+Integer.toString(testCase.getIdTC())+","+var[i]+")");
			if(i!=(var.length-1)) str.append(",");
			else str.append(";");
		}
		sessionFactory.getCurrentSession().createSQLQuery("insert into testcases_certrel (id_test,id_certrel) "
				+str.toString()).executeUpdate();
	}
}