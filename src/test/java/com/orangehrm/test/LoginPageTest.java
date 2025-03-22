package com.orangehrm.test;

import org.testng.Assert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {

		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());

	}

	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
	public void verifyValidLoginTest(String username, String password) {

		// ExtentManager.startTest("Valid Login Test"); --This has been Implemented in
		// TestListener
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login(username, password);
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible after succesfull login");
		ExtentManager.logStep("Validation Successful");
		homePage.logout();
		ExtentManager.logStep("Logged out Successfully");
		staticWait(2);
	}

	@Test(dataProvider = "inValidLoginData", dataProviderClass = DataProviders.class)
	public void inValidLoginTest(String username, String password) {

		// ExtentManager.startTest("Invalid Login Test"); --This has been Implemented in
		// TestListener
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login(username, password);
		String expectedErrorMessage = "Invalid credentials1";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage), "Test Failed: InValid Error Message");
		ExtentManager.logStep("Validation Successful");
		ExtentManager.logStep("Logged out Successfully");

	}

}
