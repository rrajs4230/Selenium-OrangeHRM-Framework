package com.orangehrm.test;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass extends BaseClass {

	@Test
	public void dummyTest() {
         
		//ExtentManager.startTest("DummyTest1 Test"); -- --This has been Implemented in TestListener
		String title = getDriver().getTitle();
		ExtentManager.logStep("Verifying the title");
		assert title.equals("OrangeHRM") : "Test Failed - Title is not Matching";

		System.out.println("Test Passed - Title is Matching");
		//ExtentManager.logSkip("This Case is skipped");
		throw new SkipException("Skipping the Test As part of Testing");
	}

}
