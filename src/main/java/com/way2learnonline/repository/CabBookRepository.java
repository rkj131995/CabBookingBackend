package com.way2learnonline.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.way2learnonline.dto.CabBookingsDTO;
import com.way2learnonline.model.CabBookings;

public interface CabBookRepository extends CrudRepository<CabBookings, Long> {

	@Query(value=" from CabBookings cb  where   DATE(cb.createDateTime)=CURDATE()")
	public Iterable<CabBookings> getTodaysBookings();
	
	
	@Query(value="from CabBookings where employeeID=?1  and  DATE(createDateTime)=CURDATE()")
	public ArrayList<CabBookings> checkExistingBookings(Long employeeID);
	
	@Transactional
	@Modifying
	@Query(value="update CabBookings c set c.dropPoint =?1 , c.routeNumber =?2 , c.time =?3 , c.modifiedDateTime = CURDATE() "
			+ " where c.bookingId=?4 and DATE(createDateTime)=CURDATE() ")
	public int updateBookings(String dropPoint, String routeNumber, String time, Long bookingId);
	
	
	
}