package org.fbla.caden.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class Search {
	
	private String text;
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private LocalDate start;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private LocalDate end;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LocalDate getStart() {
		return start;
	}
	public void setStart(LocalDate start) {
		this.start = start;
	}
	public LocalDate getEnd() {
		return end;
	}
	public void setEnd(LocalDate end) {
		this.end = end;
	}
	
}
