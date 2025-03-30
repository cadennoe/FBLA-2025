package org.fbla.caden.rest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.fbla.caden.controller.Account;
import org.fbla.caden.model.Filter;
import org.fbla.caden.model.IncomeAndExpense;
import org.fbla.caden.model.Search;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AccountController {
	
	private Account account;
	
	private Filter filter = Filter.ALL;
	private Search search = new Search();
	
	public AccountController() {
		this.account = new Account();
	}
	
	/**
	 * Initializes page on first viewing
	 * @param model for html
	 * @return page to move to
	 */
	@GetMapping("/")
	public String index(Model model) {
		this.filter = Filter.ALL;
		this.search = new Search();
		
		buildModel(model);
		return "account.html";
	}
	
	/**
	 * add a new income or expense
	 * updates total balance
	 * @param model for html
	 * @param income from html for new income or expense
	 * @return page to move to
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "add")
	public String addNew(Model model, @ModelAttribute("income") IncomeAndExpense income) {
		System.out.println("adding " + income.getAmount());
		account.addIncomeOrExpense(income);
		
		buildModel(model);
		
		if (income.isIncome()) {
			((IncomeAndExpense) model.getAttribute("income")).setIncome(true);
		}
		
		return "account.html";
	}
	
	/**
	 * edits existing income or expense
	 * @param model for html
	 * @param income from html to edit
	 * @return page to move to
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "edit")
	public String addEdit(Model model, @ModelAttribute("income") IncomeAndExpense income) {
		IncomeAndExpense found = null;
		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
 			if (income.getId() == incomeAndExpense.getId()) {
 				found = incomeAndExpense;
 			}
 			
 		}
		if (found != null) {
			found.setAmount(income.getAmount());
			found.setCategory(income.getCategory());
			found.setDate(income.getDate());
			found.setIncome(income.isIncome());
			account.getIncomeAndExpenseList().sort(Comparator.comparing(IncomeAndExpense::getDate).reversed());
 		}
		
		buildModel(model);
		return "account.html";
	}
	
	/**
	 * when user cancels, resets to empty as normal
	 * @param model for html
	 * @param income from html to cancel
	 * @return page to move to
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, params = "cancel")
	public String addCancel(Model model, @ModelAttribute("income") IncomeAndExpense income) {
		buildModel(model);
		return "account.html";
	}
	
	/**
	 * delete existing income or expense
	 * @param id of income or expense to delete
	 * @param model for html
	 * @return page to move to
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable int id, Model model) {
		
		account.deleteIncomeOrExpense(id);
		
		buildModel(model);
		return "account.html";
	}
	
	/**
	 * turning on edit mode
	 * @param id of selected income or expense
	 * @param model for html
	 * @return page to move to
	 */
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable int id, Model model) {
		
		buildModel(model);
		IncomeAndExpense found = null;
 		
 		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
 			if (id == incomeAndExpense.getId()) {
 				found = incomeAndExpense;
 			}
 		}
 		
 		if (found != null) {
 			System.out.println("editing id = " + found.getId());
			model.addAttribute("income", found);
			model.addAttribute("edit", true);
 		}
		return "account.html";
	}
	
	/**
	 * filters all incomes and expenses
	 * @param model for html
	 * @return page to move to
	 */
	@RequestMapping(value = "/filter", method = RequestMethod.POST, params = "all")
	public String filterAll(Model model) {
		this.filter = Filter.ALL;
		buildModel(model);
		return "account.html";
	}

	/**
	 * filters all incomes
	 * @param model for html
	 * @return page to move to
	 */
	@RequestMapping(value = "/filter", method = RequestMethod.POST, params = "income")
	public String filterIncome(Model model) {
		this.filter = Filter.INCOME;
		buildModel(model);
		return "account.html";
	}
	
	/**
	 * filters all expenses
	 * @param model for html
	 * @return page to move to
	 */
	@RequestMapping(value = "/filter", method = RequestMethod.POST, params = "expense")
	public String filterExpense(Model model) {
		this.filter = Filter.EXPENSE;
		buildModel(model);
		return "account.html";
	}

	/**
	 * searches for categories
	 * @param model for html
	 * @param search text to search on
	 * @return page to move to
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String add(Model model, @ModelAttribute("search") Search search) {
		System.out.println("Search = " + search.getText());
		this.search = search;
		
		buildModel(model);
		
		
		return "account.html";
	}
	
	/**
	 * base information for html every action
	 * @param model for html
	 */
	private void buildModel(Model model) {
		List<IncomeAndExpense> incomeAndExpenseList = createIncomeAndExpenseList();
		
		model.addAttribute("incomeAndExpenseList", incomeAndExpenseList);
		
		model.addAttribute("money", account.getMoney());
		IncomeAndExpense income = new IncomeAndExpense();
		income.setDate(LocalDate.now());
		model.addAttribute("income", income);
		model.addAttribute("filter", filter);
		model.addAttribute("search", search);
		model.addAttribute("edit", false);
		model.addAttribute("expenseCategoryData", getExpenseCategoryData());
		model.addAttribute("incomeCategoryData", getIncomeCategoryData());
		model.addAttribute("lastSixMonthData", getLastSixMonthData());
		model.addAttribute("lastFourWeekData", getLastFourWeekData());
	}

	/**
	 * displays income and expense list, also based on filters and searches
	 * @return list of incomes and expenses
	 */
	private List<IncomeAndExpense> createIncomeAndExpenseList() {
		List<IncomeAndExpense> incomeAndExpenseList = new ArrayList<>(account.getIncomeAndExpenseList());
		if (filter == Filter.INCOME) {
			incomeAndExpenseList.removeIf(incomeAndExpense -> !incomeAndExpense.isIncome());
		}
		if (filter == Filter.EXPENSE) {
			incomeAndExpenseList.removeIf(incomeAndExpense -> incomeAndExpense.isIncome());
		}
		
		if (search.getText() != null && !search.getText().trim().equals("")) {
			String text = search.getText().trim().toUpperCase();
			incomeAndExpenseList.removeIf(incomeAndExpense -> !incomeAndExpense.getCategory().toUpperCase().contains(text));
		}
		
		if (search.getStart() != null) {
			incomeAndExpenseList.removeIf(incomeAndExpense -> incomeAndExpense.getDate().isBefore(search.getStart()));
		}
		
		if (search.getEnd() != null) {
			incomeAndExpenseList.removeIf(incomeAndExpense -> incomeAndExpense.getDate().isAfter(search.getEnd()));
		}
		
		return incomeAndExpenseList;
	}
	
	/**
	 * grabbing values from lists to fill out graphs
	 * @return data for graphs
	 */
	
	private List<Double> getExpenseCategoryData() {
		double education = 0.0;
		double fees = 0.0;
		double food = 0.0;
		double groceries = 0.0;
		double travel = 0.0;
		double other = 0.0;
		
		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
			if ("Education" .equals(incomeAndExpense.getCategory())) {
				education += incomeAndExpense.getAmount();
			} else if ("Fees" .equals(incomeAndExpense.getCategory())) {
				fees += incomeAndExpense.getAmount();		
			} else if ("Food" .equals(incomeAndExpense.getCategory())) {
				food += incomeAndExpense.getAmount();		
			} else if ("Groceries" .equals(incomeAndExpense.getCategory())) {
				groceries += incomeAndExpense.getAmount();		
			} else if ("Travel" .equals(incomeAndExpense.getCategory())) {
				travel += incomeAndExpense.getAmount();		
			} else if ("Other Expense" .equals(incomeAndExpense.getCategory())) {
				other += incomeAndExpense.getAmount();		
			}
		}
		
		return List.of(education, fees, food, groceries, travel, other);
	}
	
	/**
	 * grabbing values from lists to fill out graphs
	 * @return data for graphs
	 */
	private List<Double> getIncomeCategoryData() {
		double allowance = 0.0;
		double work = 0.0;
		double otherIncome = 0.0;
		
		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
			if ("Allowance" .equals(incomeAndExpense.getCategory())) {
				allowance += incomeAndExpense.getAmount();
			} else if ("Work" .equals(incomeAndExpense.getCategory())) {
				work += incomeAndExpense.getAmount();		
			} else if ("Other Income" .equals(incomeAndExpense.getCategory())) {
				otherIncome += incomeAndExpense.getAmount();		
			} 
		}
		
		return List.of(allowance, work, otherIncome);
	}
	
	/**
	 * grabbing values from lists to fill out graphs
	 * @return data for graphs
	 */
	private List<Double> getLastSixMonthData() {
		double october = 0.0;
		double november = 0.0;
		double december = 0.0;
		double janurary = 0.0;
		double feburary = 0.0;
		double march = 0.0;
		
		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
			if (Month.OCTOBER .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					october += incomeAndExpense.getAmount();
				} else {
					october -= incomeAndExpense.getAmount();
				}
			} else if (Month.NOVEMBER .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					november += incomeAndExpense.getAmount();
				} else {
					november -= incomeAndExpense.getAmount();
				}	
			} else if (Month.DECEMBER .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					december += incomeAndExpense.getAmount();
				} else {
					december -= incomeAndExpense.getAmount();
				}		
			} else if (Month.JANUARY .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					janurary += incomeAndExpense.getAmount();
				} else {
					janurary -= incomeAndExpense.getAmount();
				}		
			} else if (Month.FEBRUARY .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					feburary += incomeAndExpense.getAmount();
				} else {
					feburary -= incomeAndExpense.getAmount();
				}		
			} else if (Month.MARCH .equals(incomeAndExpense.getDate().getMonth())) {
				if (incomeAndExpense.isIncome()) {
					march += incomeAndExpense.getAmount();
				} else {
					march -= incomeAndExpense.getAmount();
				}		
			}
		}
		
		return List.of(october, november, december, janurary, feburary, march);
	}
	
	/**
	 * grabbing values from lists to fill out graphs
	 * @return data for graphs
	 */
	private List<Double> getLastFourWeekData() {
		double thisWeek = 0.0;
		double lastWeek = 0.0;
		double threeWeeksAgo = 0.0;
		double fourWeeksAgo = 0.0;
		
		for (IncomeAndExpense incomeAndExpense : account.getIncomeAndExpenseList()) {
			LocalDate date = incomeAndExpense.getDate();
			if (date.isAfter(LocalDate.of(2025, 3, 29)) && date.isBefore(LocalDate.of(2025, 4, 6))) {
				if (incomeAndExpense.isIncome()) {
					thisWeek += incomeAndExpense.getAmount();
				} else {
					thisWeek -= incomeAndExpense.getAmount();
				}
			} else if (date.isAfter(LocalDate.of(2025, 3, 22)) && date.isBefore(LocalDate.of(2025, 3, 30))) {
				if (incomeAndExpense.isIncome()) {
					lastWeek += incomeAndExpense.getAmount();
				} else {
					lastWeek -= incomeAndExpense.getAmount();
				}	
			} else if (date.isAfter(LocalDate.of(2025, 3, 15)) && date.isBefore(LocalDate.of(2025, 3, 23))) {
				if (incomeAndExpense.isIncome()) {
					threeWeeksAgo += incomeAndExpense.getAmount();
				} else {
					threeWeeksAgo -= incomeAndExpense.getAmount();
				}		
			} else if (date.isAfter(LocalDate.of(2025, 3, 8)) && date.isBefore(LocalDate.of(2025, 3, 16))) {
				if (incomeAndExpense.isIncome()) {
					fourWeeksAgo += incomeAndExpense.getAmount();
				} else {
					fourWeeksAgo -= incomeAndExpense.getAmount();
				}		
			} 
		}
		
		return List.of(fourWeeksAgo, threeWeeksAgo, lastWeek, thisWeek);
	}
	
	/**
	 * set examples for demo
	 * @param model for demo
	 * @return demo info
	 */
	
	@GetMapping("/demo")
	public String demo(Model model) {
		
		IncomeAndExpense income1 = new IncomeAndExpense();

		income1 = new IncomeAndExpense();
		income1.setAmount(500);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2024, 10, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		income1 = new IncomeAndExpense();
		income1.setAmount(275);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2024, 11, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		income1 = new IncomeAndExpense();
		income1.setAmount(200);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2024, 12, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		income1 = new IncomeAndExpense();
		income1.setAmount(600);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2025, 1, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		income1 = new IncomeAndExpense();
		income1.setAmount(350);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2025, 2, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		income1 = new IncomeAndExpense();
		income1.setAmount(500);
		income1.setCategory("Work");
		income1.setDate(LocalDate.of(2025, 3, 28));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);

		income1 = new IncomeAndExpense();
		income1.setAmount(100);
		income1.setCategory("Allowance");
		income1.setDate(LocalDate.of(2025, 3, 1));
		income1.setIncome(true);
		account.addIncomeOrExpense(income1);
		
		
		IncomeAndExpense expense1 = new IncomeAndExpense();
		expense1.setAmount(25);
		expense1.setCategory("Food");
		expense1.setDate(LocalDate.of(2025, 3, 10));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(30);
		expense1.setCategory("Food");
		expense1.setDate(LocalDate.of(2025, 3, 13));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(18);
		expense1.setCategory("Food");
		expense1.setDate(LocalDate.of(2025, 3, 19));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(6);
		expense1.setCategory("Food");
		expense1.setDate(LocalDate.of(2025, 3, 24));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(75);
		expense1.setCategory("Education");
		expense1.setDate(LocalDate.of(2025, 3, 14));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(80);
		expense1.setCategory("Fees");
		expense1.setDate(LocalDate.of(2025, 3, 1));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(80);
		expense1.setCategory("Fees");
		expense1.setDate(LocalDate.of(2025, 2, 1));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(50);
		expense1.setCategory("Groceries");
		expense1.setDate(LocalDate.of(2025, 3, 10));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		
		expense1 = new IncomeAndExpense();
		expense1.setAmount(55);
		expense1.setCategory("Groceries");
		expense1.setDate(LocalDate.of(2025, 3, 2));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);

		expense1 = new IncomeAndExpense();
		expense1.setAmount(20);
		expense1.setCategory("Travel");
		expense1.setDate(LocalDate.of(2025, 3, 19));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);

		expense1 = new IncomeAndExpense();
		expense1.setAmount(100);
		expense1.setCategory("Other Expense");
		expense1.setDate(LocalDate.of(2025, 3, 2));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);

		expense1 = new IncomeAndExpense();
		expense1.setAmount(300);
		expense1.setCategory("Other Expense");
		expense1.setDate(LocalDate.of(2024, 12, 25));
		expense1.setIncome(false);
		account.addIncomeOrExpense(expense1);
		buildModel(model);
		return "account.html";
	}
}

