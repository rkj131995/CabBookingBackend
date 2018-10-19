package com.way2learnonline.repository;


import org.springframework.data.repository.CrudRepository;

import com.way2learnonline.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}

