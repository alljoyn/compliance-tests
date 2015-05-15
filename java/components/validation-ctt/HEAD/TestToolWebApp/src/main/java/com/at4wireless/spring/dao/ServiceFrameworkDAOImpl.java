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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.ServiceFramework;

@Repository
public class ServiceFrameworkDAOImpl implements ServiceFrameworkDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<ServiceFramework> list() {
		@SuppressWarnings("unchecked")
		List<ServiceFramework> listService = (List<ServiceFramework>) sessionFactory.getCurrentSession()
				.createCriteria(ServiceFramework.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listService;
	}
	
	@Override
	public List<BigInteger> getServices(int idProject) {
		@SuppressWarnings("unchecked")
		List<BigInteger> intList = (List<BigInteger>) sessionFactory.getCurrentSession()
				.createSQLQuery("select (id_service) from project_services where id_project="+idProject+";").list();
		
		return intList;
	}

	@Override
	public List<String> getServicesByName(int idProject) {
		@SuppressWarnings("unchecked")
		List<String> stringList = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select (name) from services where id_service in"
						+"(select id_service from project_services where id_project="+idProject+");").list();
		return stringList;
	}
}
