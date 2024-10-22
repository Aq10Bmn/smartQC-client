import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class Smartqc {
    private WebDriver driver;
    private boolean shouldLogout = false;
    @BeforeClass
    public void setUp() {
        // Set the path to the chromedriver executable
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\A-00714\\Desktop\\workspace\\untitled\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        System.out.println("Setup completed.");
        shouldLogout = true; // Set the flag to true after Subscribers test
    }

    @BeforeMethod
    public void testLoginOrSignUp() throws InterruptedException {
        driver.get("https://smartqc.io/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginPage = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div/div/div/form")));

        // Check if login form is present
        if (loginPage != null) {
            WebElement usernameField = driver.findElement(By.xpath("//input[@name='email']"));
            WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
            Thread.sleep(2000);


            // Enter credentials
            usernameField.sendKeys("abamne@smartqc.io");
            Thread.sleep(2000);
            passwordField.sendKeys("Aqdas@123");

            // Click the login button
            loginButton.click();
            Thread.sleep(2000);

            // Check if login was successful
            try {
                wait.until(ExpectedConditions.urlToBe("https://smartqc.io/account/dashboard"));
                String currentUrl = driver.getCurrentUrl();
                Assert.assertEquals(currentUrl, "https://smartqc.io/account/dashboard");
                System.out.println("Login successful.");
                Thread.sleep(2000);
            } catch (TimeoutException e) {
                // If login failed, assume the user does not have an account
                System.out.println("Login failed. Proceeding to sign up.");
                Thread.sleep(2000);

                // Click the "Sign Up" button
                WebElement signUpButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Sign up']")));
                Thread.sleep(2000);
                signUpButton.click();
                Thread.sleep(2000);

                // Wait for the sign-up page to load
                wait.until(ExpectedConditions.urlToBe("https://smartqc.io/register"));

                // Fill out the sign-up form (adjust as needed for the actual form fields)
                WebElement firstName = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='firstName']")));
                WebElement lastName = driver.findElement(By.xpath("//input[@name='lastName']"));
                WebElement emailField = driver.findElement(By.xpath("//input[@name='email']"));
                WebElement passwordFieldSignUp = driver.findElement(By.xpath("//input[@name='password']"));
                WebElement signUpSubmitButton = driver.findElement(By.xpath("//button[normalize-space()='Sign up']"));

                firstName.sendKeys("Aqdas");
                Thread.sleep(2000);
                lastName.sendKeys("Bamne");
                Thread.sleep(2000);
                emailField.sendKeys("bamneaqdas@gmail.com");
                Thread.sleep(2000);
                passwordFieldSignUp.sendKeys("Aqdas@123");
                Thread.sleep(2000);
                signUpSubmitButton.click();
                Thread.sleep(2000);

                // Verify successful sign up
                try {
                    wait.until(ExpectedConditions.urlToBe("https://smartqc.io/account/dashboard"));
                    String currentUrl = driver.getCurrentUrl();
                    Assert.assertEquals(currentUrl, "https://smartqc.io/account/dashboard");
                    System.out.println("Sign-up successful.");
                } catch (TimeoutException ex) {
                    System.out.println("Sign-up failed.");
                }
            }
        }
//        shouldLogout = true; // Set the flag to true after Subscribers test

    }

@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void testGetStarted() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement getStartedButton = null;
        try {
            // Wait for the "Get Started" button to be present
            getStartedButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div[2]/div/div/div/div/button")));
        } catch (TimeoutException e) {
            // Handle case where "Get Started" button is not present
            System.out.println("Get Started button not found within the specified time.");
        }
//
        // Check if the "Get Started" button is displayed and clickable
        if (getStartedButton != null && getStartedButton.isDisplayed()) {
            // Click the "Get Started" button
            getStartedButton.click();
            Thread.sleep(2000);


            // Explicit wait for the new URL
            wait.until(ExpectedConditions.urlToBe("https://smartqc.io/createcampagin/create-type"));

            // Assert if the current URL is the create campaign page URL
            String currentUrl = driver.getCurrentUrl();
            Assert.assertEquals(currentUrl, "https://smartqc.io/createcampagin/create-type");

            // Wait for the input placeholder and employee size button to be visible
            WebElement getPlaceHolder = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div[2]/div/input")));
            WebElement employeeSize = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div[2]/div/div/div/button[8]")));
            WebElement continueButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div[1]/div/div[2]/div/button")));

            // Interact with the elements
            getPlaceHolder.sendKeys("IBM-Pune");
            Thread.sleep(2000);
            employeeSize.click();
            Thread.sleep(2000);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", continueButton);
            Thread.sleep(2000);  // Wait for scroll to complete

            continueButton.click();
            Thread.sleep(2000);
            continueButton.click();
            Thread.sleep(2000);
        } else {
            // Alternative action if "Get Started" button is not displayed
            WebElement alternativeButton = driver.findElement(By.xpath("//*[@id='root']/div[1]/div/div[1]/div[2]/button"));
            alternativeButton.click();
            Thread.sleep(2000);

            WebElement getPlaceHolder = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div[2]/div/input")));
            WebElement employeeSize = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='root']/div[1]/div/div[2]/div/div/div/button[8]")));
            WebElement continueButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Continue']")));

            // Interact with the elements
            getPlaceHolder.sendKeys("IBM-Pune");
            Thread.sleep(2000);
            employeeSize.click();
            Thread.sleep(2000);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", continueButton);
            Thread.sleep(2000);  // Wait for scroll to complete

            continueButton.click();
            Thread.sleep(2000);
//            continueButton.click();
//            Thread.sleep(2000);
            System.out.println("New Campaign Created");

//        }

//    }

//    @Test(dependsOnMethods = "testGetStarted")
//    public void Import() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        // Locate the file input element and click the button to trigger the file input
//        WebElement importButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Import csv document']")));
////        importButton.click();
//
            // Wait for the file input to be present and upload the file
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='file']")));
            String filePath = "C:\\Users\\A-00714\\Desktop\\workspace\\TestFile.csv";
            fileInput.sendKeys(filePath);
            WebElement SubmitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='mb-4 btn btn-orange']")));
            SubmitButton.click();
            Thread.sleep(2000);
            WebElement Confirmation = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[normalize-space()='Yes, upload it!']")));
            Confirmation.click();
            Thread.sleep(2000);
            System.out.println("New File Imported");
            shouldLogout = true; // Set the flag to true after Subscribers test
        }

//        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void CampaignSearch() throws InterruptedException{
        driver.get("https://smartqc.io/account/campaigns");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement Search = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='myInput']")));
        Search.sendKeys("IBM-Pune");
        Thread.sleep(10000);
        System.out.println("CampaignSearch method executed.");
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void testBilling() throws InterruptedException{
        driver.get("https://smartqc.io/account/billing");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement billingButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Billing']")));
        billingButton.click();
        Thread.sleep(2000);
        WebElement changePlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[@class='ms-2 btn btn-sm btn-orange']")));
        changePlan.click();
        Thread.sleep(2000);
        System.out.println("testBilling method executed.");
        WebElement BackButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[normalize-space()='Back to Billing']")));
        BackButton.click();
        Thread.sleep(2000);
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void Advanced() throws InterruptedException {
        driver.get("https://smartqc.io/account/billing");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement billingButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Billing']")));
        billingButton.click();
        Thread.sleep(2000);
        WebElement changePlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[@class='ms-2 btn btn-sm btn-orange']")));
        changePlan.click();
        Thread.sleep(2000);
        WebElement AdvancedBuyNow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='btn-sm btn btn-orange px-4 button-hover']")));
        AdvancedBuyNow.click();
        Thread.sleep(5000);
        System.out.println("Advanced plan Activated.");
        WebElement PlanBack = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //span[@class='text-muted fw-bold']")));
        PlanBack.click();
        Thread.sleep(2000);
        WebElement BackButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[normalize-space()='Back to Billing']")));
        BackButton.click();
        Thread.sleep(2000);
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void Professional() throws InterruptedException{
        driver.get("https://smartqc.io/account/billing");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement billingButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Billing']")));
        billingButton.click();
        Thread.sleep(2000);
        WebElement changePlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[@class='ms-2 btn btn-sm btn-orange']")));
        changePlan.click();
        Thread.sleep(2000);
        WebElement ProfessionalBuyNow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'d-flex align-items-center')]//div[2]//div[1]//div[4]//a[1]")));
        ProfessionalBuyNow.click();
        Thread.sleep(5000);
        System.out.println("Professional plan Activated.");
        WebElement PlanBack = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //span[@class='text-muted fw-bold']")));
        PlanBack.click();
        Thread.sleep(2000);
        WebElement BackButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[normalize-space()='Back to Billing']")));
        BackButton.click();
        Thread.sleep(2000);
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void Starter() throws InterruptedException {
        driver.get("https://smartqc.io/account/billing");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement billingButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Billing']")));
        billingButton.click();
        Thread.sleep(2000);
        WebElement changePlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[@class='ms-2 btn btn-sm btn-orange']")));
        changePlan.click();
        Thread.sleep(2000);
        WebElement StarterBuyNow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//body//div[@id='root']//div[contains(@class,'row')]//div[contains(@class,'row')]//div[1]//div[1]//div[4]//a[1]")));
        StarterBuyNow.click();
        Thread.sleep(5000);
        System.out.println("Starter plan Activated.");
        WebElement PlanBack = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //span[@class='text-muted fw-bold']")));
        PlanBack.click();
        Thread.sleep(2000);
        WebElement BackButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(" //a[normalize-space()='Back to Billing']")));
        BackButton.click();
        Thread.sleep(2000);
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void InvoiceHistory() throws InterruptedException {
        driver.get("https://smartqc.io/account/billing");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

        WebElement viewAll = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='View All']")));

        // Scroll down to the View All button
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 1000);");
        // Wait for scroll to complete
        Thread.sleep(2000); // Adjust the sleep time if necessary

        viewAll.click();
        Thread.sleep(10000); // Wait for click action to complete
        System.out.println("Show Invoice History");
        shouldLogout = true; // Set the flag to true after Subscribers test
    }

@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void Settings() throws InterruptedException  {
        driver.get("https://smartqc.io/account/dashboard");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement SettingsButtton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Settings']")));
        SettingsButtton.click();
        Thread.sleep(10000); // Adjust the sleep time if necessary
        System.out.println("Settings method executed.");
        shouldLogout = true; // Set the flag to true after Subscribers test


    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void ChangeName() throws InterruptedException {
        driver.get("https://smartqc.io/account/profile");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement EditButtton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Edit']")));
        EditButtton.click();
        Thread.sleep(2000); // Adjust the sleep time if necessary
        WebElement Firstname = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@placeholder,'First Name')]")));
        Firstname.sendKeys("Aqdas");
        Thread.sleep(2000); // Adjust the sleep time if necessary
        WebElement Lastname = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Last Name']")));
        Lastname.sendKeys("Bamne");
        Thread.sleep(2000); // Adjust the sleep time if necessary
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,100);");
        Thread.sleep(2000); // Adjust the sleep time if necessary
        WebElement SaveButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[normalize-space()='Save']")));
        SaveButton.click();
        Thread.sleep(2000); // Adjust the sleep time if necessary
        System.out.println("Name changed Successfully");
        shouldLogout = true; // Set the flag to true after Subscribers test

    }
@Test
//    @Test(dependsOnMethods = "testLoginOrSignUp")
    public void ChangePassword() throws InterruptedException  {
        driver.get("https://smartqc.io/account/profile");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement changePasswordButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Change Password']")));
        changePasswordButton.click();
        Thread.sleep(2000); // Adjust the sleep time if necessary

        WebElement newPassword = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='New Password']")));
        newPassword.sendKeys("Aqdas@123");
        Thread.sleep(2000); // Adjust the sleep time if necessary

        WebElement confirmPassword = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Confirm New Password']")));
        confirmPassword.sendKeys("Aqdas@123");
        Thread.sleep(2000); // Adjust the sleep time if necessary
        // Scroll to the Reset Button
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 100);");
        WebElement resetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Reset Password']")));
        // Click the reset button
        resetButton.click();
        Thread.sleep(20000); // Adjust the sleep time if necessary
//        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,0);");
        System.out.println("Password changed Successfully");
        shouldLogout = true; // Set the flag to true after Subscribers test

    }

    @AfterMethod
    public void logout() {
        if (shouldLogout) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement logoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Sign out']")));
                logoutButton.click();
                Thread.sleep(2000);
                WebElement Confirmlogout = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[normalize-space()='Yes']")));
                Confirmlogout.click();
                wait.until(ExpectedConditions.urlToBe("https://smartqc.io/"));
                System.out.println("User logged out.");
            } catch (Exception e) {
                System.out.println("Logout failed: " + e.getMessage());
            }
            shouldLogout = false; // Reset the flag after logging out
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}