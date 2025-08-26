import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Custom TestNG listener for SmartQC test automation
 * Provides enhanced reporting and screenshot capture on test failures
 */
public class SmartqcTestListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        Reporter.log("==========================================================", true);
        Reporter.log("Starting test: " + result.getMethod().getMethodName(), true);
        Reporter.log("==========================================================", true);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Reporter.log("Test PASSED: " + result.getMethod().getMethodName(), true);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Reporter.log("Test FAILED: " + result.getMethod().getMethodName(), true);
        
        // Capture screenshot on failure
        Object currentClass = result.getInstance();
        WebDriver driver = ((Smartqc) currentClass).getDriver();
        
        if (driver != null) {
            captureScreenshot(driver, result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Reporter.log("Test SKIPPED: " + result.getMethod().getMethodName(), true);
    }

    @Override
    public void onStart(ITestContext context) {
        Reporter.log("==========================================================", true);
        Reporter.log("Starting test suite: " + context.getName(), true);
        Reporter.log("==========================================================", true);
    }

    @Override
    public void onFinish(ITestContext context) {
        Reporter.log("==========================================================", true);
        Reporter.log("Finished test suite: " + context.getName(), true);
        Reporter.log("Total tests run: " + context.getAllTestMethods().length, true);
        Reporter.log("Passed tests: " + context.getPassedTests().size(), true);
        Reporter.log("Failed tests: " + context.getFailedTests().size(), true);
        Reporter.log("Skipped tests: " + context.getSkippedTests().size(), true);
        Reporter.log("==========================================================", true);
    }
    
    /**
     * Captures screenshot and saves it to the screenshots directory
     * 
     * @param driver WebDriver instance
     * @param testName Name of the test method
     */
    private void captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File("screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdir();
            }
            
            // Generate timestamp for unique filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = dateFormat.format(new Date());
            
            // Take screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            // Save screenshot
            String destination = "screenshots/" + testName + "-" + timestamp + ".png";
            File target = new File(destination);
            FileUtils.copyFile(source, target);
            
            // Add screenshot to report
            Reporter.log("<a href='" + target.getAbsolutePath() + "'><img src='" + 
                    target.getAbsolutePath() + "' height='100' width='100'/></a>", true);
            Reporter.log("Screenshot saved to: " + target.getAbsolutePath(), true);
            
        } catch (IOException e) {
            Reporter.log("Failed to capture screenshot: " + e.getMessage(), true);
        }
    }
}