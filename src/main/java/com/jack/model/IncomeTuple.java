package com.jack.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data 									//We want lombok to write getters and setters
@Component	
public class IncomeTuple {

	private String vendor;
	private BigDecimal netIncome;
	private String categories;
	
	/* CONSTRUCTORS */
	public IncomeTuple() {
		
	}
	
	public IncomeTuple(String vendor, BigDecimal netIncome, String categories) {
		super();
		this.vendor = vendor;
		this.netIncome = netIncome;
		this.categories = categories;
	}
	
	
	
}
