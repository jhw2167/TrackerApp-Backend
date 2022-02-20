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
public class SummaryTuple {

	private String aggregateCol;
	private BigDecimal value;
	private String categories;
	
	/* CONSTRUCTORS */
	public SummaryTuple() {
		
	}
	
	public SummaryTuple(String aggregateCol, BigDecimal value, String categories) {
		super();
		this.aggregateCol = aggregateCol;
		this.value = value;
		this.categories = categories;
	}
	
	
	
}
