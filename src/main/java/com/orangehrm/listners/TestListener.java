package com.orangehrm.listners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListener implements ITestListener, IAnnotationTransformer {

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
		
	}

	// Triggered When a test starts
	@Override
	public void onTestStart(ITestResult result) {

		String testName = result.getMethod().getMethodName();
		// Start logging in Extent Reports
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test Started: " + testName);
	}

	// Triggered When a test succeeds
	@Override
	public void onTestSuccess(ITestResult result) {

		String testName = result.getMethod().getMethodName();

		if (!result.getTestClass().getName().toLowerCase().contains("api")) {

			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test Passed Successfully!",
					"Test End:" + testName + "Test End: " + testName + "Test End: " + testName + " -  Test Passed");
		} else {

			ExtentManager.logStepValidationForAPI(
					"Test End:" + testName + "Test End: " + testName + "Test End: " + testName + " -  Test Passed");
		}
	}

	// Triggered When a Test Fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		if(!result.getTestClass().getName().toLowerCase().contains("api")) {
			
			ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed!",
					"Test End:" + testName + "Test End: " + testName + "  Test failed");
		}
		else {
			
			ExtentManager.logFailure("Test End:" + testName + "Test End: " + testName + "  Test failed");
		}
		
	}

	// Triggered When a Test Skips
	@Override
	public void onTestSkipped(ITestResult result) {

		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped " + testName);

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedWithTimeout(result);
	}

	// Triggered when a suite Starts
	@Override
	public void onStart(ITestContext context) {

		ExtentManager.getReporter();
	}

	// Triggered when a suite Ends
	@Override
	public void onFinish(ITestContext context) {

		// Flush the Extent Reports

		ExtentManager.endTest();
	}

}
