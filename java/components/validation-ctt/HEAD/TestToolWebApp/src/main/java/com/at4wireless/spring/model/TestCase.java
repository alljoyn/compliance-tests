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

@Entity
@Table(name="testcases")
public class TestCase {
	
	@Id @GeneratedValue
	@Column(name="id_test")
	private int idTC;
	
	@Column(name="name")
	private String name;
	
	@Column(name="type")
	private String type;
	
	@Column(name="applicability")
	private String applicability;
	
	@Column(name="service_group")
	private int serviceGroup;
	
	@Column(name="last_id_project")
	private String lastIDproject;
	
	@Column(name="last_execution")
	private String lastExecution;
	
	@Column(name="last_result")
	private String lastResult;
	
	@Column(name="description")
	private String description;
	
	public int getIdTC() {
		return idTC;
	}
	public void setIdTC(int idTC) {
		this.idTC = idTC;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getApplicability() {
		return applicability;
	}
	public void setApplicability(String applicability) {
		this.applicability = applicability;
	}
	public int getServiceGroup() {
		return serviceGroup;
	}
	public void setServiceGroup(int serviceGroup) {
		this.serviceGroup = serviceGroup;
	}
	public String getLastIDproject() {
		return lastIDproject;
	}
	public void setLastIDproject(String lastIDproject) {
		this.lastIDproject = lastIDproject;
	}
	public String getLastExecution() {
		return lastExecution;
	}
	public void setLastExecution(String lastExecution) {
		this.lastExecution = lastExecution;
	}
	public String getLastResult() {
		return lastResult;
	}
	public void setLastResult(String lastResult) {
		this.lastResult = lastResult;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
