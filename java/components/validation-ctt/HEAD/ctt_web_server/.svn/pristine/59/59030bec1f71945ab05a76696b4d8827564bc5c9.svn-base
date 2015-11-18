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

import com.at4wireless.spring.model.Dut;

@Repository
public class DutDAOImpl implements DutDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Dut> list(String user) {
		@SuppressWarnings("unchecked")
		List<Dut> listDut = (List<Dut>) sessionFactory.getCurrentSession()
				.createCriteria(Dut.class)
					.add(Restrictions.like("user", user))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listDut;
	}
	
	@Override
	public Dut getDut(String user, int idDut) {
		@SuppressWarnings("unchecked")
		List<Dut> listDut = (List<Dut>) sessionFactory.getCurrentSession()
				.createCriteria(Dut.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("idDut", idDut))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		if(listDut.isEmpty()) {
			return null;
		} else {
			return listDut.get(0);
		}
	}
	
	@Override
	public Dut getDutByName(String user, String name) {
		@SuppressWarnings("unchecked")
		List<Dut> listDut = (List<Dut>) sessionFactory.getCurrentSession()
				.createCriteria(Dut.class)
					.add(Restrictions.like("user", user))
					.add(Restrictions.like("name", name))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		if(listDut.isEmpty()) {
			return null;
		} else {
			return listDut.get(0);
		}
	}

	@Override
	public void addDut(Dut dut) {
		sessionFactory.getCurrentSession().save(dut);		
	}
	
	@Override
	public void delDut(int dutId) {
		sessionFactory.getCurrentSession().createQuery("delete from Dut d where d.idDut ='"+dutId+"'").executeUpdate();
	}
	
	@Override
	public void saveChanges(Dut dut) {
		sessionFactory.getCurrentSession().createQuery("update Dut set name = '"+dut.getName()
				+"', modifiedDate = '"+dut.getModifiedDate()+"', manufacturer = '"+dut.getManufacturer()
				+"', model = '"+dut.getModel()+"', description = '"+dut.getDescription()
				+"' where idDut = '"+dut.getIdDut()+"'").executeUpdate();
	}
}
