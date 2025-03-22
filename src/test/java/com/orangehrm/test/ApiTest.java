package com.orangehrm.test;



import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.utilities.ApiUtility;
import com.orangehrm.utilities.ExtentManager;


import io.restassured.response.Response;

public class ApiTest {

	@Test
	public void verifyGetUserAPI() {

		SoftAssert softAssert = new SoftAssert();

		// Step1: Define API Endpoint
		String endPoint = "https://jsonplaceholder.typicode.com/users/1";

		ExtentManager.logStep("API Endpoint :" + endPoint);

		// Step2: Send GET Request

		ExtentManager.logStep("Sending GET Request to the API");
		Response response = ApiUtility.sendGetRequest(endPoint);

		// Step3: Validate staus Code

		ExtentManager.logStep("Validating API Response status code");

		boolean isStatusCodeValid = ApiUtility.validateStatusCode(response, 201);

		softAssert.assertTrue(isStatusCodeValid, "Status code is not as Expected");

		if (isStatusCodeValid) {

			ExtentManager.logStepValidationForAPI("Satus Code Validation Passed!");

		} else {

			ExtentManager.logFailure("Status Code Validation Failed!");
		}

		// Step:4 validate userName
		ExtentManager.logStep("Validating response body for username");
		String userName = ApiUtility.getJsonValue(response, "username");
		boolean isUserNameValid = "Bret".equals(userName);
		softAssert.assertTrue(isUserNameValid, "Username is not valid");
		if (isUserNameValid) {

			ExtentManager.logStepValidationForAPI("UserName Validation Passed!");

		} else {

			ExtentManager.logFailure("UserName Validation Failed!");
		}

		// Step:4 validate email
		ExtentManager.logStep("Validating response body for email");
		String userEmail = ApiUtility.getJsonValue(response, "email");
		boolean isEmailValid = "Sincere@april.biz".equals(userEmail);
		softAssert.assertTrue(isEmailValid, "Email is not valid");
		if (isEmailValid) {

			ExtentManager.logStepValidationForAPI("Email Validation Passed!");

		} else {

			ExtentManager.logFailure("Email Validation Failed!");
		}
		
		softAssert.assertAll();

	}

}
