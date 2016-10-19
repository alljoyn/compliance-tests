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

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@NamedQueries({
	@NamedQuery(
		name = "select_all_ixit",
		query = "from Ixit"
		),
	@NamedQuery(
		name = "select_ixit_by_service_group",
		query = "from Ixit where serviceGroup = :serviceGroup"
		),
	@NamedQuery(
		name = "select_ixit_by_name",
		query = "from Ixit where name = :name"
		)
})
@Entity
@Table(name = "ixit")
public class Ixit
{	
	@Id @GeneratedValue
	@Column(name = "id_ixit", nullable = false)
	private int idIxit;
	public int getIdIxit() { return idIxit; }
	public void setIdIxit(int idIxit) { this.idIxit = idIxit; }
	
	@Column(name = "name", nullable = false)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column(name = "value", nullable = false)
	private String value;
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
	
	@Column(name = "service_group", nullable = false)
	private int serviceGroup;
	public int getServiceGroup() { return serviceGroup; }
	public void setServiceGroup(int serviceGroup) { this.serviceGroup = serviceGroup; }
	
	@Column(name = "description")
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}