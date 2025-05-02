package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
    private static final ThreadLocal<ChromeDriverService> chromeService = new ThreadLocal<>();

    protected static Properties prop;
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
                cleanupChromeProfile();
            } catch (Exception e) {
                logger.warn("Exception while quitting WebDriver: " + e.getMessage());
            }
        }
        
        ChromeDriverService service = chromeService.get();
        if (service != null && service.isRunning()) {
            service.stop();
            logger.info("ChromeDriverService stopped successfully.");
        }
        
        driver.remove();
        actionDriver.remove();
        softAssert.remove();
        chromeService.remove();
    }

    private synchronized void launchBrowser() {
        String browser = prop.getProperty("browser", "chrome").toLowerCase();

        switch (browser) {
            case "chrome":
                ChromeOptions options = new ChromeOptions();
                options.addArguments(
                    "--headless=new",
                    "--disable-gpu",
                    "--disable-notifications",

                    "--disable-dev-shm-usage",
                    "--no-sandbox",
                    "--remote-allow-origins=*"
                );
                
                // Only use user-data-dir if explicitly configured
                if (prop.getProperty("use.chrome.profile", "false").equalsIgnoreCase("true")) {
                    String userDataDir = createUniqueChromeProfileDir();
                    options.addArguments("--user-data-dir=" + userDataDir);
                    logger.info("Using Chrome profile at: " + userDataDir);
                }
                

                
               
                driver.set(new ChromeDriver(options));
                logger.info("ChromeDriver initialized successfully with headless mode.");
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver.set(new FirefoxDriver(firefoxOptions));
                logger.info("FirefoxDriver initialized.");
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless=new");
                driver.set(new EdgeDriver(edgeOptions));
                logger.info("EdgeDriver initialized.");
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browser);
        }

        ExtentManager.registerDriver(getDriver());
    }

    private String createUniqueChromeProfileDir() {
        try {
            String baseDir = System.getProperty("java.io.tmpdir");
            Path profileDir = Paths.get(baseDir, "chrome-profile-" + Thread.currentThread().getId());
            Files.createDirectories(profileDir);
            return profileDir.toString();
        } catch (IOException e) {
            logger.error("Failed to create Chrome profile directory: " + e.getMessage());
            throw new RuntimeException("Failed to create Chrome profile directory", e);
        }
    }

    private void cleanupChromeProfile() {
        if (prop.getProperty("use.chrome.profile", "false").equalsIgnoreCase("true")) {
            try {
                String profileDir = System.getProperty("java.io.tmpdir") + "chrome-profile-" + Thread.currentThread().getId();
                Path path = Paths.get(profileDir);
                if (Files.exists(path)) {
                    Files.walk(path)
                         .sorted(java.util.Comparator.reverseOrder())
                         .forEach(p -> {
                             try {
                                 Files.deleteIfExists(p);
                             } catch (IOException e) {
                                 logger.warn("Failed to delete profile file: " + p.toString());
                             }
                         });
                    logger.info("Cleaned up Chrome profile directory: " + profileDir);
                }
            } catch (Exception e) {
                logger.warn("Error cleaning up Chrome profile: " + e.getMessage());
            }
        }
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
            throw new RuntimeException("Browser configuration failed", e);
        }
    }

    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }
}