package com.kushki_test.model;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class suscription implements Serializable{
    private static final long serialVersionUID = 1L;
	
	private String token;
	private String name;
	private String lastName;
	private String document;
	private String message;
	private String type;
	private String idSubscription;
	private String ticket;
	private String param;
}
