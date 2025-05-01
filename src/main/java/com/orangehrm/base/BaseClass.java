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
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src//main//resources//config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");
	}

	private synchronized void launchBrowser() {
		String browser = prop.getProperty("browser");
		String os = System.getProperty("os.name").toLowerCase();

		if (browser.equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless=new"); // More stable than --headless
			options.addArguments("--disable-gpu");
			options.addArguments("--disable-notifications");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--no-sandbox");

			// Assign a unique profile per thread to avoid conflicts
			String userDataDir = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + Thread.currentThread().getId();
			options.addArguments("--user-data-dir=" + userDataDir);

			driver.set(new ChromeDriver(options));
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver Instance is created");

		} else if (browser.equalsIgnoreCase("firefox")) {
			driver.set(new FirefoxDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("FirefoxDriver Instance is created");

		} else if (browser.equalsIgnoreCase("edge")) {
			driver.set(new EdgeDriver());
			ExtentManager.registerDriver(getDriver());
			logger.info("EdgeDriver Instance is created");

		} else {
			throw new IllegalArgumentException("Browser Not Supported: " + browser);
		}
	}

	private void configureBrowser() {
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		getDriver().manage().window().maximize();

		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			logger.info("Failed to Navigate to the URL: " + e.getMessage());
		}
	}

	@BeforeMethod
	public synchronized void setUP() throws IOException {
		logger.info("Settings up WebDriver for: " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);

		logger.info("WebDriver Initialized and Browser Maximized");
		logger.debug("This is debug message");

		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());
	}

	@AfterMethod
	public void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				logger.info("Unable to quit the Driver: " + e.getMessage());
			}
		}
		logger.info("WebDriver Instance is closed");
		driver.remove();
		actionDriver.remove();
	}

	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

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

	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	public static Properties getProp() {
		return prop;
	}
}
