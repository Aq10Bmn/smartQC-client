# SmartQC Test Automation Suite

This project contains automated tests for the SmartQC web application using Selenium WebDriver and TestNG.

## Features

- Automated login/signup
- Campaign creation and management
- File import testing
- Billing and plan management
- User profile management
- Comprehensive test reporting
- Screenshot capture on failures
- Detailed logging

## Prerequisites

- Java JDK 8 or higher
- Maven
- Chrome browser
- ChromeDriver (matching your Chrome version)

## Project Structure

```
smartQC-client/
├── src/
│   └── test/
│       └── java/
│           ├── Smartqc.java           # Main test class
│           └── SmartqcTestListener.java # TestNG listener for reporting
├── logs/                              # Test execution logs
├── screenshots/                       # Screenshots captured during test execution
├── reports/                           # Test execution reports
├── testng.xml                         # TestNG configuration
└── README.md                          # This file
```

## Running the Tests

### Using Maven

```bash
mvn clean test
```

### Using TestNG directly

```bash
java -cp <classpath> org.testng.TestNG testng.xml
```

## Test Reports

After test execution, reports can be found in:

- TestNG HTML reports: `target/surefire-reports/index.html`
- Custom summary reports: `reports/test-summary-*.txt`
- Screenshots: `screenshots/`
- Logs: `logs/smartqc-*.log`

## Configuration

Test configuration is stored in the `Smartqc.java` file:

- `BASE_URL`: Base URL of the SmartQC application
- `CHROME_DRIVER_PATH`: Path to the ChromeDriver executable
- `TEST_FILE_PATH`: Path to the test file for import testing
- Test credentials and other test data

## Troubleshooting

If tests fail, check:

1. ChromeDriver version matches your Chrome browser version
2. Test credentials are valid
3. Application URLs are correct
4. Check screenshots in the `screenshots` directory for visual debugging
5. Review logs in the `logs` directory for detailed error information

## Contributing

1. Follow the existing code style
2. Add proper logging and error handling
3. Include screenshots for visual verification
4. Update documentation as needed