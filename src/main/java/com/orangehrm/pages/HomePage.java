package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage {

	private ActionDriver actionDriver;

	// Define locators using By class

	private By adminTab = By.xpath("//span[text()='Admin']");
	private By userIDButton = By.className("oxd-userdropdown-name");
	private By logoutButton = By.xpath("//a[text()='Logout']");
	private By orangeHRMlogo = By.xpath("//div[@class='oxd-brand-banner']//img");
	private By pimTab = By.xpath("//span[text()='PIM']");
	private By employeeSearch = By
			.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div/div/div/input");
	private By searchButton = By.xpath("//button[@type='submit']");
	private By empFirstAndMiddleName = By.xpath("(//div[@class='oxd-table-card']/div/div/div)[3]");
	private By emplastName = By.xpath("(//div[@class='oxd-table-card']/div/div/div)[4]");
	/*
	 * public HomePage(WebDriver driver) {
	 * 
	 * this.actionDriver = new ActionDriver(driver); }
	 */

	public HomePage(WebDriver driver) {

		this.actionDriver = BaseClass.getactionDriver();
	}

	// Method to verify if Admin tab is visible

	public boolean isAdminTabVisible() {

		return actionDriver.isDisplayed(adminTab);

	}

	public boolean verifyOrangeHRMlogo() {
		return actionDriver.isDisplayed(orangeHRMlogo);

	}

	// Method to Navigate to PIM tab
	public void clickOnPIMTab() {

		actionDriver.click(pimTab);
	}

	// Employee Search

	public void employeeSearch(String value) {

		actionDriver.enterText(employeeSearch, value);

	}

	// Click on SearchButton

	public void clickOnSearch() {

		actionDriver.click(searchButton);
	}

	// ScrollDownToThe Element

	public void scrollDown() {

		actionDriver.scrollToElement(empFirstAndMiddleName);

	}

	// Verify employee first and middle Name

	public boolean verifyEmployeeFirstAndMiddleName(String empFirstAndMiddleNameFromDB) {

		return actionDriver.compareText(empFirstAndMiddleName, empFirstAndMiddleNameFromDB);
	}

	// Verify employee first and last Name

	public boolean verifyEmployeeLastName(String emplastNameFromDB) {

		return actionDriver.compareText(emplastName, emplastNameFromDB);
	}

	public void logout() {

		actionDriver.click(userIDButton);
		actionDriver.click(logoutButton);

	}

}
