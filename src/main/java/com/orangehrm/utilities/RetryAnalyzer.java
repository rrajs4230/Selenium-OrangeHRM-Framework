package com.orangehrm.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
	
	private int retryCount =0; // Number of retries
	
	private static final int maxRetryCount = 1; //maximum no of retries

	@Override
	public boolean retry(ITestResult result) {
		
		if(retryCount<maxRetryCount) {
			
			retryCount++;
			return true; // Retry the test
		}
		return false;
	}

}
