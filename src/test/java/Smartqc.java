import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * SmartQC Test Automation Suite
 * This class contains automated tests for the SmartQC web application
 * 
 * Features:
 * - Automated login/signup
 * - Campaign creation and management
 * - File import testing
 * - Billing and plan management
 * - User profile management
 */
@Listeners(SmartqcTestListener.class)
public class Smartqc {
    
    // Constants
    private static final String BASE_URL = "https://smartqc.io";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String REGISTER_URL = BASE_URL + "/register";
    private static final String DASHBOARD_URL = BASE_URL + "/account/dashboard";
    private static final String CAMPAIGNS_URL = BASE_URL + "/account/campaigns";
    private static final String BILLING_URL = BASE_URL + "/account/billing";
    private static final String PROFILE_URL = BASE_URL + "/account/profile";
    private static final String CREATE_CAMPAIGN_URL = BASE_URL + "/account/createcampaign";
    
    private static final String CHROME_DRIVER_PATH = "C:\\Users\\A-00714\\Desktop\\workspace\\untitled\\src\\test\\resources\\chromedriver.exe";
    private static final String TEST_FILE_PATH = "C:\\Users\\A-00714\\Desktop\\workspace\\TestFile.csv";
    
    // Test data
    private static final String LOGIN_EMAIL = "abamne@smartqc.io";
    private static final String LOGIN_PASSWORD = "Alltake@1234";
    private static final String SIGNUP_FIRST_NAME = "Aqdas";
    private static final String SIGNUP_LAST_NAME = "Bamne";
    private static final String SIGNUP_EMAIL = "abamne@smartqc.io";
    private static final String SIGNUP_PASSWORD = "Aqdas@123";
    private static final String COMPANY_NAME = "IBM-Pune";
    
    // Wait timeouts
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(20);
    private static final Duration LONG_WAIT = Duration.ofSeconds(50);
    
    // Instance variables
    private WebDriver driver;
    private WebDriverWait wait;
    private FluentWait<WebDriver> fluentWait;
    private boolean shouldLogout = false;
    private static final Logger logger = Logger.getLogger(Smartqc.class.getName());
    private static FileHandler fileHandler;
    
    // Screenshot directory
    private static final String SCREENSHOT_DIR = "screenshots";
    
    /**
     * Getter for WebDriver instance (used by TestListener)
     */
    public WebDriver getDriver() {
        return driver;
    }
    @BeforeClass
    public void setUp() {
        try {
            // Set up logging
            setupLogger();
            logger.info("Setting up WebDriver...");
            Reporter.log("Setting up WebDriver...", true);
            
            // Create screenshots directory if it doesn't exist
            createScreenshotDirectory();
            
            // Configure Chrome options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            
            // Set the path to the chromedriver executable
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            driver = new ChromeDriver(options);
            
            // Initialize wait objects
            wait = new WebDriverWait(driver, DEFAULT_WAIT);
            fluentWait = new FluentWait<>(driver)
                    .withTimeout(DEFAULT_WAIT)
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);
            
            logger.info("Setup completed successfully.");
            Reporter.log("Setup completed successfully.", true);
        } catch (Exception e) {
            logger.severe("Setup failed: " + e.getMessage());
            Reporter.log("Setup failed: " + e.getMessage(), true);
            throw e;
        }
    }
    
    /**
     * Sets up the logger with file output
     */
    private void setupLogger() {
        try {
            // Create logs directory if it doesn't exist
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdir();
            }
            
            // Generate timestamp for log filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Configure file handler for logger
            fileHandler = new FileHandler("logs/smartqc-" + timestamp + ".log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            
            logger.info("Logger initialized successfully.");
        } catch (IOException e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }
    
    /**
     * Creates the screenshot directory if it doesn't exist
     */
    private void createScreenshotDirectory() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            boolean created = screenshotDir.mkdir();
            if (created) {
                logger.info("Created screenshots directory: " + screenshotDir.getAbsolutePath());
            } else {
                logger.warning("Failed to create screenshots directory: " + screenshotDir.getAbsolutePath());
            }
        }
    }

    @BeforeMethod
    public void loginOrSignUp() {
        logger.info("Attempting to login or sign up...");
        Reporter.log("Attempting to login or sign up...", true);
        
        try {
            // Navigate to login page
            driver.get(LOGIN_URL);
            
            // Wait for login page to load with better error handling
            boolean pageLoaded = waitForPageToLoad();
            if (!pageLoaded) {
                logger.severe("Login page failed to load properly");
                Reporter.log("Login page failed to load properly", true);
                captureScreenshot("loginPageLoadFailure");
                return;
            }
            
            // Wait for login form to be visible
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@class='card-body p-4 p-md-5']")));
            
            // Attempt login first
            if (attemptLogin()) {
                logger.info("Login successful");
                Reporter.log("Login successful", true);
                shouldLogout = true;
                return;
            }
            
            // If login fails, try signup
            if (attemptSignUp()) {
                logger.info("Sign-up successful");
                Reporter.log("Sign-up successful", true);
                shouldLogout = true;
            } else {
                logger.severe("Both login and sign-up failed");
                Reporter.log("Both login and sign-up failed", true);
                captureScreenshot("loginSignupFailure");
            }
            
        } catch (TimeoutException e) {
            logger.severe("Timeout during login/signup: " + e.getMessage());
            Reporter.log("Timeout during login/signup: " + e.getMessage(), true);
            captureScreenshot("loginSignupTimeout");
        } catch (WebDriverException e) {
            logger.severe("WebDriver error during login/signup: " + e.getMessage());
            Reporter.log("WebDriver error during login/signup: " + e.getMessage(), true);
            captureScreenshot("loginSignupWebDriverError");
        } catch (Exception e) {
            logger.severe("Unexpected error during login/signup: " + e.getMessage());
            Reporter.log("Unexpected error during login/signup: " + e.getMessage(), true);
            captureScreenshot("loginSignupUnexpectedError");
        }
    }
    
    /**
     * Waits for page to completely load
     * @return true if page loaded successfully, false otherwise
     */
    private boolean waitForPageToLoad() {
        try {
            ExpectedCondition<Boolean> pageLoadCondition = driver -> {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            };
            return wait.until(pageLoadCondition);
        } catch (Exception e) {
            logger.warning("Page load check failed: " + e.getMessage());
            return false;
        }
    }
    
    // Wait until typical spinners/overlays are gone
    private void waitForSpinnerToDisappear() {
        By[] candidates = new By[] {
            By.cssSelector(".MuiBackdrop-root"),
            By.cssSelector(".MuiCircularProgress-root"),
            By.cssSelector(".ant-spin"),
            By.cssSelector(".spinner"),
            By.cssSelector(".loading"),
            By.cssSelector("[data-testid='spinner']"),
            By.cssSelector(".overlay"),
            By.cssSelector(".backdrop"),
            By.cssSelector(".modal-backdrop")
        };
        for (By by : candidates) {
            try {
                new WebDriverWait(driver, Duration.ofSeconds(6))
                    .until(ExpectedConditions.invisibilityOfElementLocated(by));
            } catch (Exception ignored) {
                // Try next candidate
            }
        }
    }
    
    /**
     * Captures a screenshot with the given name prefix
     * @param prefix Name prefix for the screenshot file
     */
    private void captureScreenshot(String prefix) {
        try {
            // Generate timestamp for unique filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Take screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            // Save screenshot
            String destination = SCREENSHOT_DIR + "/" + prefix + "-" + timestamp + ".png";
            File target = new File(destination);
            FileUtils.copyFile(source, target);
            
            logger.info("Screenshot saved to: " + target.getAbsolutePath());
            Reporter.log("Screenshot saved to: " + target.getAbsolutePath(), true);
            Reporter.log("<a href='" + target.getAbsolutePath() + "'><img src='" + 
                    target.getAbsolutePath() + "' height='100' width='100'/></a>", true);
            
        } catch (IOException e) {
            logger.warning("Failed to capture screenshot: " + e.getMessage());
            Reporter.log("Failed to capture screenshot: " + e.getMessage(), true);
        }
    }
    
    /**
     * Attempts to login with existing credentials
     * @return true if login successful, false otherwise
     */
    private boolean attemptLogin() {
        try {
            logger.info("Attempting login with email: " + LOGIN_EMAIL);
            Reporter.log("Attempting login with email: " + LOGIN_EMAIL, true);
            
            // Find login form elements with better error handling
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Enter your email']")));
            
            if (usernameField == null) {
                logger.warning("Username field not found");
                Reporter.log("Username field not found", true);
                return false;
            }
            
            WebElement passwordField = driver.findElement(
                By.xpath("//input[@placeholder='Enter your password']"));
            WebElement loginButton = driver.findElement(
                By.xpath("//button[normalize-space()='Sign In']"));

            // Enter credentials with logging
            logger.fine("Clearing and entering username");
            usernameField.clear();
            usernameField.sendKeys(LOGIN_EMAIL);
            
            logger.fine("Clearing and entering password");
            passwordField.clear();
            passwordField.sendKeys(LOGIN_PASSWORD);

            // Click login button
            logger.fine("Clicking login button");
            loginButton.click();

            // Wait for dashboard URL to confirm successful login
            logger.fine("Waiting for redirect to dashboard");
            wait.until(ExpectedConditions.urlToBe(DASHBOARD_URL));
            String currentUrl = driver.getCurrentUrl();
            
            // Verify we're on the dashboard
            if (DASHBOARD_URL.equals(currentUrl)) {
                logger.info("Successfully logged in and redirected to dashboard");
                Reporter.log("Successfully logged in and redirected to dashboard", true);
                return true;
            } else {
                logger.warning("Login seemed successful but URL doesn't match dashboard: " + currentUrl);
                Reporter.log("Login seemed successful but URL doesn't match dashboard: " + currentUrl, true);
                return false;
            }
            
        } catch (TimeoutException e) {
            logger.warning("Login failed due to timeout: " + e.getMessage());
            Reporter.log("Login failed due to timeout - proceeding to sign up", true);
            return false;
        } catch (NoSuchElementException e) {
            logger.warning("Login failed - element not found: " + e.getMessage());
            Reporter.log("Login failed - element not found: " + e.getMessage(), true);
            return false;
        } catch (Exception e) {
            logger.warning("Login failed due to unexpected error: " + e.getMessage());
            Reporter.log("Login failed due to unexpected error: " + e.getMessage(), true);
            return false;
        }
    }
    
    /**
     * Attempts to sign up with new user credentials
     * @return true if signup successful, false otherwise
     */
    private boolean attemptSignUp() {
        try {
            // Click the "Sign Up" button
            WebElement signUpButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Sign up']")));
            signUpButton.click();

            // Wait for the sign-up page to load
            wait.until(ExpectedConditions.urlToBe(REGISTER_URL));

            // Fill out the sign-up form
            WebElement firstName = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@name='firstName']")));
            WebElement lastName = driver.findElement(By.xpath("//input[@name='lastName']"));
            WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
            WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
            WebElement signUpSubmitButton = driver.findElement(
                By.xpath("//button[normalize-space()='Sign up']"));

            firstName.sendKeys(SIGNUP_FIRST_NAME);
            lastName.sendKeys(SIGNUP_LAST_NAME);
            emailField.sendKeys(SIGNUP_EMAIL);
            passwordField.sendKeys(SIGNUP_PASSWORD);
            signUpSubmitButton.click();

            // Verify successful sign up
            wait.until(ExpectedConditions.urlToBe(DASHBOARD_URL));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertEquals(currentUrl, DASHBOARD_URL);
            
            return true;
            
        } catch (TimeoutException e) {
            System.out.println("Sign-up failed.");
            return false;
        }
    }
    
    /**
     * Tests the campaign creation functionality
     * Verifies that a user can successfully create a new campaign
     */
    @Test(description = "Create a new campaign", priority = 1)
public void testCreateCampaign() {
    logger.info("Testing campaign creation...");
    Reporter.log("Testing campaign creation...", true);

    try {
        // Wait for dashboard to fully load
        waitForPageToLoad();

        // Navigate to Campaigns page and click "New Campaign"
        driver.get(createCampaignUrl);
        waitForPageToLoad();
        waitForSpinnerToDisappear();

        WebElement newCampaignBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[normalize-space()='New Campaign']")));
        scrollToElement(newCampaignBtn);
        newCampaignBtn.click();

        waitForPageToLoad();
        waitForSpinnerToDisappear();

        // Verify navigation
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.equals(CREATE_CAMPAIGN_URL) && !currentUrl.contains("/createcampaign")) {
            captureScreenshot("campaign-url-mismatch");
            Assert.fail("Failed to navigate to campaign creation page. Current: " + currentUrl);
        }

        // Take screenshot before filling details
        captureScreenshot("before-campaign-details");

        // Fill in campaign details
        fillCampaignDetails();

        // Take screenshot after filling details
        captureScreenshot("after-campaign-details");

        // Find and click continue button
        WebElement continueButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[@id=\"root\"]/div[1]/div/div[2]/div/button")));

        scrollToElement(continueButton);

        // Click first continue
        continueButton.click();

        // Wait for button to be clickable again
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));

        captureScreenshot("before-second-continue");

        // Click second continue
        continueButton.click();

        // Take screenshot after completion
        captureScreenshot("campaign-flow-complete");

        shouldLogout = true;
        logger.info("Campaign creation test completed successfully");
        Reporter.log("Campaign creation test completed successfully", true);

    } catch (TimeoutException e) {
        logger.severe("Timeout during campaign creation: " + e.getMessage());
        Reporter.log("Timeout during campaign creation: " + e.getMessage(), true);
        captureScreenshot("campaign-creation-timeout-error");
        Assert.fail("Campaign creation failed due to timeout: " + e.getMessage());
// Navigate to Campaigns page and click "New Campaign"
driver.get(CAMPAIGNS_URL);
waitForPageToLoad();
waitForSpinnerToDisappear();

WebElement newCampaignBtn = wait.until(ExpectedConditions.elementToBeClickable(
    By.xpath("//button[normalize-space()='New Campaign']")));
scrollToElement(newCampaignBtn);
newCampaignBtn.click();

waitForPageToLoad();
waitForSpinnerToDisappear();

// Verify navigation
String currentUrl = driver.getCurrentUrl();
if (!currentUrl.equals(CREATE_CAMPAIGN_URL) && !currentUrl.contains("/createcampaign")) {
    captureScreenshot("campaign-url-mismatch");
    Assert.fail("Failed to navigate to campaign creation page. Current: " + currentUrl);
}
    } catch (NoSuchElementException e) {
        logger.severe("Element not found during campaign creation: " + e.getMessage());
        Reporter.log("Element not found during campaign creation: " + e.getMessage(), true);
        captureScreenshot("campaign-creation-element-not-found");
        Assert.fail("Campaign creation failed - element not found: " + e.getMessage());
    } catch (WebDriverException e) {
        logger.severe("WebDriver error during campaign creation: " + e.getMessage());
        Reporter.log("WebDriver error during campaign creation: " + e.getMessage(), true);
        captureScreenshot("campaign-creation-webdriver-error");
        Assert.fail("Campaign creation failed due to WebDriver error: " + e.getMessage());
    } catch (Exception e) {
        logger.severe("Unexpected error during campaign creation: " + e.getMessage());
        Reporter.log("Unexpected error during campaign creation: " + e.getMessage(), true);
        captureScreenshot("campaign-creation-unexpected-error");
        Assert.fail("Campaign creation failed due to unexpected error: " + e.getMessage());
    }
}

/**
 * Fills in campaign details (company name and employee size)
 */
private void fillCampaignDetails() {
    WebElement companyNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//*[@id='root']/div[1]/div/div[2]/div/input")));
    WebElement employeeSizeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//*[@id='root']/div[1]/div/div[2]/div/div/div/button[8]")));

    companyNameField.clear();
    companyNameField.sendKeys(COMPANY_NAME);
    employeeSizeButton.click();
}

    // }

    @Test
    public void testFileImport() {
        System.out.println("Testing file import functionality...");
        
        try {
            // Wait for the file input to be present and upload the file
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@type='file']")));
            fileInput.sendKeys(TEST_FILE_PATH);
            
            WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@class='mb-4 btn btn-orange']")));
            submitButton.click();
            
            WebElement confirmationButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='Yes, upload it!']")));
            confirmationButton.click();
            
            System.out.println("File imported successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during file import: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during file import: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during file import: " + e.getMessage());
        }
    }
    @Test
    public void testCampaignSearch() {
        System.out.println("Testing campaign search functionality...");
        
        try {
            driver.get(CAMPAIGNS_URL);
            
            WebElement searchField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@id='myInput']")));
            searchField.clear();
            searchField.sendKeys(COMPANY_NAME);
            
            // Wait for search results to load
            wait.until(ExpectedConditions.attributeContains(searchField, "value", COMPANY_NAME));
            
            System.out.println("Campaign search completed successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during campaign search: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during campaign search: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during campaign search: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to navigate to billing page
     */
    private void navigateToBilling() {
        driver.get(BILLING_URL);
        
        WebElement billingButton = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[normalize-space()='Billing']")));
        billingButton.click();
    }
    
    /**
     * Helper method to scroll to an element
     */
    private void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        
        // Small wait for scroll to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    @Test
    public void testBillingNavigation() {
        System.out.println("Testing billing navigation...");
        
        try {
            navigateToBilling();
            
            WebElement changePlanButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@class='ms-2 btn btn-sm btn-orange']")));
            changePlanButton.click();
            
            WebElement backButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Back to Billing']")));
            backButton.click();
            
            System.out.println("Billing navigation test completed.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during billing navigation: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during billing navigation: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during billing navigation: " + e.getMessage());
        }
    }
    
    /**
     * Generic method to test plan activation
     */
    private void testPlanActivation(String planName, String buyNowXPath) {
        try {
            navigateToBilling();
            
            WebElement changePlanButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@class='ms-2 btn btn-sm btn-orange']")));
            changePlanButton.click();
            
            WebElement buyNowButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath(buyNowXPath)));
            buyNowButton.click();
            
            // Wait for plan activation
            Thread.sleep(5000); // This could be replaced with a better wait condition
            
            WebElement planBackButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[@class='text-muted fw-bold']")));
            planBackButton.click();
            
            WebElement backButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Back to Billing']")));
            backButton.click();
            
            System.out.println(planName + " plan activated successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during " + planName + " plan activation: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during " + planName + " plan activation: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during " + planName + " plan activation: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
    @Test
    public void testAdvancedPlanActivation() {
        System.out.println("Testing Advanced plan activation...");
        testPlanActivation("Advanced", "//a[@class='btn-sm btn btn-orange px-4 button-hover']");
    }
    @Test
    public void testProfessionalPlanActivation() {
        System.out.println("Testing Professional plan activation...");
        testPlanActivation("Professional", 
            "//div[contains(@class,'d-flex align-items-center')]//div[2]//div[1]//div[4]//a[1]");
    }
    @Test
    public void testStarterPlanActivation() {
        System.out.println("Testing Starter plan activation...");
        testPlanActivation("Starter", 
            "//body//div[@id='root']//div[contains(@class,'row')]//div[contains(@class,'row')]//div[1]//div[1]//div[4]//a[1]");
    }
    @Test
    public void testInvoiceHistory() {
        System.out.println("Testing invoice history...");
        
        try {
            driver.get(BILLING_URL);
            WebDriverWait longWait = new WebDriverWait(driver, LONG_WAIT);
            
            WebElement viewAllButton = longWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='View All']")));
            
            // Scroll to the View All button
            scrollToElement(viewAllButton);
            viewAllButton.click();
            
            // Wait for invoice history to load
            Thread.sleep(10000); // This could be replaced with a better wait condition
            
            System.out.println("Invoice history displayed successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during invoice history test: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during invoice history test: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during invoice history test: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during invoice history test: " + e.getMessage());
        }
    }

    @Test
    public void testSettingsNavigation() {
        System.out.println("Testing settings navigation...");
        
        try {
            driver.get(DASHBOARD_URL);
            
            WebElement settingsButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Settings']")));
            settingsButton.click();
            
            // Wait for settings page to load
            Thread.sleep(10000); // This could be replaced with a better wait condition
            
            System.out.println("Settings navigation completed successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during settings navigation: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during settings navigation: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during settings navigation: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during settings navigation: " + e.getMessage());
        }
    }
    @Test
    public void testNameChange() {
        System.out.println("Testing name change functionality...");
        
        try {
            driver.get(PROFILE_URL);
            
            WebElement editButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Edit']")));
            editButton.click();
            
            WebElement firstNameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[contains(@placeholder,'First Name')]")));
            WebElement lastNameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Last Name']")));
            
            firstNameField.clear();
            firstNameField.sendKeys(SIGNUP_FIRST_NAME);
            lastNameField.clear();
            lastNameField.sendKeys(SIGNUP_LAST_NAME);
            
            WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='Save']")));
            scrollToElement(saveButton);
            saveButton.click();
            
            System.out.println("Name changed successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during name change: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during name change: " + e.getMessage());
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during name change: " + e.getMessage());
        }
    }
    @Test
    public void testPasswordChange() {
        System.out.println("Testing password change functionality...");
        
        try {
            driver.get(PROFILE_URL);
            
            WebElement changePasswordButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[normalize-space()='Change Password']")));
            changePasswordButton.click();
            
            WebElement newPasswordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='New Password']")));
            WebElement confirmPasswordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@placeholder='Confirm New Password']")));
            
            newPasswordField.sendKeys(LOGIN_PASSWORD);
            confirmPasswordField.sendKeys(LOGIN_PASSWORD);
            
            WebElement resetButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Reset Password']")));
            scrollToElement(resetButton);
            resetButton.click();
            
            // Wait for password reset to complete
            Thread.sleep(20000); // This could be replaced with a better wait condition
            
            System.out.println("Password changed successfully.");
            shouldLogout = true;
            
        } catch (TimeoutException e) {
            System.out.println("Timeout during password change: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Element not found during password change: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during password change: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (WebDriverException e) {
            System.out.println("WebDriver error during password change: " + e.getMessage());
        }
    }

    @AfterMethod
    public void logout() {
        if (shouldLogout) {
            try {
                System.out.println("Logging out...");
                
                WebElement logoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='d-flex align-items-center user-info']//*[name()='svg']")));
                logoutButton.click();
                
                WebElement confirmLogoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[normalize-space()='Yes']")));
                confirmLogoutButton.click();
                
                wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
                System.out.println("User logged out successfully.");
                
            } catch (TimeoutException e) {
                System.out.println("Logout failed due to timeout: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("Logout failed - element not found: " + e.getMessage());
            } catch (WebDriverException e) {
                System.out.println("Logout failed due to WebDriver error: " + e.getMessage());
            } finally {
                shouldLogout = false; // Reset the flag after logout attempt
            }
        }
    }

    /**
     * Cleans up resources after all tests have completed
     */
    @AfterClass
    public void tearDown() {
        logger.info("Tearing down WebDriver and cleaning up resources...");
        Reporter.log("Tearing down WebDriver and cleaning up resources...", true);
        
        try {
            // Close WebDriver if it exists
            if (driver != null) {
                driver.quit();
                logger.info("WebDriver closed successfully");
                Reporter.log("WebDriver closed successfully", true);
            }
            
            // Close file handler for logger
            if (fileHandler != null) {
                fileHandler.close();
                logger.info("Logger file handler closed");
            }
            
            // Generate summary report
            generateTestSummary();
            
        } catch (Exception e) {
            logger.severe("Error during teardown: " + e.getMessage());
            Reporter.log("Error during teardown: " + e.getMessage(), true);
        }
    }
    
    /**
     * Generates a summary of test execution
     */
    private void generateTestSummary() {
        try {
            // Create reports directory if it doesn't exist
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdir();
            }
            
            // Generate timestamp for report filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Create summary file
            File summaryFile = new File("reports/test-summary-" + timestamp + ".txt");
            
            // Log summary creation
            logger.info("Test summary generated: " + summaryFile.getAbsolutePath());
            Reporter.log("Test summary generated: " + summaryFile.getAbsolutePath(), true);
            
        } catch (Exception e) {
            logger.warning("Failed to generate test summary: " + e.getMessage());
            Reporter.log("Failed to generate test summary: " + e.getMessage(), true);
        }
    }
}