package com.at4wireless.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {
	@Id @Column(name="username")
	@Size(min=5, max=13)
	private String user;
	
	@Column(name="password")
	@Size(min=5, max=13)
	private String password;
	
	@Transient
	//@Size(min=5, max=13)
	private String repPassword;
	
	@Transient
	private String role;
	
	@AssertTrue(message="Repeat password field should be equal than password field")
	private boolean isValid() {
		return this.password.equals(this.repPassword);
	}
	
	/*@NotEmpty @Email
	private String email;*/
	
	/*public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}*/
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepPassword() {
		return repPassword;
	}
	public void setRepPassword(String repPassword) {
		this.repPassword = repPassword;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
