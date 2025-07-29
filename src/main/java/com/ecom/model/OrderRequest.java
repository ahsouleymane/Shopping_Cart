package com.ecom.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {


	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String mobileNo;
	
	private String Address;
	
	private String city;
	
	private String state;
	
	private String pincode;
	
	private String paymentType;

}
