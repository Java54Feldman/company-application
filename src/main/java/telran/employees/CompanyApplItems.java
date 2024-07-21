package telran.employees;

import java.util.*;

import telran.employees.*;
import telran.view.InputOutput;
import telran.view.Item;

//public void addEmployee(Employee empl);
//public Employee getEmployee(long id);
//public Employee removeEmployee(long id);
//public int getDepartmentBudget(String department);
//public String[] getDepartments();
//public Manager[] getManagersWithMostFactor();

public class CompanyApplItems {
	static Company company;
	static HashSet<String> departments;

	public static List<Item> getCompanyItems(Company company, HashSet<String> departments) {
		CompanyApplItems.company = company;
		CompanyApplItems.departments = departments;
		Item[] items = { Item.of("Add employee", CompanyApplItems::addEmployee),
				Item.of("Display employee data", CompanyApplItems::getEmployee),
				Item.of("Remove employee", CompanyApplItems::removeEmployee),
				Item.of("Display department budget", CompanyApplItems::getDepartmentBudget),
				Item.of("Display departments", CompanyApplItems::getDepartments),
				Item.of("Display managers with most factor", CompanyApplItems::getManagersWithMostFactor) };
		return new ArrayList<Item>(List.of(items));
	}

	static void addEmployee(InputOutput io) {
		Employee empl = readEmployee(io);
		String type = io.readStringOptions("Enter employee type (WageEmployee, Manager, SalesPerson)",
				"Wrong employee type", new HashSet<String>(List.of("WageEmployee", "Manager", "SalesPerson")));
		Employee result = switch (type) {
		case "WageEmployee" -> getWageEmployee(empl, io);
		case "Manager" -> getManager(empl, io);
		case "SalesPerson" -> getSalesPerson(empl, io);
		default -> null;
		};
		company.addEmployee(result);
		io.writeLine("Employee has been added");
	}

	private static Employee getSalesPerson(Employee empl, InputOutput io) {
		WageEmployee wageEmployee = (WageEmployee) getWageEmployee(empl, io);
		float percents = io.readNumberRange("Enter percents", "Wrong percents value", 0.5, 2).floatValue();
		long sales = io.readNumberRange("Enter sales", "Wrong sales value", 500, 50000).longValue();
		return new SalesPerson(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), wageEmployee.getHours(),
				wageEmployee.getWage(), percents, sales);
	}

	private static Employee getManager(Employee empl, InputOutput io) {
		float factor = io.readNumberRange("Enter manager factor", "Wrong factor value", 1.5, 5).floatValue();
		return new Manager(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), factor);
	}

	private static Employee getWageEmployee(Employee empl, InputOutput io) {
		int hours = io.readNumberRange("Enter working hours", "Wrong hours value", 10, 200).intValue();
		int wage = io.readNumberRange("Enter hour wage", "Wrong wage value", 100, 1000).intValue();
		return new WageEmployee(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), hours, wage);
	}

	private static Employee readEmployee(InputOutput io) {
		long id = io.readNumberRange("Enter id value", "Wrong id value", 1000, 10000).longValue();
		int basicSalary = io.readNumberRange("Enter basic salary", "Wrong basic salary", 2000, 20000).intValue();
		String department = io.readStringOptions("Enter department " + departments, "Wrong department", departments);
		return new Employee(id, basicSalary, department);
	}

	static void getEmployee(InputOutput io) {
		long id = io.readNumberRange("Enter id", "Wrong id value", 1000, 10000).longValue();
		Employee empl = company.getEmployee(id);
		if (empl != null) {
			String typeEmpl = empl.getClass().getSimpleName();
			io.writeString(String.format(
					"Employee data:\n" + "%s\n" + "id = %d\n" + "basic salary = %d\n" + "department = %s\n", 
					typeEmpl, id, empl.getBasicSalary(), empl.getDepartment()));
			if (empl instanceof Manager) {
				io.writeString(String.format("factor = %.2f\n", ((Manager)empl).getFactor()));
			} else if(empl instanceof WageEmployee || empl instanceof SalesPerson) {
				io.writeString(String.format("hours = %d\n" + "wage = %d\n", ((WageEmployee)empl).getHours(), ((WageEmployee)empl).getWage()));
			} 
			if(empl instanceof SalesPerson) {
				io.writeString(String.format("percent = %.2f\n" + "sales = %d\n", ((SalesPerson)empl).getPercent(), ((SalesPerson)empl).getSales()));
			} 
		} else {
			io.writeString(String.format("Employee with id = %d not found\n", id));
		}
	}

	static void removeEmployee(InputOutput io) {
		long id = io.readNumberRange("Enter id", "Wrong id value", 1000, 10000).longValue();
		if(company.getEmployee(id) != null) {
			company.removeEmployee(id);
			io.writeString(String.format("Employee with id = %d has been removed\n", id));
		} else {
			io.writeString(String.format("Employee with id = %d not found\n", id));
		}
	}

	static void getDepartmentBudget(InputOutput io) {
		String department = io.readStringOptions("Enter department " + departments, "Wrong department", departments);
		int budget = company.getDepartmentBudget(department);
		io.writeString(String.format("%s department budget = %d\n", department, budget));
	}

	static void getDepartments(InputOutput io) {
		io.writeString(String.join(" ", company.getDepartments()) + "\n");
	}

	static void getManagersWithMostFactor(InputOutput io) {
		Manager[] managers = company.getManagersWithMostFactor();
		if(managers.length > 0) {
			float factor = managers[0].getFactor();
			io.writeString(String.format("Factor = %.2f have the following managers:\n", factor));
			for (Manager manager : managers) {
				io.writeString(String.format("Manager with id = %d\n", manager.getId()));
			}
		} else {
			io.writeString("No managers in the company");
		}
	}
}
