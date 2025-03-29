package org.fbla.caden.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fbla.caden.model.IncomeAndExpense;

public class Account {

	
	private List<IncomeAndExpense> incomeAndExpenseList = new ArrayList<>();
	
	public void addIncomeOrExpense(IncomeAndExpense income) {

		incomeAndExpenseList.add(income);
		incomeAndExpenseList.sort(Comparator.comparing(IncomeAndExpense::getDate).reversed());
	}

 	public void deleteIncomeOrExpense(int id) {
 		IncomeAndExpense found = null;
 		
 		for (IncomeAndExpense incomeAndExpense : incomeAndExpenseList) {
 			if (id == incomeAndExpense.getId()) {
 				found = incomeAndExpense;
 			}
 		}
 		
 		if (found != null) {		
			incomeAndExpenseList.remove(found);
 		}
 	}
	
	public double getMoney() {
		double money = 0.0;
		for (IncomeAndExpense incomeAndExpense : incomeAndExpenseList) {
			if (incomeAndExpense.isIncome()) {
				money += incomeAndExpense.getAmount();
			} else {
				money -= incomeAndExpense.getAmount();
			}
		}
		return money;
	}
	public List<IncomeAndExpense> getIncomeAndExpenseList() {
		return incomeAndExpenseList;
	}

}
