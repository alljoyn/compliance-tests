package com.at4wireless.spring.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

@Repository
public class TcclDAOImpl implements TcclDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	/*public TcclDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}*/

	@Override
	//@Transactional
	public List<Tccl> list() {
		@SuppressWarnings("unchecked")
		List<Tccl> listTccl = (List<Tccl>) sessionFactory.getCurrentSession()
				.createCriteria(Tccl.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listTccl;
	}

	@Override
	public int getNumber(String certRel) {
		@SuppressWarnings("unchecked")
		List<Tccl> listTccl = (List<Tccl>) sessionFactory.getCurrentSession()
				.createSQLQuery("select * from tccl where name like '%"+certRel+"%';").list();
		
		if (listTccl.isEmpty()) {
			return 0;
		} else {
			return listTccl.size();
		}
	}

	@Override
	public int addTccl(Tccl tccl) {
		sessionFactory.getCurrentSession().save(tccl);
		return tccl.getIdTccl();
	}

	@Override
	public void saveList(String values) {
		sessionFactory.getCurrentSession().createSQLQuery("insert into tccl_testcase(id_tccl,type,enable,id_test)"
				+" values "+values+";").executeUpdate();
	}

	@Override
	public void deleteList(int idTccl) {
		sessionFactory.getCurrentSession().createSQLQuery("delete from tccl_testcase where id_tccl="
				+idTccl+";").executeUpdate();
		
	}

	@Override
	public void deleteTccl(int idTccl) {
		sessionFactory.getCurrentSession()
				.createQuery("delete from Tccl t where t.idTccl ='"+idTccl+"'").executeUpdate();
		
	}

	@Override
	public List<TestCaseTccl> getList(int idTccl) {
		@SuppressWarnings("unchecked")
		List<TestCaseTccl> tcclList = (List<TestCaseTccl>) sessionFactory.getCurrentSession()
				.createSQLQuery("SELECT b.id_test, b.name, b.description, a.type, a.enable FROM tccl_testcase a"
						+" INNER JOIN testcases b ON a.id_test = b.id_test"
						+" where a.id_tccl='"+idTccl+"';").list();
		//JTF:TO FIX
		return tcclList;
	}
	
	@Override
	public void updateTccl(int idTccl, java.sql.Timestamp date) {
		sessionFactory.getCurrentSession().createQuery("update Tccl set modifiedDate = '"+date
				+"' where idTccl = '"+idTccl+"'").executeUpdate();
	}

	@Override
	public List<Tccl> listByCR(int idCertRel) {
		@SuppressWarnings("unchecked")
		List<Tccl> listTccl = (List<Tccl>) sessionFactory.getCurrentSession()
				.createCriteria(Tccl.class)
					.add(Restrictions.like("idCertrel", idCertRel))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		if (listTccl.isEmpty()) {
			return null;
		} else {
			return listTccl;
		}
	}

	@Override
	public List<Integer> getIds(int idTccl) {
		@SuppressWarnings("unchecked")
		List<BigInteger> idList = (List<BigInteger>) sessionFactory.getCurrentSession()
				.createSQLQuery("select id_test from tccl_testcase where id_tccl="
						+idTccl+" and enable=true").list();
		
		List<Integer> intList = new ArrayList<Integer>();
		for (BigInteger bi : idList) {
			intList.add(bi.intValue());
		}
		return intList;
	}

	@Override
	public List<Integer> getIdsDisabled(int idTccl) {
		@SuppressWarnings("unchecked")
		List<BigInteger> idList = (List<BigInteger>) sessionFactory.getCurrentSession()
				.createSQLQuery("select id_test from tccl_testcase where id_tccl="
						+idTccl+" and enable=false").list();
		
		List<Integer> intList = new ArrayList<Integer>();
		for (BigInteger bi : idList) {
			intList.add(bi.intValue());
		}
		return intList;
	}

	@Override
	public String getTcclName(int idTccl) {
		@SuppressWarnings("unchecked")
		List<String> listTccl = (List<String>) sessionFactory.getCurrentSession()
				.createSQLQuery("select name from tccl where id_tccl='"+idTccl+"';").list();
		
		if (listTccl.isEmpty()) {
			return null;
		} else {
			return listTccl.get(0);
		}
	}
}
