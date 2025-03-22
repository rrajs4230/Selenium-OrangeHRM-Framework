package com.orangehrm.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtility {

	// Metgod to send the GET request
	public static Response sendGetRequest(String endPoint) {

		return RestAssured.get(endPoint);
	}

	// Method to send the POST Request

	public static void sendPostRequest(String endPoint, String payLoad) {

		RestAssured.given().header("Content-Type", "application/json").body(payLoad).post();
	}

	// Method to validate the response status

	public static boolean validateStatusCode(Response response, int statusCode) {

		return response.getStatusCode() == statusCode;
	}

	// Method to extarct value from JASON

	public static String getJsonValue(Response response, String value) {

		return response.jsonPath().getString(value);
	}

}
