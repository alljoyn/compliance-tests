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
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Ics;

@Repository
public class IcsDAOImpl implements IcsDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
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

	@Override
	public void add(Ics ics)
	{
		sessionFactory.getCurrentSession().saveOrUpdate(ics);
	}

	@Override
	public void update(Ics ics)
	{
		sessionFactory.getCurrentSession().createQuery("update Ics set name = '"+ics.getName()
		+"', serviceGroup = '"+ics.getServiceGroup()+"', scrExpression = '"+ics.getScrExpression()
		+"', description = '"+ics.getDescription()+"' where id = '"+ics.getId()+"'").executeUpdate();
	}
}
