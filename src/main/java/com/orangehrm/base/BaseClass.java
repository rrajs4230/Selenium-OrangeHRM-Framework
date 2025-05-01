package com.orangehrm.base;

import java.io.FileInputStream;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	// Getter Method for Soft Assert
	public SoftAssert getSoftAssert() {

		return softAssert.get();

	}

	@BeforeSuite
	public void loadConfig() throws IOException {// Load the configuration file

		prop = new Properties();
		FileInputStream fis = new FileInputStream("src//main//resources//config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");

		// Start the ExtentReport

		// ExtentManager.getReporter(); --This has been Implemented in TestListener

	}

	/*
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private synchronized void launchBrowser() {

		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {

			// Create ChromeOtions

			ChromeOptions options = new ChromeOptions();
			//options.addArguments("--headless"); // Run Chrome in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU for headless mode
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource
			options.addArguments("--no-sandbox"); // Required for some CI enviornments l

			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options));
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver Instance is created");
		} else if (browser.equalsIgnoreCase("firefox")) {

			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("FirefoxDriver Instance is created");

		} else if (browser.equalsIgnoreCase("edge")) {

			// driver = new EdgeDriver();
			driver.set(new FirefoxDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("EdgeDriver Instance is created");

		} else {

			throw new IllegalArgumentException("Browser Not Supported:" + browser);
		}
	}

	/*
	 * Configure browser settings such as Implicit wait, maximize the browserand
	 * navigateURL
	 */
	private void configureBrowser() {

		// Implicit Wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

		// maximize the driver
		getDriver().manage().window().maximize();

		// Navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {

			logger.info("Failed to Navigate to the URL:" + e.getMessage());

		}

	}

	@BeforeMethod
	public synchronized void setUP() throws IOException {

		logger.info("Settings up WebDriver for:" + this.getClass().getSimpleName());

		launchBrowser();
		configureBrowser();
		staticWait(2);

		logger.info("WebDriver Initialized and Browser Maximized");
		logger.trace("Trace message");
		logger.error("error message");
		logger.debug("This is debug message");
		logger.fatal("This is fatal message");
		logger.warn("This is warn message");

		// Initialize the actionDriver only once

		/*
		 * if(actionDriver==null) {
		 * 
		 * actionDriver = new ActionDriver(driver);
		 * 
		 * logger.info("ActionDriver instance is created. "+Thread.currentThread().getId
		 * ());
		 * 
		 * }
		 */

		// Initialize ActionDriver for the current Thread

		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread:" + Thread.currentThread().getId());

	}

	@AfterMethod
	public void tearDown() {
		if (getDriver() != null) {

			try {
				getDriver().quit();
			} catch (Exception e) {

				logger.info("Unable to quit the Driver:" + e.getMessage());
			}
		}
		logger.info("WebDriver Instance is closed");
		driver.remove();
		driver.remove();
		// driver=null;
		// actionDriver=null;

		// ExtentManager.endTest(); --This has been Implemented in TestListener
	}

	// get WebDriver Instance
	public static WebDriver getDriver() {

		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// get ActionDriver Instance
	public static ActionDriver getactionDriver() {

		if (actionDriver.get() == null) {
			logger.info("actionDriver is not initialized");
			throw new IllegalStateException("actionDriver is not initialized");
		}
		return actionDriver.get();
	}

	public void setDriver(ThreadLocal<WebDriver> driver) {

		this.driver = driver;

	}

	// static wait for pause
	public void staticWait(int seconds) {

		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	public static Properties getProp() {
		return prop;

	}

}
