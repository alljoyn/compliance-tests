package com.at4wireless.spring.dao;

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
		sessionFactory.getCurrentSession().createQuery("delete from GoldenUnit g where g.idGolden ='"+idGolden+"'").executeUpdate();
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

	@Override
	public List<String> getGuList(int idProject) {
		@SuppressWarnings("unchecked")
		List<String> stringList = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select (name) from golden where id_golden in"
						+"(select id_golden from project_golden where id_project="+idProject+");").list();
		return stringList;
	}
}
