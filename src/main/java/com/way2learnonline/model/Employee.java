package com.way2learnonline.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Employee {
	@Id @GeneratedValue
	private Long id;
	private String name;	
	private String empId;
	private String emailId;
	
	
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Long getCommentId() {
		return id;
	}
	public void setCommentId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return empId;
	}
	public void setId(String id) {
		this.empId = id;
	} 
	
	
	
	

}
