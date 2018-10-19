package com.way2learnonline.controllers;

import static org.assertj.core.api.Assertions.shouldHaveThrown;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.way2learnonline.JpaConfig;
import com.way2learnonline.dto.CabBookingsDTO;
import com.way2learnonline.dto.CommentsDTO;
import com.way2learnonline.dto.EmployeeDTO;
import com.way2learnonline.mailer.MailUtil;
import com.way2learnonline.model.CabBookings;
import com.way2learnonline.model.Comment;
import com.way2learnonline.model.Employee;
import com.way2learnonline.repository.CabBookRepository;
import com.way2learnonline.repository.CommentsRepository;
import com.way2learnonline.repository.EmployeeRepository;

@CrossOrigin
@Controller
@RequestMapping("/servers")
public class ServerController {

	ApplicationContext context = new AnnotationConfigApplicationContext(JpaConfig.class);
	CommentsRepository commentsRepository = context.getBean(CommentsRepository.class);
	EmployeeRepository employeeRepository = context.getBean(EmployeeRepository.class);
	CabBookRepository cabBookRepository = context.getBean(CabBookRepository.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<CommentsDTO> getAllComments() {

		Iterable<Comment> commentsList = commentsRepository.findAll();

		List<CommentsDTO> commentsDTOList = new ArrayList<CommentsDTO>();

		for (Comment comment : commentsList) {
			commentsDTOList.add(CommentsDTO.createCommentDTO(comment));
		}

		return commentsDTOList;
	}

	@RequestMapping(value = "/getEmps", method = RequestMethod.GET)
	public @ResponseBody List<EmployeeDTO> getAllEmps() {

		Iterable<Employee> empsList = employeeRepository.findAll();

		List<EmployeeDTO> empDTOList = new ArrayList<EmployeeDTO>();

		for (Employee emplyoee : empsList) {
			empDTOList.add(EmployeeDTO.createEmployeeDTO(emplyoee));
		}

		return empDTOList;
	}

	@RequestMapping(value = "/getTodaysBookings", method = RequestMethod.GET)
	public @ResponseBody List<CabBookingsDTO> getTodaysBookings() {
		Iterable<CabBookings> cabBookingList = cabBookRepository.getTodaysBookings();

		List<CabBookingsDTO> cabBookingDTOList = new ArrayList<CabBookingsDTO>();

		for (CabBookings cabBookings : cabBookingList) {
			cabBookingDTOList.add(CabBookingsDTO.createCabBookingDTO(cabBookings));
		}

		return cabBookingDTOList;
	}

	@RequestMapping(value = "/getCurrentBookings", method = RequestMethod.GET)
	public @ResponseBody List<CabBookingsDTO> getCurrentBookings() {
		Iterable<CabBookings> cabBookingList = cabBookRepository.findAll();

		List<CabBookingsDTO> cabBookingDTOList = new ArrayList<CabBookingsDTO>();

		for (CabBookings cabBookings : cabBookingList) {
			cabBookingDTOList.add(CabBookingsDTO.createCabBookingDTO(cabBookings));
		}

		return cabBookingDTOList;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addNewComment(@RequestBody CommentsDTO CommentsDTO) {

		Comment server = CommentsDTO.createNewComent();
		server = commentsRepository.save(server);

		URI newServerURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(CommentsDTO.getId()).toUri();

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(newServerURI);

		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}

	@SuppressWarnings("null")
	@RequestMapping(value = "/bookCab", method = RequestMethod.POST)
	public ResponseEntity<?> bookCab(@RequestBody CabBookingsDTO cabBookingsDTO) {
		int beginIndex = cabBookingsDTO.getEmployeeName().indexOf("(");
		int endIndex = cabBookingsDTO.getEmployeeName().lastIndexOf(")");
		Long empId = Long.valueOf(cabBookingsDTO.getEmployeeName().substring(beginIndex + 1, endIndex));
		cabBookingsDTO.setEmployeeID(empId);
		HttpHeaders responseHeaders = new HttpHeaders();
		boolean alreadyBooked = checkForEmpBookingsExists(cabBookingsDTO);
		if (!alreadyBooked) {
			CabBookings newCab = cabBookingsDTO.bookNewCab();
			newCab = cabBookRepository.save(newCab);

			cabBookingsDTO.createCabBookingDTO(newCab, cabBookingsDTO);

			URI newServerURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(newCab.getBookingId()).toUri();

			responseHeaders.setLocation(newServerURI);

			new MailUtil().sendMail(cabBookingsDTO.getEmailId(), "Cab Booked successfully");

		}

		return new ResponseEntity<>(cabBookingsDTO, responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/updateCab", method = RequestMethod.POST)
	public ResponseEntity<CabBookingsDTO> updateCab(@RequestBody CabBookingsDTO cabBookingsDTO) {
		int beginIndex = cabBookingsDTO.getEmployeeName().indexOf("(");
		int endIndex = cabBookingsDTO.getEmployeeName().lastIndexOf(")");
		Long empId = Long.valueOf(cabBookingsDTO.getEmployeeName().substring(beginIndex + 1, endIndex));
		cabBookingsDTO.setEmployeeID(empId);
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newServerURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(cabBookingsDTO.getBookingId()).toUri();

		responseHeaders.setLocation(newServerURI);

		boolean alreadyBooked = checkForEmpBookingsExists(cabBookingsDTO);
		int updateStatus = 0;
		if (alreadyBooked) {
			updateStatus = updateCurrentBooking(cabBookingsDTO);
			if (updateStatus > 0) {
				cabBookingsDTO.setError_msg("UPDATED");
				new MailUtil().sendMail(cabBookingsDTO.getEmailId(), "Cab Updated successfully");

			}

			return new ResponseEntity<CabBookingsDTO>(cabBookingsDTO, responseHeaders, HttpStatus.OK);
		}

		cabBookingsDTO.setError_msg("NOT_UPDATED");

		return new ResponseEntity<CabBookingsDTO>(cabBookingsDTO, responseHeaders, HttpStatus.OK);
	}

	private int updateCurrentBooking(CabBookingsDTO cabBookingsDTO) {

		int status = 0;
		long bookingId = compareBookingBeforeUpdating(cabBookingsDTO);
		if (bookingId == 0) {
			return status;
		} else {

			return cabBookRepository.updateBookings(cabBookingsDTO.getDropPoint(), cabBookingsDTO.getRouteNumber(),
					cabBookingsDTO.getTime(), bookingId);
		}

	}

	private Long compareBookingBeforeUpdating(CabBookingsDTO cabBookingsDTO) {
		// checking if there are changes in the booking details
		long bookingId = 0;
		List<CabBookings> cabBookingList = cabBookRepository.checkExistingBookings(cabBookingsDTO.getEmployeeID());

		List<CabBookingsDTO> cabBookingDTOList = new ArrayList<CabBookingsDTO>();

		for (CabBookings cabBookings : cabBookingList) {

			cabBookingDTOList.add(CabBookingsDTO.createCabBookingDTO(cabBookings));

		}

		if ((cabBookingDTOList.size() != 0)) {
			CabBookingsDTO oldBooking = cabBookingDTOList.get(0);
			bookingId = oldBooking.getBookingId();
			if ((oldBooking.getRouteNumber().equals(cabBookingsDTO.getRouteNumber())
					&& oldBooking.getTime().equals(cabBookingsDTO.getTime())
					&& oldBooking.getDropPoint().equals(cabBookingsDTO.getDropPoint()))) {
				return (long) 0; // not required to update
			}

		}
		return bookingId;

	}

	public boolean checkForEmpBookingsExists(CabBookingsDTO cabBookingsDTO) {

		int beginIndex = cabBookingsDTO.getEmployeeName().indexOf("(");
		int endIndex = cabBookingsDTO.getEmployeeName().lastIndexOf(")");
		Long empId = Long.valueOf(cabBookingsDTO.getEmployeeName().substring(beginIndex + 1, endIndex));
		ArrayList<CabBookings> cabBookingList = cabBookRepository.checkExistingBookings(cabBookingsDTO.getEmployeeID());
		if (cabBookingList.isEmpty()) {
			return false;
		}

		return true;

	}

	@CrossOrigin
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCommentById(@PathVariable("id") Long Id) {

		commentsRepository.delete(Id);

		URI newServerURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(Id).toUri();

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(newServerURI);

		return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);

	}
}
