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

package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="tccl")
public class Tccl {
	
	@Id @GeneratedValue
	@Column(name="id_tccl")
	private int idTccl;
	
	@Column(name="name")
	private String name;
	
	@Column(name="created_date")
	private java.sql.Timestamp createdDate;
	
	@Column(name="modified_date")
	private java.sql.Timestamp modifiedDate;
	
	@Column(name="id_certrel")
	private int idCertrel;
	
	@Transient
	private String nameCertrel;
	
	@Column(name="num_tc")
	private int numTc;
	
	public int getIdTccl() {
		return idTccl;
	}
	public void setIdTccl(int idTccl) {
		this.idTccl = idTccl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public int getIdCertrel() {
		return idCertrel;
	}
	public void setIdCertrel(int idCertrel) {
		this.idCertrel = idCertrel;
	}
	public int getNumTc() {
		return numTc;
	}
	public void setNumTc(int numTc) {
		this.numTc = numTc;
	}
	public String getNameCertrel() {
		return nameCertrel;
	}
	public void setNameCertrel(String nameCertrel) {
		this.nameCertrel = nameCertrel;
	}
	
	
}
