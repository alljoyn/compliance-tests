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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at4wireless.spring.model.Ixit;

@Repository
public class IxitDAOImpl implements IxitDAO
{
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Ixit> list()
	{	
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_all_ixit", Ixit.class);
		
		return query.getResultList();
	}
	
	@Override
	public Ixit get(String name)
	{
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ixit_by_name", Ixit.class)
				.setParameter("name", name);
		
		Ixit foundIxit = null;
		try
		{
			foundIxit = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			
		}
		
		return foundIxit;
	}
	
	@Override
	@Transactional
	public List<Ixit> getByService(int serviceID)
	{	
		TypedQuery<Ixit> query = sessionFactory.getCurrentSession()
				.createNamedQuery("select_ixit_by_service_group", Ixit.class)
				.setParameter("serviceGroup", serviceID);
		
		return query.getResultList();
	}

	@Override
	public void add(Ixit ixit)
	{
		sessionFactory.getCurrentSession().save(ixit);
	}
}