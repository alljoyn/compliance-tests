/*******************************************************************************
 *  * 
 *      Copyright (c) 2016 Open Connectivity Foundation and AllJoyn Open
 *      Source Project Contributors and others.
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0

 *******************************************************************************/
package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User
{
	public User() {}
	public User(String user, String password, String repPassword, String role, String aesSecretKey)
	{
		this.user = user;
		this.password = password;
		this.repPassword = repPassword;
		this.role = role;
		this.aesSecretKey = aesSecretKey;
	}
	
	@Id @Column(name = "username", nullable = false)
	@Size(min=5, max=13)
	private String user;
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
	
	@Column(name = "password", nullable = false)
	private String password;
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	@Transient
	private String repPassword;
	public String getRepPassword() { return repPassword; }
	public void setRepPassword(String repPassword) { this.repPassword = repPassword; }
	
	@Transient
	private String role;
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	
	@Column(name = "aes_cipher_key", nullable = false)
	private String aesSecretKey;
	public String getAesSecretKey() { return aesSecretKey; }
	public void setAesSecretKey(String aesSecretKey) { this.aesSecretKey = aesSecretKey; }
	
	@AssertTrue(message="Repeat password field should be equal than password field")
	private boolean isValid() {
		return this.password.equals(this.repPassword);
	}
}