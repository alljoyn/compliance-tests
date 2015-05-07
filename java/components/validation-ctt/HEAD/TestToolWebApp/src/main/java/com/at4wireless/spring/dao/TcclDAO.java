package com.at4wireless.spring.dao;

import java.util.List;

import com.at4wireless.spring.model.Tccl;
import com.at4wireless.spring.model.TestCaseTccl;

public interface TcclDAO {
	public List<Tccl> list();
	public int getNumber(String certRel);
	public int addTccl(Tccl tccl);
	public void saveList(String values);
	public void deleteList(int idTccl);
	public void deleteTccl(int idTccl);
	public List<TestCaseTccl> getList(int idTccl);
	public void updateTccl(int idTccl, java.sql.Timestamp date);
	public List<Tccl> listByCR(int idCertRel);
	public List<Integer> getIds(int idTccl);
	public List<Integer> getIdsDisabled(int idTccl);
	public String getTcclName(int idTccl);
}
