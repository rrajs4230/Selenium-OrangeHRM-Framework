package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
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

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
    private static final ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

    private static Properties prop;
    public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

    public static WebDriver getDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver == null) {
            throw new IllegalStateException("WebDriver is not initialized.");
        }
        return webDriver;
    }

    public static ActionDriver getActionDriver() {
        ActionDriver action = actionDriver.get();
        if (action == null) {
            throw new IllegalStateException("ActionDriver is not initialized.");
        }
        return action;
    }

    public SoftAssert getSoftAssert() {
        return softAssert.get();
    }

    public static Properties getProp() {
        return prop;
    }

    @BeforeSuite
    public void loadConfig() {
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(fis);
            logger.info("config.properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException("Configuration load failure", e);
        }
    }

    @BeforeMethod
    public synchronized void setUp() {
        logger.info("Initializing WebDriver for: " + this.getClass().getSimpleName());
        launchBrowser();
        configureBrowser();
        staticWait(2);
        actionDriver.set(new ActionDriver(getDriver()));
        logger.info("WebDriver and ActionDriver are ready for thread: " + Thread.currentThread().getId());
    }

    @AfterMethod
    public void tearDown() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
                logger.info("WebDriver quit successfully.");
            } catch (Exception e) {
                logger.warn("Exception while quitting WebDriver: " + e.getMessage());
            }
        }
        driver.remove();
        actionDriver.remove();
        softAssert.remove();
    }

    private synchronized void launchBrowser() {
        String browser = prop.getProperty("browser", "chrome").toLowerCase();

        switch (browser) {
            case "chrome":
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless=new", "--disable-gpu", "--disable-notifications", "--disable-dev-shm-usage", "--no-sandbox");
                String userDataDir = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + Thread.currentThread().getId();
                options.addArguments("--user-data-dir=" + userDataDir);
                driver.set(new ChromeDriver(options));
                logger.info("ChromeDriver initialized with custom options.");
                break;

            case "firefox":
                driver.set(new FirefoxDriver());
                logger.info("FirefoxDriver initialized.");
                break;

            case "edge":
                driver.set(new EdgeDriver());
                logger.info("EdgeDriver initialized.");
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browser);
        }

        ExtentManager.registerDriver(getDriver());
    }

    private void configureBrowser() {
        try {
            int implicitWait = Integer.parseInt(prop.getProperty("implicitWait", "10"));
            WebDriver webDriver = getDriver();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            webDriver.manage().window().maximize();
            webDriver.get(prop.getProperty("url"));
            logger.info("Navigated to URL: " + prop.getProperty("url"));
        } catch (Exception e) {
            logger.error("Error during browser configuration: " + e.getMessage());
        }
    }

    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }
}
