package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {

		int explicitwait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitwait));
		logger.info("WebDriver instance is created");
	}

	// wait for the page to load

	public void waitForPageLoad(int timeOutInSec) {

		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) driver)
					.executeScript("return document.readyState").equals("completed"));
			logger.info("Page loaded successfully");
		} catch (Exception e) {

			logger.error("Page did not load within " + timeOutInSec + " seconds. Exception:" + e.getMessage());
		}

	}

	// Method to click an Element
	public void click(By by) {

		String elementDescription = getElementDescription(by);

		try {
			applyBorder(by, "green");
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("click an element:" + elementDescription);
			logger.info("click an element--->" + elementDescription);
		} catch (Exception e) {
			applyBorder(by, "red");
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element:", elementDescription);
			logger.error("Unable to click element:" + e.getMessage());
		}

	}

	// Method to enter text into an input field

	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text:" + getElementDescription(by) + "--->" + value);

		} catch (Exception e) {

			applyBorder(by, "red");
			logger.error("Unable To enter the value:" + e.getMessage());

		}
	}

	// Method To GetText
	public String getText(By by) {

		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unbale to get the text:" + e.getMessage());
			return "";
		}

	}

//Method To Compare Two Text

	public boolean compareText(By by, String expectedText) {

		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();

			if (expectedText.equals(actualText)) {

				applyBorder(by, "green");
				logger.info("Text are Matching:" + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare Text",
						"Text Verified Successfully!" + actualText + " equals " + expectedText);
				return true;
			} else {
				applyBorder(by, "red");
				logger.info("Text are not Matching:" + actualText + " Not equals " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison Failed!",
						"Text Comparison Failed! " + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {

			applyBorder(by, "red");
			logger.error("Unable to Compare Texts" + e.getMessage());
		}
		return false;

	}

	// Method to check if an Element is displayed

	public boolean isDisplayed(By by) {

		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			logger.info("Element is displayed--->" + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is Displayed",
					"Element is Displayed:" + getElementDescription(by));
			return driver.findElement(by).isDisplayed();

		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Element is not displayed--->" + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element is not displayed: ",
					"Element is not displayed:" + getElementDescription(by));
			return false;
		}
	}

	// Scroll to an Element

	public void scrollToElement(By by) {

		try {
			applyBorder(by, "green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to locate Element:" + e.getMessage());
		}

	}

	// wait for element to be clickable

	private void waitForElementToBeClickable(By by) {

		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {

			logger.error("element is not clickable:" + e.getMessage());
		}
	}

	// wait for element to be visible

	private void waitForElementToBeVisible(By by) {

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {

			logger.error("element is not visible:" + e.getMessage());
		}
	}

	// Method to the description of an element using By locator

	public String getElementDescription(By locator) {

		// check for null driver or locator to avoid NullPointer Exception

		if (driver == null)
			return "driver is null";
		if (locator == null)

			return "locator is null";

		// find the element using the locator

		try {
			WebElement element = driver.findElement(locator);

			// Get Element Attributes

			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String text = element.getText();
			String className = element.getDomAttribute("className");
			String placeHolder = element.getDomAttribute("placeHolder");

			// Return the description based on element attributes

			if (isNotEmpty(name)) {

				return "Element with name:" + name;

			} else if (isNotEmpty(id)) {

				return "Element with id:" + id;
			} else if (isNotEmpty(text)) {

				return "Element with text:" + truncate(text, 50);
			} else if (isNotEmpty(className)) {

				return "Element with className:" + className;
			} else if (isNotEmpty(placeHolder)) {

				return "Element with placeHolder:" + placeHolder;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

			logger.error("Unable to describe the element" + e.getMessage());
		}
		return "Unable to describe the element";

	}

	// utility method to check a String is not Null or empty

	private boolean isNotEmpty(String value) {

		return value != null && !value.isEmpty();
	}

	// utility Method to truncate long String

	private String truncate(String value, int maxLength) {

		if (value == null || value.length() <= maxLength) {

			return value;
		}

		return value.substring(0, maxLength) + "....";

	}

	// Utility Method to Border an Element

	public void applyBorder(By by, String color) {

		try {
			// Locate the element

			WebElement element = driver.findElement(by);
			// Apply the border

			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color " + color + "to element " + getElementDescription(by));
		} catch (Exception e) {

			logger.warn("Failed to apply the border to an Element: " + getElementDescription(by), e);
		}
	}

}
