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

import com.at4wireless.spring.model.GoldenUnit;

@Repository
public class GoldenUnitDAOImpl implements GoldenUnitDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<GoldenUnit> list(String user) {
		@SuppressWarnings("unchecked")
		List<GoldenUnit> listGolden = (List<GoldenUnit>) sessionFactory.getCurrentSession()
				.createCriteria(GoldenUnit.class)
					.add(Restrictions.like("user", user))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listGolden;
	}
	
	@Override
	public GoldenUnit getGu(String user, int idGolden) {
		@SuppressWarnings("unchecked")
		List<GoldenUnit> listGolden = (List<GoldenUnit>) sessionFactory.getCurrentSession()
				.createCriteria(GoldenUnit.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("idGolden", idGolden))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		if(listGolden.isEmpty()) {
			return null;
		} else {
			return listGolden.get(0);
		}
	}

	@Override
	public void addGu(GoldenUnit gu) {
		sessionFactory.getCurrentSession().save(gu);		
	}
	
	@Override
	public void delGu(int idGolden) {
		
		sessionFactory.getCurrentSession().createSQLQuery("delete from project_golden where id_golden="+idGolden).executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from golden where id_golden ="+idGolden).executeUpdate();
	}
	
	@Override
	public void saveChanges(GoldenUnit gu) {
		sessionFactory.getCurrentSession().createQuery("update GoldenUnit set name = '"+gu.getName()
				+"', modifiedDate = '"+gu.getModifiedDate()+"', category = '"+gu.getCategory()
				+"', manufacturer = '"+gu.getManufacturer()+"', model = '"+gu.getModel()
				+"', swVer = '"+gu.getSwVer()+"', hwVer='"+gu.getHwVer()
				+"', description = '"+gu.getDescription()
				+"' where idGolden = '"+gu.getIdGolden()+"'").executeUpdate();
	}

	@Override
	public GoldenUnit getGuByName(String user, String name) {
		@SuppressWarnings("unchecked")
		List<GoldenUnit> listGolden = (List<GoldenUnit>) sessionFactory.getCurrentSession()
				.createCriteria(GoldenUnit.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("name", name))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		if(listGolden.isEmpty()) {
			return null;	
		} else {
			return listGolden.get(0);
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<GoldenUnit> getGuList(int idProject) {
		
		List<BigInteger> listIds = (List<BigInteger>) sessionFactory.getCurrentSession()
				.createSQLQuery("select id_golden from project_golden where id_project="+idProject).list();
		
		List<Integer> cast = new ArrayList<Integer>();
		for (BigInteger bi : listIds) {
			cast.add(bi.intValue());
		}
		
		List<GoldenUnit> guList = new ArrayList<GoldenUnit>();
		if (!cast.isEmpty())
		{
			guList = (List<GoldenUnit>) sessionFactory.getCurrentSession()
					.createCriteria(GoldenUnit.class)
						.add(Restrictions.in("idGolden", cast))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		}
		
		return guList;
	}
}
