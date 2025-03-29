package org.fbla.caden.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class IncomeAndExpense {
	
	private static int idCounter = 0;
	
	private int id;
	private double amount;
	private String category = "";
	
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private LocalDate date;
	
	private boolean income = false;
	
	public IncomeAndExpense() {
		this.id = idCounter++;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public boolean isIncome() {
		return income;
	}
	public void setIncome(boolean income) {
		this.income = income;
	}
}
