package com.way2learnonline.dto;

import com.way2learnonline.model.Employee;

/**
 * @author sshet
 *
 */
public class EmployeeDTO {

	String name;
	String id;
	String emailId;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static EmployeeDTO createEmployeeDTO(Employee employee) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		employeeDTO.setId(employee.getId());
		employeeDTO.setName(employee.getName());
		employeeDTO.setEmailId(employee.getEmailId());
		return employeeDTO;
	}

}
