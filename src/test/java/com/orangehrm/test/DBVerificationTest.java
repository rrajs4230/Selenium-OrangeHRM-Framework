package com.orangehrm.test;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DBConnection;
import com.orangehrm.utilities.ExtentManager;

public class DBVerificationTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {

		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());

	}

	@Test
	public void verifyEmployeeNameVerificationFromDB() {

		SoftAssert softAssert = getSoftAssert();

		ExtentManager.logStep("Logging with Admin Credentials");
		loginPage.login(prop.getProperty("username"), prop.getProperty("password"));

		ExtentManager.logStep("click on PIM tab");
		homePage.clickOnPIMTab();

		homePage.employeeSearch("Rakesh");
		homePage.clickOnSearch();
		staticWait(30);
		homePage.scrollDown();

		ExtentManager.logStep("Get the Employee Name from DB");
		String employee_id = "0002";

		// Fetch the data into a map

		Map<String, String> employeeDetails = DBConnection.getEmployeeDetails(employee_id);

		String emplfirstName = employeeDetails.get("firstName");
		String emplMiddleName = employeeDetails.get("middleName");
		String emplastName = employeeDetails.get("lastName");

		String empFirstAndMiddleName = (emplfirstName).trim();

		// Validation for first and middle name
		ExtentManager.logStep("Verify the employee first and middle name");
		softAssert.assertTrue(homePage.verifyEmployeeFirstAndMiddleName(empFirstAndMiddleName),
				"First and Middle name are not Matching");

		// validation for last name
		ExtentManager.logStep("Verify the employee last name");

		softAssert.assertTrue(homePage.verifyEmployeeLastName(emplastName));

		ExtentManager.logStep("DB Validation Completed...");

		softAssert.assertAll();
	}

}
