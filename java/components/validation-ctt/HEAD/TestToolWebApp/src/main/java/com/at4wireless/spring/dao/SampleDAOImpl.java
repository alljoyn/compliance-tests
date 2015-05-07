package com.at4wireless.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.at4wireless.spring.model.Sample;

@Repository
public class SampleDAOImpl implements SampleDAO {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Sample> list(int dut) {
		@SuppressWarnings("unchecked")
		List<Sample> listSample = (List<Sample>) sessionFactory.getCurrentSession()
				.createCriteria(Sample.class)
					.add(Restrictions.like("associatedDut", dut))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listSample;
	}

	@Override
	public void addSample(Sample sample) {
		sessionFactory.getCurrentSession().save(sample);		
	}
	
	@Override
	public void delSample(int idSample) {
		sessionFactory.getCurrentSession().createQuery("delete from Sample s where s.idSample ='"+idSample+"'").executeUpdate();
	}
	
	@Override
	public Sample getSample(int idSample) {
		@SuppressWarnings("unchecked")
		List<Sample> listSample = (List<Sample>) sessionFactory.getCurrentSession()
				.createCriteria(Sample.class)
					.add(Restrictions.like("idSample", idSample))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listSample.get(0);
	}
	
	@Override
	public void saveChanges(Sample sample) {
		sessionFactory.getCurrentSession().createQuery("update Sample set deviceId = '"+sample.getDeviceId()
				+"', appId = '"+sample.getAppId()+"', swVer = '"+sample.getSwVer()
				+"', hwVer = '"+sample.getHwVer()+"' where idSample = '"+sample.getIdSample()+"'").executeUpdate();
	}
}
