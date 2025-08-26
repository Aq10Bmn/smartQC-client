# Issue Fix: FileUtils Resolution Problem

## Issue Description

The test execution was failing with the following error:

```
java.lang.Error: Unresolved compilation problem:
 FileUtils cannot be resolved
 at SmartqcTestListener.captureScreenshot(SmartqcTestListener.java:94)
```

## Root Cause

The error occurred because the Apache Commons IO library, which provides the `FileUtils` class, was not included in the project dependencies. This library is required for file operations such as saving screenshots during test execution.

## Solution

1. Added the Apache Commons IO dependency to the project's pom.xml file:

```xml
<!-- Apache Commons IO for file operations -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.16.1</version>
</dependency>
```

2. Verified that the dependency was correctly resolved by running:
   - `mvn clean compile test-compile`
   - `mvn clean install -DskipTests`

## Verification

After adding the dependency, the compilation was successful, indicating that the `FileUtils` class is now properly resolved.

## Additional Notes

- The `FileUtils` class is used in both the `SmartqcTestListener` and `Smartqc` classes for screenshot capture functionality.
- The version 2.16.1 of commons-io was chosen as it's the latest stable version available at the time of this fix.
- No code changes were required in the test classes themselves, as the implementation was correct but simply missing the required dependency.

## Next Steps

1. Run the tests to verify that the screenshot functionality works correctly
2. Consider adding other useful dependencies for enhanced reporting and testing capabilities
3. Update the project documentation to include information about the required dependencies