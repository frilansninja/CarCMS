package se.meltastudio.cms.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordResetTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String FRONTEND_URL = "http://localhost:5173";
    private static final String TEST_EMAIL = "admin@melta-studios.se";
    private static final String ORIGINAL_PASSWORD = "admin123";
    private static final String NEW_PASSWORD = "newpass123";

    @BeforeAll
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("WebDriver initialized successfully");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("WebDriver closed");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load forgot password page successfully")
    public void testForgotPasswordPageLoads() {
        driver.get(FRONTEND_URL + "/forgot-password");

        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h4")));

        // Verify we're on the forgot password page
        WebElement heading = driver.findElement(By.xpath("//h4[contains(text(), 'Glömt lösenord')]"));
        assertNotNull(heading, "Forgot password heading should be present");

        // Check for email input
        WebElement emailInput = driver.findElement(By.xpath("//input[@type='email']"));
        assertNotNull(emailInput, "Email input should be present");

        // Check for submit button
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        assertNotNull(submitButton, "Submit button should be present");

        System.out.println("Forgot password page loaded successfully with all required elements");
    }

    @Test
    @Order(2)
    @DisplayName("Should generate password reset link")
    public void testGenerateResetLink() throws InterruptedException {
        driver.get(FRONTEND_URL + "/forgot-password");

        // Wait for email input
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']"))
        );

        // Enter email
        emailInput.clear();
        emailInput.sendKeys(TEST_EMAIL);
        System.out.println("Entered email: " + TEST_EMAIL);

        // Click submit button
        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        submitButton.click();
        System.out.println("Clicked submit button");

        // Wait for reset link to appear
        WebElement resetLinkField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@value, 'reset-password')]"))
        );

        assertNotNull(resetLinkField, "Reset link field should be displayed");
        String resetLink = resetLinkField.getAttribute("value");
        assertNotNull(resetLink, "Reset link should not be null");
        assertTrue(resetLink.contains("/reset-password/"), "Reset link should contain /reset-password/ path");

        System.out.println("Reset link generated successfully: " + resetLink);

        // Verify success message
        WebElement successAlert = driver.findElement(By.xpath("//div[contains(@class, 'MuiAlert-standardSuccess')]"));
        assertNotNull(successAlert, "Success alert should be displayed");

        // Verify copy button is present
        WebElement copyButton = driver.findElement(By.xpath("//button[contains(text(), 'Kopiera länk')]"));
        assertNotNull(copyButton, "Copy button should be present");
    }

    @Test
    @Order(3)
    @DisplayName("Should complete full password reset flow")
    public void testCompletePasswordResetFlow() throws InterruptedException {
        // Step 1: Request password reset
        driver.get(FRONTEND_URL + "/forgot-password");

        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']"))
        );
        emailInput.clear();
        emailInput.sendKeys(TEST_EMAIL);

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        submitButton.click();

        // Step 2: Get the reset link
        WebElement resetLinkField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@value, 'reset-password')]"))
        );
        String resetLink = resetLinkField.getAttribute("value");
        System.out.println("Got reset link: " + resetLink);

        // Extract token from URL
        String token = resetLink.substring(resetLink.lastIndexOf("/") + 1);
        System.out.println("Extracted token: " + token);

        // Step 3: Navigate to reset password page
        driver.get(FRONTEND_URL + "/reset-password/" + token);

        // Wait for reset password form
        WebElement newPasswordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(text(), 'Nytt lösenord')]/following-sibling::div//input"))
        );
        WebElement confirmPasswordInput = driver.findElement(By.xpath("//label[contains(text(), 'Bekräfta lösenord')]/following-sibling::div//input"));
        WebElement resetButton = driver.findElement(By.xpath("//button[contains(text(), 'Återställ lösenord')]"));

        // Step 4: Enter new password
        newPasswordInput.clear();
        newPasswordInput.sendKeys(NEW_PASSWORD);
        System.out.println("Entered new password");

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(NEW_PASSWORD);
        System.out.println("Confirmed new password");

        // Step 5: Submit password reset
        resetButton.click();
        System.out.println("Clicked reset password button");

        // Step 6: Wait for success message
        WebElement successAlert = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'MuiAlert-standardSuccess')]"))
        );
        assertNotNull(successAlert, "Success alert should be displayed");
        assertTrue(successAlert.getText().contains("återställts"), "Success message should indicate password was reset");

        System.out.println("Password reset successful!");

        // Step 7: Wait for redirect to login (or manually navigate)
        Thread.sleep(3500); // Wait for auto-redirect (3 seconds + buffer)

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after redirect: " + currentUrl);

        // Step 8: Login with new password
        driver.get(FRONTEND_URL + "/login");

        WebElement usernameInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Användarnamn']"))
        );
        WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Lösenord']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Logga in')]"));

        usernameInput.clear();
        usernameInput.sendKeys(TEST_EMAIL);

        passwordInput.clear();
        passwordInput.sendKeys(NEW_PASSWORD);

        loginButton.click();
        System.out.println("Logging in with new password");

        // Step 9: Verify successful login
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        String loginUrl = driver.getCurrentUrl();
        assertTrue(loginUrl.contains("/dashboard"),
            "Should navigate to dashboard after successful login. Current URL: " + loginUrl);

        // Verify access token is stored
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String accessToken = (String) js.executeScript("return localStorage.getItem('accessToken');");
        assertNotNull(accessToken, "Access token should be stored in localStorage");
        assertTrue(accessToken.startsWith("eyJ"), "Access token should be a valid JWT token");

        System.out.println("Successfully logged in with new password!");
        System.out.println("Full password reset flow completed successfully!");
    }

    @Test
    @Order(4)
    @DisplayName("Should show error for password mismatch")
    public void testPasswordMismatchValidation() throws InterruptedException {
        // First get a reset token
        driver.get(FRONTEND_URL + "/forgot-password");

        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']"))
        );
        emailInput.clear();
        emailInput.sendKeys(TEST_EMAIL);

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        submitButton.click();

        WebElement resetLinkField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@value, 'reset-password')]"))
        );
        String resetLink = resetLinkField.getAttribute("value");
        String token = resetLink.substring(resetLink.lastIndexOf("/") + 1);

        // Navigate to reset password page
        driver.get(FRONTEND_URL + "/reset-password/" + token);

        // Enter mismatched passwords
        WebElement newPasswordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(text(), 'Nytt lösenord')]/following-sibling::div//input"))
        );
        WebElement confirmPasswordInput = driver.findElement(By.xpath("//label[contains(text(), 'Bekräfta lösenord')]/following-sibling::div//input"));
        WebElement resetButton = driver.findElement(By.xpath("//button[contains(text(), 'Återställ lösenord')]"));

        newPasswordInput.clear();
        newPasswordInput.sendKeys("password123");

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("differentpass");

        resetButton.click();

        // Verify error message appears
        WebElement errorAlert = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'MuiAlert-standardError')]"))
        );
        assertNotNull(errorAlert, "Error alert should be displayed");
        assertTrue(errorAlert.getText().contains("matchar inte"), "Error should mention passwords don't match");

        System.out.println("Password mismatch validation working correctly");
    }

    @Test
    @Order(5)
    @DisplayName("Should show error for password too short")
    public void testPasswordLengthValidation() throws InterruptedException {
        // First get a reset token
        driver.get(FRONTEND_URL + "/forgot-password");

        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']"))
        );
        emailInput.clear();
        emailInput.sendKeys(TEST_EMAIL);

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        submitButton.click();

        WebElement resetLinkField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@value, 'reset-password')]"))
        );
        String resetLink = resetLinkField.getAttribute("value");
        String token = resetLink.substring(resetLink.lastIndexOf("/") + 1);

        // Navigate to reset password page
        driver.get(FRONTEND_URL + "/reset-password/" + token);

        // Enter password that's too short
        WebElement newPasswordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(text(), 'Nytt lösenord')]/following-sibling::div//input"))
        );
        WebElement confirmPasswordInput = driver.findElement(By.xpath("//label[contains(text(), 'Bekräfta lösenord')]/following-sibling::div//input"));
        WebElement resetButton = driver.findElement(By.xpath("//button[contains(text(), 'Återställ lösenord')]"));

        newPasswordInput.clear();
        newPasswordInput.sendKeys("short");

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("short");

        resetButton.click();

        // Verify error message appears
        WebElement errorAlert = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'MuiAlert-standardError')]"))
        );
        assertNotNull(errorAlert, "Error alert should be displayed");
        assertTrue(errorAlert.getText().contains("8 tecken"), "Error should mention minimum 8 characters");

        System.out.println("Password length validation working correctly");
    }

    @Test
    @Order(6)
    @DisplayName("Should restore original password for future tests")
    public void testRestoreOriginalPassword() throws InterruptedException {
        // Get a reset token
        driver.get(FRONTEND_URL + "/forgot-password");

        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']"))
        );
        emailInput.clear();
        emailInput.sendKeys(TEST_EMAIL);

        WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Generera återställningslänk')]"));
        submitButton.click();

        WebElement resetLinkField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@value, 'reset-password')]"))
        );
        String resetLink = resetLinkField.getAttribute("value");
        String token = resetLink.substring(resetLink.lastIndexOf("/") + 1);

        // Navigate to reset password page
        driver.get(FRONTEND_URL + "/reset-password/" + token);

        // Reset back to original password
        WebElement newPasswordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(text(), 'Nytt lösenord')]/following-sibling::div//input"))
        );
        WebElement confirmPasswordInput = driver.findElement(By.xpath("//label[contains(text(), 'Bekräfta lösenord')]/following-sibling::div//input"));
        WebElement resetButton = driver.findElement(By.xpath("//button[contains(text(), 'Återställ lösenord')]"));

        newPasswordInput.clear();
        newPasswordInput.sendKeys(ORIGINAL_PASSWORD);

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(ORIGINAL_PASSWORD);

        resetButton.click();

        // Wait for success
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'MuiAlert-standardSuccess')]"))
        );

        System.out.println("Password restored to original: " + ORIGINAL_PASSWORD);
    }
}
